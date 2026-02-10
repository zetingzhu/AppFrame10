package com.trade.zt_speechrecognizer.speechtotext

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.trade.zt_speechrecognizer.R

class MySpeechToText : AppCompatActivity() {
    private val TAG = MySpeechToText::class.java.simpleName
    private val SPEECH_REQUEST_CODE = 1001
    private val MAX_SPEECH_DURATION = 60000

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var tvResult: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvTime: TextView
    private lateinit var btnStart: Button
    private lateinit var spinnerLanguage: Spinner

    private var isListening = false
    private var startTime: Long = 0
    private val handler = Handler(Looper.getMainLooper())
    private val timeUpdateRunnable = object : Runnable {
        override fun run() {
            if (isListening) {
                val currentTime = System.currentTimeMillis()
                val elapsedSeconds = (currentTime - startTime) / 1000
                val minutes = elapsedSeconds / 60
                val seconds = elapsedSeconds % 60
                tvTime.text = String.format("%02d:%02d", minutes, seconds)

                if (elapsedSeconds >= 60) {
                    Log.w(TAG, "识别时间超过60秒，自动停止")
                    tvStatus.text = "识别超时（60秒）"
                    stopSpeechRecognition()
                } else {
                    handler.postDelayed(this, 1000)
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startSpeechRecognition()
        } else {
            tvStatus.text = "录音权限被拒绝，无法使用语音识别"
        }
    }

    private val languageMap = mapOf(
        0 to "zh-CN",
        1 to "zh-TW",
        2 to "en-US",
        3 to "de-DE",
        4 to "fr-FR",
        5 to "es-ES",
        6 to "ja-JP",
        7 to "ko-KR",
        8 to "ru-RU"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_speech_to_text)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initViews()
        initSpeechRecognizer()
    }

    private fun initViews() {
        tvResult = findViewById(R.id.tvResult)
        tvStatus = findViewById(R.id.tvStatus)
        tvTime = findViewById(R.id.tvTime)
        btnStart = findViewById(R.id.btnStart)
        spinnerLanguage = findViewById(R.id.spinnerLanguage)

        btnStart.setOnClickListener {
            if (isListening) {
                stopSpeechRecognition()
            } else {
                checkPermissionAndStart()
            }
        }
    }

    private fun initSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d(TAG, "onReadyForSpeech")
                isListening = true
                startTime = System.currentTimeMillis()
                tvTime.text = "00:00"
                handler.post(timeUpdateRunnable)
                btnStart.text = "停止识别"
                tvStatus.text = "正在聆听..."
            }

            override fun onBeginningOfSpeech() {
                Log.d(TAG, "onBeginningOfSpeech")
                tvStatus.text = "检测到语音..."
            }

            override fun onRmsChanged(rmsdB: Float) {
                Log.d(TAG, "onRmsChanged: $rmsdB")
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                Log.d(TAG, "onBufferReceived")
            }

            override fun onEndOfSpeech() {
                Log.d(TAG, "onEndOfSpeech")
                tvStatus.text = "处理中..."
            }

            override fun onError(error: Int) {
                Log.e(TAG, "onError: $error")
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "音频录制错误"
                    SpeechRecognizer.ERROR_CLIENT -> "客户端错误"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "权限不足"
                    SpeechRecognizer.ERROR_NETWORK -> "网络错误"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "网络超时"
                    SpeechRecognizer.ERROR_NO_MATCH -> "未匹配到语音"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "识别器忙碌"
                    SpeechRecognizer.ERROR_SERVER -> "服务器错误"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "语音超时"
                    else -> "未知错误: $error"
                }
                tvStatus.text = "错误: $errorMessage"
                stopSpeechRecognition()
            }

            override fun onResults(results: Bundle?) {
                Log.d(TAG, "onResults")
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                Log.d(TAG, "matches: $matches")
                if (!matches.isNullOrEmpty()) {
                    val recognizedText = matches[0]
                    Log.d(TAG, "recognizedText: $recognizedText")
                    appendResult(recognizedText)
                    tvStatus.text = "识别完成"
                } else {
                    Log.w(TAG, "matches is null or empty")
                    tvStatus.text = "未识别到语音"
                }
                isListening = false
                handler.removeCallbacks(timeUpdateRunnable)
                btnStart.text = "开始识别"
            }

            override fun onPartialResults(partialResults: Bundle?) {
                Log.d(TAG, "onPartialResults")
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    tvStatus.text = "识别中: ${matches[0]}"
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.d(TAG, "onEvent: $eventType")
            }
        })
    }

    private fun checkPermissionAndStart() {
        val permission = Manifest.permission.RECORD_AUDIO
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            startSpeechRecognition()
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun startSpeechRecognition() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            tvStatus.text = "设备不支持语音识别"
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)

            val selectedLanguagePosition = spinnerLanguage.selectedItemPosition
            val languageCode = languageMap[selectedLanguagePosition] ?: "zh-CN"
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)

            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1500)
        }

        try {
            speechRecognizer.startListening(intent)
        } catch (e: Exception) {
            Log.e(TAG, "启动语音识别失败", e)
            tvStatus.text = "启动失败: ${e.message}"
        }
    }

    private fun stopSpeechRecognition() {
        if (isListening) {
            handler.removeCallbacks(timeUpdateRunnable)
            try {
                speechRecognizer.stopListening()
            } catch (e: Exception) {
                Log.e(TAG, "停止语音识别时出错", e)
            }
            isListening = false
            btnStart.text = "开始识别"
            tvStatus.text = "已停止"
        }
    }

    private fun appendResult(text: String) {
        val currentText = tvResult.text.toString()
        Log.d(TAG, "appendResult - currentText: '$currentText'")
        Log.d(TAG, "appendResult - newText: '$text'")
        if (currentText == "识别结果将显示在这里...") {
            tvResult.text = text
        } else {
            tvResult.text = "$currentText\n\n$text"
        }
        Log.d(TAG, "appendResult - finalText: '${tvResult.text}'")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.destroy()
        }
    }
}