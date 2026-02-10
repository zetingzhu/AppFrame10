package com.trade.zt_speechrecognizer.audio

/**
 * @author: zeting
 * @date: 2026/2/10
 * @description: 录音界面 Activity V2
 * 主要功能：
 * 1. 录音控制（开始/停止）
 * 2. 录音播放（播放/暂停/进度拖拽）
 * 3. 录音状态实时显示（时长/文件大小/波形数据回调）
 * 4. 权限处理
 */

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.trade.zt_speechrecognizer.R
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class MyRecordAct2 : AppCompatActivity() {

    // UI 组件
    private lateinit var btnRecord: Button
    private lateinit var btnPlay: ImageButton
    private lateinit var btnStop: ImageButton
    private lateinit var seekBar: SeekBar
    private lateinit var tvStatus: TextView
    private lateinit var tvDuration: TextView
    private lateinit var tvCurrentTime: TextView
    private lateinit var tvTotalTime: TextView
    private lateinit var tvFileSize: TextView
    private lateinit var tvFilePath: TextView
    private lateinit var tvSampleRate: TextView

    // 核心功能组件
    private lateinit var audioRecorder: AudioRecorder
    private lateinit var mediaPlayer: MediaPlayer

    // 状态标志
    private var isRecording = AtomicBoolean(false) // 是否正在录音 (原子操作保证线程安全)
    private var mPlaying = false // 是否正在播放
    private var isTrackingTouch = false // 是否正在拖动进度条 (防止拖动时进度跳变)
    private var startTime: Long = 0 // 录音开始时间戳
    private var recordingFile: File? = null // 当前录音文件

    // UI 更新 Handler
    private val handler = Handler(Looper.getMainLooper())
    
    // 录音状态更新任务 (50ms刷新一次)
    private val updateRunnable = object : Runnable {
        override fun run() {
            updateRecordingUI()
            handler.postDelayed(this, 50)
        }
    }

    // 播放进度更新任务 (100ms刷新一次)
    private val playbackRunnable = object : Runnable {
        override fun run() {
            updatePlaybackUI()
            handler.postDelayed(this, 100)
        }
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
        private const val SAMPLE_RATE = 44100 // 采样率
        private const val DISCARD_DURATION = 150 // 丢弃前150ms数据 (去爆音)

        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, MyRecordAct2::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_v2)

        initViews()
        initRecorder()
        initMediaPlayer()
        checkPermissions()

        tvSampleRate.text = "$SAMPLE_RATE Hz"
    }

    /**
     * 初始化视图组件及监听器
     */
    private fun initViews() {
        btnRecord = findViewById(R.id.btnRecord)
        btnPlay = findViewById(R.id.btnPlay)
        btnStop = findViewById(R.id.btnStop)
        seekBar = findViewById(R.id.seekBar)
        tvStatus = findViewById(R.id.tvStatus)
        tvDuration = findViewById(R.id.tvDuration)
        tvCurrentTime = findViewById(R.id.tvCurrentTime)
        tvTotalTime = findViewById(R.id.tvTotalTime)
        tvFileSize = findViewById(R.id.tvFileSize)
        tvFilePath = findViewById(R.id.tvFilePath)
        tvSampleRate = findViewById(R.id.tvSampleRate)

        // 录音按钮点击事件
        btnRecord.setOnClickListener {
            if (isRecording.get()) {
                stopRecording()
            } else {
                startRecording()
            }
        }

        // 播放/暂停按钮点击事件
        btnPlay.setOnClickListener {
            togglePlayback()
        }

        // 停止播放按钮点击事件
        btnStop.setOnClickListener {
            stopPlayback()
        }

        // 进度条拖动监听
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // 如果是用户手动拖动，且当前正在播放，则实时调整播放进度
                if (fromUser && mediaPlayer.isPlaying) {
                    val duration = mediaPlayer.duration
                    val newPosition = (duration * progress / 100).toInt()
                    mediaPlayer.seekTo(newPosition)
                    tvCurrentTime.text = formatTime(newPosition)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // 开始拖动，标记状态，避免 Timer 自动更新进度条导致冲突
                isTrackingTouch = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // 停止拖动，恢复 Timer 更新，并确保最终位置正确
                isTrackingTouch = false
                if (mediaPlayer.isPlaying || mPlaying) {
                    val duration = mediaPlayer.duration
                    val progress = seekBar?.progress ?: 0
                    val newPosition = (duration * progress / 100).toInt()
                    mediaPlayer.seekTo(newPosition)
                }
            }
        })
    }

    /**
     * 初始化录音器 (AudioRecorder)
     */
    private fun initRecorder() {
        audioRecorder = AudioRecorder(
            sampleRate = SAMPLE_RATE,
            discardDuration = DISCARD_DURATION,
            onRecordingStart = { file ->
                recordingFile = file
                runOnUiThread {
                    tvStatus.text = "录音进行中..."
                    tvFilePath.text = "文件保存路径: ${file.absolutePath}"
                    updatePlaybackButtons(false) // 录音时禁用播放按钮
                }
            },
            onRecordingStop = { file ->
                runOnUiThread {
                    tvStatus.text = "录音已保存"
                    updateFileInfo(file)
                    updatePlaybackButtons(file.exists() && file.length() > 0)
                }
            },
            onError = { error ->
                runOnUiThread {
                    tvStatus.text = "错误: $error"
                    Toast.makeText(this, "录音错误: $error", Toast.LENGTH_SHORT).show()
                    resetUI()
                }
            },
            onAudioData = { audioData ->
                // 这里可以处理实时音频数据，例如绘制波形图
                runOnUiThread {
                    // TODO: 如果有波形视图，在这里更新
                }
            }
        )
    }

    /**
     * 初始化播放器 (MediaPlayer)
     */
    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer().apply {
            // 播放完成监听
            setOnCompletionListener {
                runOnUiThread {
                    onPlaybackCompleted()
                }
            }

            // 错误监听
            setOnErrorListener { _, what, extra ->
                runOnUiThread {
                    Toast.makeText(
                        this@MyRecordAct2,
                        "播放错误: $what, $extra", Toast.LENGTH_SHORT
                    ).show()
                    resetPlaybackUI()
                }
                true
            }

            // 准备完成监听
            setOnPreparedListener {
                runOnUiThread {
                    val duration = it.duration
                    tvTotalTime.text = formatTime(duration)
                    seekBar.max = 100
                    seekBar.progress = 0
                    seekBar.isEnabled = true

                    // 开始播放
                    it.start()
                    mPlaying = true
                    btnPlay.setImageResource(android.R.drawable.ic_media_pause)
                    btnStop.isEnabled = true

                    // 开始更新进度
                    handler.post(playbackRunnable)
                }
            }
        }
    }

    /**
     * 开始录音
     */
    private fun startRecording() {
        if (!isRecording.get()) {
            if (audioRecorder.startRecording()) {
                isRecording.set(true)
                startTime = System.currentTimeMillis()
                
                // 更新 UI 状态
                btnRecord.text = "停止录音"
                btnRecord.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        android.R.color.holo_green_dark
                    )
                )
                handler.post(updateRunnable)
                updatePlaybackButtons(false)
            }
        }
    }

    /**
     * 停止录音
     */
    private fun stopRecording() {
        if (isRecording.get()) {
            audioRecorder.stopRecording()
            isRecording.set(false)
            
            // 停止 UI 更新
            handler.removeCallbacks(updateRunnable)
            
            // 恢复 UI 状态
            btnRecord.text = "开始录音"
            btnRecord.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    android.R.color.holo_red_dark
                )
            )
            tvDuration.text = "00:00"
        }
    }

    /**
     * 切换播放/暂停状态
     */
    private fun togglePlayback() {
        if (recordingFile == null || !recordingFile!!.exists()) {
            Toast.makeText(this, "请先录制音频", Toast.LENGTH_SHORT).show()
            return
        }

        if (mPlaying) {
            pausePlayback()
        } else {
            startPlayback()
        }
    }

    /**
     * 开始播放逻辑
     */
    private fun startPlayback() {
        try {
            if (!mediaPlayer.isPlaying) {
                if (mediaPlayer.currentPosition > 0) {
                    // 继续播放
                    mediaPlayer.start()
                } else {
                    // 重新开始播放
                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(recordingFile!!.absolutePath)
                    mediaPlayer.prepareAsync()
                }

                mPlaying = true
                btnPlay.setImageResource(android.R.drawable.ic_media_pause)
                btnStop.isEnabled = true
                handler.post(playbackRunnable)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "播放失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 暂停播放
     */
    private fun pausePlayback() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            mPlaying = false
            btnPlay.setImageResource(android.R.drawable.ic_media_play)
            handler.removeCallbacks(playbackRunnable)
        }
    }

    /**
     * 停止播放
     */
    private fun stopPlayback() {
        if (mediaPlayer.isPlaying || mPlaying) {
            mediaPlayer.stop()
            mediaPlayer.reset()
            mPlaying = false
            resetPlaybackUI()
            handler.removeCallbacks(playbackRunnable)
        }
    }

    /**
     * 播放完成回调
     */
    private fun onPlaybackCompleted() {
        mPlaying = false
        resetPlaybackUI()
        handler.removeCallbacks(playbackRunnable)
        Toast.makeText(this, "播放完成", Toast.LENGTH_SHORT).show()
    }

    /**
     * 重置播放 UI
     */
    private fun resetPlaybackUI() {
        btnPlay.setImageResource(android.R.drawable.ic_media_play)
        btnStop.isEnabled = false
        seekBar.progress = 0
        tvCurrentTime.text = "00:00"
        mPlaying = false
    }

    /**
     * 更新录音 UI (时长/大小)
     */
    private fun updateRecordingUI() {
        if (isRecording.get()) {
            val elapsed = System.currentTimeMillis() - startTime
            val minutes = (elapsed / 1000 / 60).toInt()
            val seconds = (elapsed / 1000 % 60).toInt()
            tvDuration.text = String.format("%02d:%02d", minutes, seconds)

            recordingFile?.let { file ->
                if (file.exists()) {
                    val sizeKB = file.length() / 1024
                    tvFileSize.text = "$sizeKB KB"
                }
            }
        }
    }

    /**
     * 更新播放进度 UI
     */
    private fun updatePlaybackUI() {
        // 如果用户正在拖动 SeekBar，则暂停更新，避免冲突
        if (isTrackingTouch) return

        if (mediaPlayer.isPlaying) {
            val currentPosition = mediaPlayer.currentPosition
            val duration = mediaPlayer.duration

            tvCurrentTime.text = formatTime(currentPosition)

            if (duration > 0) {
                val progress = (currentPosition * 100 / duration).toInt()
                seekBar.progress = progress
            }
        }
    }

    /**
     * 格式化时间 mm:ss
     */
    private fun formatTime(milliseconds: Int): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    /**
     * 更新录音文件信息 (时长/大小)
     * 优化：使用 MediaMetadataRetriever 替代 MediaPlayer，减少内存开销
     */
    private fun updateFileInfo(file: File) {
        if (file.exists()) {
            val sizeKB = file.length() / 1024
            tvFileSize.text = "$sizeKB KB"

            try {
                // 优化点：使用 MediaMetadataRetriever 获取时长，比创建 MediaPlayer 更轻量
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(file.absolutePath)
                val timeString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val duration = timeString?.toLong() ?: 0L
                retriever.release()

                val minutes = duration / 1000 / 60
                val seconds = duration / 1000 % 60
                tvDuration.text = String.format("%02d:%02d", minutes, seconds)
                tvTotalTime.text = String.format("%02d:%02d", minutes, seconds)
            } catch (e: Exception) {
                e.printStackTrace()
                // 降级处理或显示错误
                tvDuration.text = "00:00"
            }
        }
    }

    /**
     * 更新播放控制按钮状态
     */
    private fun updatePlaybackButtons(enabled: Boolean) {
        btnPlay.isEnabled = enabled
        btnStop.isEnabled = false
        seekBar.isEnabled = enabled // 只有可播放时才允许拖动
        seekBar.progress = 0
        tvCurrentTime.text = "00:00"

        if (!enabled) {
            btnPlay.setImageResource(android.R.drawable.ic_media_play)
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.reset()
                mPlaying = false
                handler.removeCallbacks(playbackRunnable)
            }
        }
    }

    /**
     * 重置所有 UI 状态
     */
    private fun resetUI() {
        isRecording.set(false)
        handler.removeCallbacks(updateRunnable)
        handler.removeCallbacks(playbackRunnable)
        btnRecord.text = "开始录音"
        btnRecord.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        tvDuration.text = "00:00"
        tvStatus.text = "准备录音..."
        updatePlaybackButtons(false)
    }

    /**
     * 检查并请求权限
     */
    private fun checkPermissions() {
        val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ 只需要录音权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    REQUEST_RECORD_AUDIO_PERMISSION
                )
            }
        } else {
            // Android 12及以下可能需要存储权限 (取决于文件保存位置，如果是私有目录其实不需要 WRITE_EXTERNAL_STORAGE)
            // 这里保留原有逻辑，但建议如果只用 getExternalFilesDir 则移除 WRITE_EXTERNAL_STORAGE
            val permissionsNeeded = mutableListOf<String>()
            permissionsNeeded.add(Manifest.permission.RECORD_AUDIO)

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

            if (permissionsNeeded.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    this,
                    permissionsNeeded.toTypedArray(),
                    REQUEST_RECORD_AUDIO_PERMISSION
                )
            }
        }
    }

    /**
     * 权限请求回调
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            var allGranted = true
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false
                    break
                }
            }

            if (!allGranted) {
                Toast.makeText(this, "需要录音权限才能使用此功能", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 销毁时释放资源
     */
    override fun onDestroy() {
        super.onDestroy()
        if (isRecording.get()) {
            stopRecording()
        }
        stopPlayback()
        handler.removeCallbacks(updateRunnable)
        handler.removeCallbacks(playbackRunnable)
        mediaPlayer.release()
    }
}
