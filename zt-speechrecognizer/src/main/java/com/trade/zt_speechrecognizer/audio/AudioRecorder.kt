package com.trade.zt_speechrecognizer.audio

/**
 * @author: zeting
 * @date: 2026/2/10
 *
 */

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs

class AudioRecorder(
    private val sampleRate: Int = 44100,
    private val discardDuration: Int = 150, // 丢弃的毫秒数
    private val onRecordingStart: ((File) -> Unit)? = null,
    private val onRecordingStop: ((File) -> Unit)? = null,
    private val onError: ((String) -> Unit)? = null,
    private val onAudioData: ((ShortArray) -> Unit)? = null
) {

    companion object {
        private const val TAG = "AudioRecorder"
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private const val BUFFER_SIZE_MULTIPLIER = 4 // 缓冲区倍数，防止欠载
    }

    private var audioRecord: AudioRecord? = null
    private var recordingThread: Thread? = null
    private var isRecording = false
    private var wavFileWriter: WavFileWriter? = null
    private var outputFile: File? = null
    private var executor: ExecutorService = Executors.newSingleThreadExecutor()

    private var dcRemover = DCRemover()
    private var highPassFilter = HighPassFilter(30f, sampleRate.toFloat()) // 30Hz高通滤波

    /**
     * 开始录音
     * @return 是否成功开始
     */
    fun startRecording(): Boolean {
        if (isRecording) {
            Log.w(TAG, "Already recording")
            return false
        }

        return try {
            // 1. 创建输出文件
            outputFile = createAudioFile()

            // 2. 初始化WAV写入器
            wavFileWriter = WavFileWriter().apply {
                open(outputFile!!.absolutePath, sampleRate, 1, 16)
            }

            // 3. 计算缓冲区大小
            val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, CHANNEL_CONFIG, AUDIO_FORMAT)
            if (minBufferSize == AudioRecord.ERROR || minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
                throw IllegalStateException("无法获取有效的缓冲区大小")
            }

            val bufferSize = minBufferSize * BUFFER_SIZE_MULTIPLIER

            // 4. 创建AudioRecord
            val audioSource = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaRecorder.AudioSource.VOICE_RECOGNITION // 更好的音质，适合语音
            } else {
                MediaRecorder.AudioSource.MIC
            }

            audioRecord = AudioRecord(
                audioSource,
                sampleRate,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize
            ).apply {
                // 5. 开始录音（但不立即处理数据）
                startRecording()
            }

            // 6. 开始录音线程
            isRecording = true
            startRecordingThread()

            onRecordingStart?.invoke(outputFile!!)
            Log.d(TAG, "Recording started: ${outputFile!!.absolutePath}")
            true

        } catch (e: Exception) {
            Log.e(TAG, "Failed to start recording", e)
            onError?.invoke(e.message ?: "Unknown error")
            cleanup()
            false
        }
    }

    /**
     * 停止录音
     */
    fun stopRecording() {
        if (!isRecording) return

        isRecording = false

        // 等待录音线程结束
        recordingThread?.join(1000)
        recordingThread = null

        // 停止AudioRecord
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping AudioRecord", e)
        } finally {
            audioRecord = null
        }

        // 关闭WAV文件写入器
        wavFileWriter?.close()
        wavFileWriter = null

        // 回调
        outputFile?.let { file ->
            if (file.exists() && file.length() > 0) {
                onRecordingStop?.invoke(file)
                Log.d(TAG, "Recording stopped, file size: ${file.length()} bytes")
            } else {
                onError?.invoke("录音文件为空或不存在")
                file.delete() // 删除无效文件
            }
        }

        outputFile = null
    }

    /**
     * 创建录音文件
     */
    private fun createAudioFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "RECORDING_${timeStamp}.wav"

        val storageDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ 使用应用专属目录
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        } else {
            File(Environment.getExternalStorageDirectory(), "AudioRecordDemo")
        }

        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        return File(storageDir, fileName)
    }

    /**
     * 启动录音处理线程
     */
    private fun startRecordingThread() {
        recordingThread = Thread {
            processAudioRecording()
        }.apply {
            priority = Thread.MAX_PRIORITY // 提高线程优先级，减少丢帧
            start()
        }
    }

    /**
     * 处理录音数据（核心逻辑）
     */
    private fun processAudioRecording() {
        val audioRecord = this.audioRecord ?: return
        val wavWriter = this.wavFileWriter ?: return

        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, CHANNEL_CONFIG, AUDIO_FORMAT)
        val buffer = ShortArray(bufferSize / 2) // 16位 = 2字节，所以除以2

        // 关键：丢弃初始的音频数据
        discardInitialAudio(audioRecord, buffer)

        Log.d(TAG, "Initial audio discarded, starting real processing...")

        // 开始真正的录音处理
        while (isRecording && audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
            try {
                // 读取音频数据
                val samplesRead = audioRecord.read(buffer, 0, buffer.size, AudioRecord.READ_BLOCKING)

                when {
                    samplesRead == AudioRecord.ERROR_INVALID_OPERATION -> {
                        Log.e(TAG, "ERROR_INVALID_OPERATION")
                        break
                    }
                    samplesRead == AudioRecord.ERROR_BAD_VALUE -> {
                        Log.e(TAG, "ERROR_BAD_VALUE")
                        break
                    }
                    samplesRead == AudioRecord.ERROR -> {
                        Log.e(TAG, "ERROR reading audio")
                        break
                    }
                    samplesRead > 0 -> {
                        // 应用DC偏移去除
                        val cleanedBuffer = dcRemover.removeDCOffset(buffer.copyOf(samplesRead))

                        // 可选：应用高通滤波（进一步去除低频噪音）
                        // val filteredBuffer = highPassFilter.process(cleanedBuffer)

                        // 写入WAV文件
                        wavWriter.write(cleanedBuffer)

                        // 回调音频数据（用于波形显示）
                        onAudioData?.invoke(cleanedBuffer)

                        // 简单的静音检测（可选）
                        if (isSilence(cleanedBuffer)) {
                            Log.w(TAG, "Silence detected")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in recording thread", e)
                break
            }
        }
    }

    /**
     * 丢弃初始的音频数据（消除杂音的关键）
     */
    private fun discardInitialAudio(audioRecord: AudioRecord, buffer: ShortArray) {
        val samplesToDiscard = (sampleRate * discardDuration / 1000).toInt()
        var discardedSamples = 0

        Log.d(TAG, "Discarding initial $discardDuration ms ($samplesToDiscard samples)")

        while (isRecording && discardedSamples < samplesToDiscard) {
            val samplesRead = audioRecord.read(buffer, 0, buffer.size, AudioRecord.READ_BLOCKING)

            if (samplesRead > 0) {
                discardedSamples += samplesRead
                Log.d(TAG, "Discarded $samplesRead samples (total: $discardedSamples/$samplesToDiscard)")

                // 在丢弃过程中也可以更新UI显示
                onAudioData?.invoke(buffer.copyOf(samplesRead))
            }
        }

        // 再额外丢弃一小段数据，确保完全稳定
        Thread.sleep(20)
        audioRecord.read(buffer, 0, buffer.size, AudioRecord.READ_BLOCKING)

        Log.d(TAG, "Initial audio discard completed")
    }

    /**
     * 简单的静音检测
     */
    private fun isSilence(buffer: ShortArray, threshold: Short = 1000): Boolean {
        var sum = 0L
        for (sample in buffer) {
            sum += abs(sample.toLong())
        }
        val average = sum / buffer.size
        return average < threshold
    }

    /**
     * 清理资源
     */
    private fun cleanup() {
        isRecording = false
        audioRecord?.release()
        audioRecord = null
        wavFileWriter?.close()
        wavFileWriter = null
        recordingThread = null
    }

    /**
     * 获取当前录音状态
     */
    fun isRecording(): Boolean = isRecording

    /**
     * 获取输出文件
     */
    fun getOutputFile(): File? = outputFile

    /**
     * 释放资源
     */
    fun release() {
        stopRecording()
        executor.shutdown()
    }
}

/**
 * DC偏移去除器
 */
class DCRemover {
    private var runningAverage = 0.0
    private val alpha = 0.01 // 平滑系数

    fun removeDCOffset(input: ShortArray): ShortArray {
        val output = ShortArray(input.size)

        for (i in input.indices) {
            // 计算直流分量（滑动平均）
            runningAverage = alpha * input[i] + (1 - alpha) * runningAverage

            // 去除直流偏移
            val corrected = input[i] - runningAverage.toInt()

            // 限制在有效范围内
            output[i] = when {
                corrected > Short.MAX_VALUE -> Short.MAX_VALUE
                corrected < Short.MIN_VALUE -> Short.MIN_VALUE
                else -> corrected.toShort()
            }
        }

        return output
    }
}

/**
 * 高通滤波器
 */
class HighPassFilter(private val cutoffFrequency: Float, private val sampleRate: Float) {
    private var prevInput = 0f
    private var prevOutput = 0f
    private val dt = 1.0f / sampleRate
    private val rc = 1.0f / (2 * Math.PI.toFloat() * cutoffFrequency)
    private val alpha = rc / (rc + dt)

    fun process(input: Float): Float {
        val output = alpha * (prevOutput + input - prevInput)
        prevInput = input
        prevOutput = output
        return output
    }

    fun process(buffer: ShortArray): FloatArray {
        val result = FloatArray(buffer.size)
        for (i in buffer.indices) {
            result[i] = process(buffer[i].toFloat() / Short.MAX_VALUE)
        }
        return result
    }
}