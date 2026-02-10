package com.trade.zt_speechrecognizer.audio3

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.squti.androidwaverecorder.WaveRecorder
import com.trade.zt_speechrecognizer.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 使用 WaveRecorder 库进行录音并存储到 Music 目录
 * 包含完整录音、播放、计时与状态显示功能
 */
class MyAudioV3 : AppCompatActivity() {

    // UI 组件
    private lateinit var tvStatus: TextView
    private lateinit var tvTimer: TextView
    private lateinit var tvFilePath: TextView
    private lateinit var btnStartRecord: Button
    private lateinit var btnStopRecord: Button
    private lateinit var btnPlayAudio: Button
    private lateinit var btnStopAudio: Button

    // 核心组件
    private var waveRecorder: WaveRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var currentFilePath: String = ""
    private var isRecording = false
    private var isPlaying = false

    // 计时器相关
    private var startTime = 0L
    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isRecording) {
                val millis = System.currentTimeMillis() - startTime
                updateTimerUI(millis)
                handler.postDelayed(this, 100) // 0.1秒刷新一次
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, MyAudioV3::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_audio_v3)

        initViews()
        checkPermissions()
    }

    private fun initViews() {
        tvStatus = findViewById(R.id.tvStatus)
        tvTimer = findViewById(R.id.tvTimer)
        tvFilePath = findViewById(R.id.tvFilePath)
        btnStartRecord = findViewById(R.id.btnStartRecord)
        btnStopRecord = findViewById(R.id.btnStopRecord)
        btnPlayAudio = findViewById(R.id.btnPlayAudio)
        btnStopAudio = findViewById(R.id.btnStopAudio)

        btnStartRecord.setOnClickListener {
            if (checkPermissions()) {
                startRecording()
            }
        }

        btnStopRecord.setOnClickListener {
            stopRecording()
        }

        btnPlayAudio.setOnClickListener {
            playAudio()
        }

        btnStopAudio.setOnClickListener {
            stopAudio()
        }
    }

    /**
     * 开始录音
     */
    private fun startRecording() {
        if (isRecording) return

        // 1. 生成文件路径 (存放在 App 专属的 Music 目录下，无需额外存储权限)
        // 注意：WaveRecorder 需要传入具体的文件路径字符串
        val fileName = "Record_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.wav"
        val musicDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        if (musicDir != null && !musicDir.exists()) {
            musicDir.mkdirs()
        }
        val file = File(musicDir, fileName)
        currentFilePath = file.absolutePath

        // 2. 初始化 WaveRecorder
        waveRecorder = WaveRecorder(currentFilePath).apply {
            noiseSuppressorActive = true // 开启降噪
            waveConfig.sampleRate = 44100 // 采样率
            waveConfig.audioEncoding = AudioFormat.ENCODING_PCM_16BIT // 16位 PCM
            
            // 可选：监听音量变化用于更新 UI (这里简单打印或不做处理)
            onAmplitudeListener = { amplitude ->
                // 可以根据 amplitude 更新波形图
            }
        }

        // 3. 开始录制
        try {
            waveRecorder?.startRecording()
            isRecording = true
            startTime = System.currentTimeMillis()
            handler.post(timerRunnable)

            // 更新 UI
            updateUIState(State.RECORDING)
            tvFilePath.text = "文件路径: $currentFilePath"
            Toast.makeText(this, "开始录音", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "录音启动失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 停止录音
     */
    private fun stopRecording() {
        if (!isRecording) return

        try {
            waveRecorder?.stopRecording()
            isRecording = false
            handler.removeCallbacks(timerRunnable)

            updateUIState(State.IDLE_HAS_FILE)
            Toast.makeText(this, "录音已保存", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 播放录音
     */
    private fun playAudio() {
        if (isPlaying) return
        if (currentFilePath.isEmpty() || !File(currentFilePath).exists()) {
            Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(currentFilePath)
                prepare()
                setOnCompletionListener {
                    stopAudio()
                    Toast.makeText(this@MyAudioV3, "播放结束", Toast.LENGTH_SHORT).show()
                }
                start()
            }
            isPlaying = true
            updateUIState(State.PLAYING)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "播放失败: ${e.message}", Toast.LENGTH_SHORT).show()
            stopAudio()
        }
    }

    /**
     * 停止播放
     */
    private fun stopAudio() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            isPlaying = false
            updateUIState(State.IDLE_HAS_FILE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 检查权限
     */
    private fun checkPermissions(): Boolean {
        val permissions = mutableListOf(Manifest.permission.RECORD_AUDIO)
        
        // Android 12 (S) 及以下如果需要写入外部存储 (非 App 专属目录) 才需要存储权限
        // 本例使用 getExternalFilesDir 不需要 WRITE_EXTERNAL_STORAGE，但为了保险起见还是检查一下
        // if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        //    permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        // }

        val notGranted = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGranted.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, notGranted.toTypedArray(), PERMISSION_REQUEST_CODE)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "权限已获取", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "需要录音权限才能使用", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * 更新计时器 UI
     */
    private fun updateTimerUI(millis: Long) {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val format = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        tvTimer.text = format
    }

    /**
     * UI 状态枚举
     */
    enum class State {
        IDLE_NO_FILE,
        IDLE_HAS_FILE,
        RECORDING,
        PLAYING
    }

    /**
     * 更新按钮和状态文字
     */
    private fun updateUIState(state: State) {
        when (state) {
            State.IDLE_NO_FILE -> {
                tvStatus.text = "状态: 准备就绪"
                btnStartRecord.isEnabled = true
                btnStopRecord.isEnabled = false
                btnPlayAudio.isEnabled = false
                btnStopAudio.isEnabled = false
            }
            State.IDLE_HAS_FILE -> {
                tvStatus.text = "状态: 录音完成"
                btnStartRecord.isEnabled = true
                btnStopRecord.isEnabled = false
                btnPlayAudio.isEnabled = true
                btnStopAudio.isEnabled = false
            }
            State.RECORDING -> {
                tvStatus.text = "状态: 正在录音..."
                btnStartRecord.isEnabled = false
                btnStopRecord.isEnabled = true
                btnPlayAudio.isEnabled = false
                btnStopAudio.isEnabled = false
            }
            State.PLAYING -> {
                tvStatus.text = "状态: 正在播放..."
                btnStartRecord.isEnabled = false
                btnStopRecord.isEnabled = false
                btnPlayAudio.isEnabled = false
                btnStopAudio.isEnabled = true
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (isRecording) {
            stopRecording()
        }
        if (isPlaying) {
            stopAudio()
        }
    }
}
