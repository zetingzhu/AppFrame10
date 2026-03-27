package com.trade.zt_speechrecognizer.audio3

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.trade.zt_speechrecognizer.R
import java.io.File

/**
 * 使用 WaveRecorderHelper 工具类进行录音
 * 包含完整录音、暂停、恢复、播放、计时与状态显示功能
 */
class MyAudioV3 : AppCompatActivity(), WaveRecorderHelper.Callback {

    // UI 组件
    private lateinit var tvStatus: TextView
    private lateinit var tvTimer: TextView
    private lateinit var tvFilePath: TextView
    private lateinit var btnStartRecord: Button
    private lateinit var btnStopRecord: Button
    private lateinit var btnPlayAudio: Button
    private lateinit var btnStopAudio: Button

    // 核心组件
    private lateinit var recorderHelper: WaveRecorderHelper
    private var mediaPlayer: MediaPlayer? = null
    private var currentFilePath: String = ""
    private var isPlaying = false

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
        initRecorder()
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
                handleRecordButtonClick()
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

    private fun initRecorder() {
        recorderHelper = WaveRecorderHelper(this, this)
    }

    /**
     * 处理录音按钮点击 (开始/暂停/恢复)
     */
    private fun handleRecordButtonClick() {
        if (!recorderHelper.isRecording()) {
            // 开始录音
            recorderHelper.startRecording()
        } else if (!recorderHelper.isPaused()) {
            // 暂停录音
            recorderHelper.pauseRecording()
        } else {
            // 恢复录音
            recorderHelper.resumeRecording()
        }
    }

    /**
     * 停止录音
     */
    private fun stopRecording() {
        if (!recorderHelper.isRecording()) return
        recorderHelper.stopRecording()
    }

    // --- WaveRecorderHelper.Callback 实现 ---

    override fun onTimerUpdate(millis: Long, formattedTime: String) {
        tvTimer.text = formattedTime
    }

    override fun onRecordingStart() {
        updateUIState(State.RECORDING)
        tvFilePath.text = "正在录音..."
        Toast.makeText(this, "开始录音", Toast.LENGTH_SHORT).show()
    }

    override fun onRecordingStop(filePath: String) {
        currentFilePath = filePath
        updateUIState(State.IDLE_HAS_FILE)
        tvFilePath.text = "文件路径: $currentFilePath"
        Toast.makeText(this, "录音已保存", Toast.LENGTH_SHORT).show()
    }

    override fun onRecordingPause() {
        updateUIState(State.PAUSED)
        Toast.makeText(this, "录音已暂停", Toast.LENGTH_SHORT).show()
    }

    override fun onRecordingResume() {
        updateUIState(State.RECORDING)
        Toast.makeText(this, "录音已恢复", Toast.LENGTH_SHORT).show()
    }

    override fun onError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        updateUIState(State.IDLE_NO_FILE)
    }

    override fun onAmplitudeChange(amplitude: Int) {
        // 可以更新波形图
    }

    // --- 播放逻辑 ---

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
     * UI 状态枚举
     */
    enum class State {
        IDLE_NO_FILE,
        IDLE_HAS_FILE,
        RECORDING,
        PAUSED,
        PLAYING
    }

    /**
     * 更新按钮和状态文字
     */
    private fun updateUIState(state: State) {
        when (state) {
            State.IDLE_NO_FILE -> {
                tvStatus.text = "状态: 准备就绪"
                btnStartRecord.text = "开始录音"
                btnStartRecord.isEnabled = true
                btnStopRecord.isEnabled = false
                btnPlayAudio.isEnabled = false
                btnStopAudio.isEnabled = false
            }
            State.IDLE_HAS_FILE -> {
                tvStatus.text = "状态: 录音完成"
                btnStartRecord.text = "开始录音"
                btnStartRecord.isEnabled = true
                btnStopRecord.isEnabled = false
                btnPlayAudio.isEnabled = true
                btnStopAudio.isEnabled = false
            }
            State.RECORDING -> {
                tvStatus.text = "状态: 正在录音..."
                btnStartRecord.text = "暂停录音"
                btnStartRecord.isEnabled = true
                btnStopRecord.isEnabled = true
                btnPlayAudio.isEnabled = false
                btnStopAudio.isEnabled = false
            }
            State.PAUSED -> {
                tvStatus.text = "状态: 已暂停"
                btnStartRecord.text = "继续录音"
                btnStartRecord.isEnabled = true
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
        if (recorderHelper.isRecording()) {
            stopRecording()
        }
        if (isPlaying) {
            stopAudio()
        }
    }
}
