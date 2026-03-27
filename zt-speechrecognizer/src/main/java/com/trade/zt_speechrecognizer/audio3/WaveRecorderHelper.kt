package com.trade.zt_speechrecognizer.audio3

import android.content.Context
import android.media.AudioFormat
import android.os.Environment
import android.os.Handler
import android.os.Looper
import com.github.squti.androidwaverecorder.WaveRecorder
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 录音工具类 (基于 WaveRecorder)
 * 提供录音开始、停止、暂停、恢复、计时回调、文件路径回调功能
 * 统一使用 Callback 接口管理回调
 */
class WaveRecorderHelper(private val context: Context, private val callback: Callback) {

    private var waveRecorder: WaveRecorder? = null
    private var isRecording = false
    private var isPaused = false
    private var currentFilePath: String = ""
    private var startTime = 0L
    private var totalPausedDuration = 0L
    private var pauseTime = 0L

    // 主线程 Handler，确保回调在主线程执行
    private val mainHandler = Handler(Looper.getMainLooper())

    /**
     * 录音状态回调接口
     */
    interface Callback {
        /**
         * 录音时长更新回调
         * @param millis 当前录音时长（毫秒）
         * @param formattedTime 格式化后的时间字符串 (mm:ss)
         */
        fun onTimerUpdate(millis: Long, formattedTime: String)

        /**
         * 录音开始回调
         */
        fun onRecordingStart()

        /**
         * 录音停止回调
         * @param filePath 录音文件保存的绝对路径
         */
        fun onRecordingStop(filePath: String)

        /**
         * 录音暂停回调
         */
        fun onRecordingPause()

        /**
         * 录音恢复回调
         */
        fun onRecordingResume()

        /**
         * 错误回调
         * @param message 错误信息描述
         */
        fun onError(message: String)

        /**
         * 音量振幅变化回调
         * @param amplitude 当前音量振幅值
         */
        fun onAmplitudeChange(amplitude: Int)
    }

    // 计时任务
    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isRecording && !isPaused) {
                val millis = System.currentTimeMillis() - startTime - totalPausedDuration
                val seconds = (millis / 1000) % 60
                val minutes = (millis / (1000 * 60)) % 60
                val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                
                callback.onTimerUpdate(millis, formattedTime)
                
                mainHandler.postDelayed(this, 100) // 0.1秒刷新一次
            }
        }
    }

    /**
     * 开始录音
     * @return 是否成功启动
     */
    fun startRecording(): Boolean {
        if (isRecording) return false

        try {
            // 1. 生成文件路径 (Music 目录)
            val fileName = "Record_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.wav"
            val musicDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            if (musicDir != null && !musicDir.exists()) {
                musicDir.mkdirs()
            }
            val file = File(musicDir, fileName)
            currentFilePath = file.absolutePath

            // 2. 配置 WaveRecorder
            waveRecorder = WaveRecorder(currentFilePath).apply {
                noiseSuppressorActive = true
                waveConfig.sampleRate = 44100
                waveConfig.audioEncoding = AudioFormat.ENCODING_PCM_16BIT
                
                // 转发振幅回调
                onAmplitudeListener = { amplitude ->
                    mainHandler.post {
                        callback.onAmplitudeChange(amplitude)
                    }
                }
            }

            // 3. 启动
            waveRecorder?.startRecording()
            isRecording = true
            isPaused = false
            startTime = System.currentTimeMillis()
            totalPausedDuration = 0
            
            // 启动计时
            mainHandler.post(timerRunnable)
            
            callback.onRecordingStart()
            return true

        } catch (e: Exception) {
            e.printStackTrace()
            callback.onError("启动录音失败: ${e.message}")
            return false
        }
    }

    /**
     * 暂停录音
     */
    fun pauseRecording() {
        if (!isRecording || isPaused) return
        
        try {
            waveRecorder?.pauseRecording()
            isPaused = true
            pauseTime = System.currentTimeMillis()
            mainHandler.removeCallbacks(timerRunnable)
            callback.onRecordingPause()
        } catch (e: Exception) {
            e.printStackTrace()
            callback.onError("暂停录音失败: ${e.message}")
        }
    }

    /**
     * 恢复录音
     */
    fun resumeRecording() {
        if (!isRecording || !isPaused) return
        
        try {
            waveRecorder?.resumeRecording()
            isPaused = false
            totalPausedDuration += (System.currentTimeMillis() - pauseTime)
            mainHandler.post(timerRunnable)
            callback.onRecordingResume()
        } catch (e: Exception) {
            e.printStackTrace()
            callback.onError("恢复录音失败: ${e.message}")
        }
    }

    /**
     * 停止录音
     * @return 录音文件路径，如果未在录音则返回 null
     */
    fun stopRecording(): String? {
        if (!isRecording) return null

        try {
            waveRecorder?.stopRecording()
            isRecording = false
            isPaused = false
            mainHandler.removeCallbacks(timerRunnable)
            
            callback.onRecordingStop(currentFilePath)
            return currentFilePath

        } catch (e: Exception) {
            e.printStackTrace()
            callback.onError("停止录音失败: ${e.message}")
            return null
        }
    }

    /**
     * 取消录音 (停止并删除文件)
     */
    fun cancelRecording() {
        if (isRecording) {
            stopRecording()
            val file = File(currentFilePath)
            if (file.exists()) {
                file.delete()
            }
        }
    }

    /**
     * 是否正在录音 (包括暂停状态)
     */
    fun isRecording(): Boolean = isRecording

    /**
     * 是否处于暂停状态
     */
    fun isPaused(): Boolean = isPaused

    /**
     * 获取当前录音文件路径
     */
    fun getCurrentFilePath(): String = currentFilePath
}
