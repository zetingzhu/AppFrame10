package com.zzt.zt_downtosecondview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.zzt.zt_downtosecondview.act.DownToSecondActivity
import com.zzt.zt_downtosecondview.act.PullMainActivity
import com.zzt.zt_downtosecondview.ui.theme.AppFrame10Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppFrame10Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

    val context = LocalContext.current

    Column {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )

        Button(onClick = {
            DownToSecondActivity.start(context)
        }) {
            Text(text = "下拉进入二楼 , 没成功")
        }

        Button(onClick = {
            PullMainActivity.start(context)
        }) {
            Text(text = "下拉进入二楼 ai , 没成功")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppFrame10Theme {
        Greeting("Android")
    }
}