package com.trade.zt_speechrecognizer.audio

/**
 * @author: zeting
 * @date: 2026/2/10
 * @description: 音频录制管理器
 * 功能：
 * 1. 封装 AudioRecord 进行音频采集
 * 2. 支持 WAV 文件写入
 * 3. 支持去直流偏移 (DC Offset Removal)
 * 4. 支持静音检测
 * 5. 自动丢弃录音初期的不稳定数据
 */

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresPermission
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs

class AudioRecorder(
    private val sampleRate: Int = 44100, // 采样率，默认 44.1kHz
    private val discardDuration: Int = 150, // 丢弃录音开始前的数据时长(ms)，用于消除爆音
    private val onRecordingStart: ((File) -> Unit)? = null, // 录音开始回调
    private val onRecordingStop: ((File) -> Unit)? = null, // 录音结束回调
    private val onError: ((String) -> Unit)? = null, // 错误回调
    private val onAudioData: ((ShortArray) -> Unit)? = null // 实时音频数据回调
) {

    companion object {
        private const val TAG = "AudioRecorder"
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO // 单声道
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT // 16位PCM
        private const val BUFFER_SIZE_MULTIPLIER = 4 // 缓冲区大小倍数，防止缓冲区溢出
    }

    private var audioRecord: AudioRecord? = null
    private var recordingThread: Thread? = null
    private var isRecording = false
    private var wavFileWriter: WavFileWriter? = null
    private var outputFile: File? = null
    // 单线程池用于处理后台任务
    private var executor: ExecutorService = Executors.newSingleThreadExecutor()

    // 音频处理工具
    private var dcRemover = DCRemover() // 直流偏移去除器
    private var highPassFilter = HighPassFilter(30f, sampleRate.toFloat()) // 30Hz高通滤波器，去除低频噪声

    /**
     * 开始录音
     * @return 是否成功启动录音
     */
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun startRecording(): Boolean {
        if (isRecording) {
            Log.w(TAG, "AudioRecorder: 正在录音中，请勿重复启动")
            return false
        }

        return try {
            // 1. 创建输出文件
            outputFile = createAudioFile()

            // 2. 初始化 WAV 文件写入器
            wavFileWriter = WavFileWriter().apply {
                open(outputFile!!.absolutePath, sampleRate, 1, 16)
            }

            // 3. 计算最小缓冲区大小
            val minBufferSize =
                AudioRecord.getMinBufferSize(sampleRate, CHANNEL_CONFIG, AUDIO_FORMAT)
            if (minBufferSize == AudioRecord.ERROR || minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
                throw IllegalStateException("无法获取有效的音频缓冲区大小")
            }

            // 扩大缓冲区以增加稳定性
            val bufferSize = minBufferSize * BUFFER_SIZE_MULTIPLIER

            // 4. 选择音频源
            val audioSource = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaRecorder.AudioSource.VOICE_RECOGNITION // Android 10+ 推荐用于语音识别/处理，可能会有系统级的降噪
            } else {
                MediaRecorder.AudioSource.MIC // 通用麦克风源
            }

            // 5. 创建 AudioRecord 实例
            audioRecord = AudioRecord(
                audioSource, sampleRate, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize
            ).apply {
                if (state != AudioRecord.STATE_INITIALIZED) {
                    throw IllegalStateException("AudioRecord 初始化失败")
                }
                // 开始采集 (此时数据还未读取)
                startRecording()
            }

            // 6. 启动数据读取线程
            isRecording = true
            startRecordingThread()

            onRecordingStart?.invoke(outputFile!!)
            Log.d(TAG, "Recording started: ${outputFile!!.absolutePath}")
            true

        } catch (e: Exception) {
            Log.e(TAG, "Failed to start recording", e)
            onError?.invoke(e.message ?: "启动录音失败")
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

        // 等待录音线程安全结束
        try {
            recordingThread?.join(1000)
        } catch (e: InterruptedException) {
            Log.e(TAG, "Interrupted while waiting for recording thread to stop", e)
        }
        recordingThread = null

        // 停止并释放 AudioRecord
        try {
            if (audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                audioRecord?.stop()
            }
            audioRecord?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping AudioRecord", e)
        } finally {
            audioRecord = null
        }

        // 关闭 WAV 文件写入器
        try {
            wavFileWriter?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing WavFileWriter", e)
        } finally {
            wavFileWriter = null
        }

        // 触发回调
        outputFile?.let { file ->
            if (file.exists() && file.length() > 0) {
                onRecordingStop?.invoke(file)
                Log.d(TAG, "Recording stopped, file size: ${file.length()} bytes")
            } else {
                onError?.invoke("录音文件为空或不存在")
                // 删除无效文件
                try {
                    file.delete()
                } catch (e: Exception) {
                    Log.e(TAG, "Error deleting empty file", e)
                }
            }
        }

        outputFile = null
    }

    /**
     * 创建录音文件
     * 根据 Android 版本选择合适的存储目录
     */
    private fun createAudioFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "RECORDING_${timeStamp}.wav"

        val storageDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ 使用公共 Music 目录 (Scoped Storage 友好)
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        } else {
            // 旧版本使用外部存储根目录下的自定义文件夹
            File(Environment.getExternalStorageDirectory(), "AudioRecordDemo")
        }

        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.e(TAG, "Failed to create directory: ${storageDir.absolutePath}")
            }
        }

        return File(storageDir, fileName)
    }

    /**
     * 启动高优先级的录音处理线程
     */
    private fun startRecordingThread() {
        recordingThread = Thread {
            processAudioRecording()
        }.apply {
            priority = Thread.MAX_PRIORITY // 提高线程优先级，减少因 CPU 调度导致的丢帧
            start()
        }
    }

    /**
     * 处理录音数据（核心逻辑）
     * 循环读取 AudioRecord 数据 -> 处理(去直流/滤波) -> 写入文件 -> 回调
     */
    private fun processAudioRecording() {
        val audioRecord = this.audioRecord ?: return
        val wavWriter = this.wavFileWriter ?: return

        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, CHANNEL_CONFIG, AUDIO_FORMAT)
        val buffer = ShortArray(bufferSize / 2) // Short 占 2 字节，所以长度为 bufferSize / 2

        // 关键步骤：丢弃初始的不稳定音频数据
        discardInitialAudio(audioRecord, buffer)

        Log.d(TAG, "Initial audio discarded, starting real processing...")

        // 开始真正的录音循环
        while (isRecording && audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
            try {
                // 读取音频数据到 buffer
                val samplesRead =
                    audioRecord.read(buffer, 0, buffer.size, AudioRecord.READ_BLOCKING)

                when {
                    samplesRead == AudioRecord.ERROR_INVALID_OPERATION -> {
                        Log.e(TAG, "AudioRecord error: ERROR_INVALID_OPERATION")
                        break
                    }

                    samplesRead == AudioRecord.ERROR_BAD_VALUE -> {
                        Log.e(TAG, "AudioRecord error: ERROR_BAD_VALUE")
                        break
                    }

                    samplesRead == AudioRecord.ERROR_DEAD_OBJECT -> {
                        Log.e(TAG, "AudioRecord error: ERROR_DEAD_OBJECT")
                        break
                    }

                    samplesRead == AudioRecord.ERROR -> {
                        Log.e(TAG, "AudioRecord error: ERROR")
                        break
                    }

                    samplesRead > 0 -> {
                        // 1. 去除直流偏移 (DC Offset)
                        val cleanedBuffer = dcRemover.removeDCOffset(buffer.copyOf(samplesRead))

                        // 2. (可选) 高通滤波 - 去除低频噪音
                        // 注意：HighPassFilter 目前处理 Float，如需启用需进行 Short <-> Float 转换
                        // val filteredBuffer = highPassFilter.process(cleanedBuffer)

                        // 3. 写入 WAV 文件
                        wavWriter.write(cleanedBuffer)

                        // 4. 回调音频数据（用于 UI 波形显示等）
                        onAudioData?.invoke(cleanedBuffer)

                        // 5. (可选) 简单的静音检测日志
                        if (isSilence(cleanedBuffer)) {
                            // Log.v(TAG, "Silence detected") // 避免刷屏，使用 Verbose 或减少频率
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
     * 丢弃初始的音频数据
     * 原因：AudioRecord 启动初期可能会有电流声或爆音
     */
    private fun discardInitialAudio(audioRecord: AudioRecord, buffer: ShortArray) {
        val samplesToDiscard = (sampleRate * discardDuration / 1000).toInt()
        var discardedSamples = 0

        Log.d(TAG, "Discarding initial $discardDuration ms ($samplesToDiscard samples)")

        while (isRecording && discardedSamples < samplesToDiscard) {
            val samplesRead = audioRecord.read(buffer, 0, buffer.size, AudioRecord.READ_BLOCKING)

            if (samplesRead > 0) {
                discardedSamples += samplesRead
                // 即使是丢弃的数据，也可以回调给 UI 用于显示波形动画（可选）
                onAudioData?.invoke(buffer.copyOf(samplesRead))
            } else if (samplesRead < 0) {
                 // 发生错误，退出丢弃循环
                 Log.e(TAG, "Error discarding audio: $samplesRead")
                 break
            }
        }

        // 额外丢弃一小段数据并休眠，确保硬件状态完全稳定
        try {
            Thread.sleep(20)
            audioRecord.read(buffer, 0, buffer.size, AudioRecord.READ_BLOCKING)
        } catch (e: InterruptedException) {
            // ignore
        }

        Log.d(TAG, "Initial audio discard completed")
    }

    /**
     * 简单的静音检测
     * @param threshold 静音阈值 (0-32767)
     */
    private fun isSilence(buffer: ShortArray, threshold: Short = 1000): Boolean {
        var sum = 0L
        for (sample in buffer) {
            sum += abs(sample.toLong())
        }
        val average = if (buffer.isNotEmpty()) sum / buffer.size else 0
        return average < threshold
    }

    /**
     * 清理资源
     */
    private fun cleanup() {
        isRecording = false
        try {
            audioRecord?.release()
        } catch (e: Exception) { e.printStackTrace() }
        audioRecord = null
        
        try {
            wavFileWriter?.close()
        } catch (e: Exception) { e.printStackTrace() }
        wavFileWriter = null
        
        recordingThread = null
    }

    /**
     * 获取当前是否正在录音
     */
    fun isRecording(): Boolean = isRecording

    /**
     * 获取当前输出文件
     */
    fun getOutputFile(): File? = outputFile

    /**
     * 彻底释放所有资源 (Activity 销毁时调用)
     */
    fun release() {
        stopRecording()
        executor.shutdown()
    }
}

/**
 * DC 偏移去除器 (DC Offset Remover)
 * 原理：使用一阶低通滤波器估算直流分量，然后从信号中减去它。
 * 用于消除麦克风硬件可能产生的直流偏置。
 */
class DCRemover {
    private var runningAverage = 0.0
    private val alpha = 0.01 // 平滑系数 (越小变化越慢，0.01 适合去除 DC)

    fun removeDCOffset(input: ShortArray): ShortArray {
        val output = ShortArray(input.size)

        for (i in input.indices) {
            // 计算直流分量（滑动平均）
            runningAverage = alpha * input[i] + (1 - alpha) * runningAverage

            // 去除直流偏移
            val corrected = input[i] - runningAverage.toInt()

            // 限制在 Short 的有效范围内 (防溢出)
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
 * 高通滤波器 (High Pass Filter)
 * 原理：RC 高通滤波器算法
 * 用途：去除低频噪音（如风声、呼吸声等）
 */
class HighPassFilter(private val cutoffFrequency: Float, private val sampleRate: Float) {
    private var prevInput = 0f
    private var prevOutput = 0f
    private val dt = 1.0f / sampleRate
    private val rc = 1.0f / (2 * Math.PI.toFloat() * cutoffFrequency)
    private val alpha = rc / (rc + dt)

    /**
     * 处理单个样本
     */
    fun process(input: Float): Float {
        val output = alpha * (prevOutput + input - prevInput)
        prevInput = input
        prevOutput = output
        return output
    }

    /**
     * 处理 Float 数组
     */
    fun process(buffer: ShortArray): FloatArray {
        val result = FloatArray(buffer.size)
        for (i in buffer.indices) {
            // 将 Short 归一化到 -1.0 ~ 1.0 范围处理
            result[i] = process(buffer[i].toFloat() / Short.MAX_VALUE)
        }
        return result
    }
}
