package com.trade.zt_speechrecognizer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.trade.zt_speechrecognizer.audio.MyRecordAct2
import com.trade.zt_speechrecognizer.audio3.MyAudioV3
import com.trade.zt_speechrecognizer.speechtotext.MySpeechToText
import com.trade.zt_speechrecognizer.ui.theme.AppFrame10Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppFrame10Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SpeechRecognizerList(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SpeechRecognizerList(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "语音识别功能列表",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                val intent = Intent(context, MySpeechToText::class.java)
                context.startActivity(intent)
            }
        ) {
            Text("系统语音识别")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                MyRecordAct2.Companion.start(context)
            }
        ) {
            Text("录音--v2噪")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                MyAudioV3.Companion.start(context)
            }
        ) {
            Text("录音--v3")
        }
    }
}