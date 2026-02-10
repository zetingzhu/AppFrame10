package com.trade.zt_speechrecognizer.audio

/**
 * @author: zeting
 * @date: 2026/2/10
 *
 */

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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

    private lateinit var audioRecorder: AudioRecorder
    private lateinit var mediaPlayer: MediaPlayer

    private var isRecording = AtomicBoolean(false)
    private var mPlaying = false
    private var startTime: Long = 0
    private var recordingFile: File? = null

    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            updateRecordingUI()
            handler.postDelayed(this, 50)
        }
    }

    private val playbackRunnable = object : Runnable {
        override fun run() {
            updatePlaybackUI()
            handler.postDelayed(this, 100)
        }
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
        private const val SAMPLE_RATE = 44100
        private const val DISCARD_DURATION = 150

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

        btnRecord.setOnClickListener {
            if (isRecording.get()) {
                stopRecording()
            } else {
                startRecording()
            }
        }

        btnPlay.setOnClickListener {
            togglePlayback()
        }

        btnStop.setOnClickListener {
            stopPlayback()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && mediaPlayer.isPlaying) {
                    val duration = mediaPlayer.duration
                    val newPosition = (duration * progress / 100).toInt()
                    mediaPlayer.seekTo(newPosition)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Do nothing
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Do nothing
            }
        })
    }

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
                runOnUiThread {
                }
            }
        )
    }

    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer().apply {
            setOnCompletionListener {
                runOnUiThread {
                    onPlaybackCompleted()
                }
            }

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

    private fun startRecording() {
        if (!isRecording.get()) {
            if (audioRecorder.startRecording()) {
                isRecording.set(true)
                startTime = System.currentTimeMillis()
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

    private fun stopRecording() {
        if (isRecording.get()) {
            audioRecorder.stopRecording()
            isRecording.set(false)
            handler.removeCallbacks(updateRunnable)
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

    private fun pausePlayback() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            mPlaying = false
            btnPlay.setImageResource(android.R.drawable.ic_media_play)
            handler.removeCallbacks(playbackRunnable)
        }
    }

    private fun stopPlayback() {
        if (mediaPlayer.isPlaying || mPlaying) {
            mediaPlayer.stop()
            mediaPlayer.reset()
            mPlaying = false
            resetPlaybackUI()
            handler.removeCallbacks(playbackRunnable)
        }
    }

    private fun onPlaybackCompleted() {
        mPlaying = false
        resetPlaybackUI()
        handler.removeCallbacks(playbackRunnable)
        Toast.makeText(this, "播放完成", Toast.LENGTH_SHORT).show()
    }

    private fun resetPlaybackUI() {
        btnPlay.setImageResource(android.R.drawable.ic_media_play)
        btnStop.isEnabled = false
        seekBar.progress = 0
        tvCurrentTime.text = "00:00"
        mPlaying = false
    }

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

    private fun updatePlaybackUI() {
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

    private fun formatTime(milliseconds: Int): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun updateFileInfo(file: File) {
        if (file.exists()) {
            val sizeKB = file.length() / 1024
            tvFileSize.text = "$sizeKB KB"

            try {
                val tempPlayer = MediaPlayer()
                tempPlayer.setDataSource(file.absolutePath)
                tempPlayer.prepare()
                val duration = tempPlayer.duration
                val minutes = duration / 1000 / 60
                val seconds = duration / 1000 % 60
                tvDuration.text = String.format("%02d:%02d", minutes, seconds)
                tvTotalTime.text = String.format("%02d:%02d", minutes, seconds)
                tempPlayer.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updatePlaybackButtons(enabled: Boolean) {
        btnPlay.isEnabled = enabled
        btnStop.isEnabled = false
        seekBar.isEnabled = false
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

    private fun checkPermissions() {
        val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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