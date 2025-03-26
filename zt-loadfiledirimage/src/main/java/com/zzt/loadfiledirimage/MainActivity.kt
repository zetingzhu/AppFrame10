package com.zzt.loadfiledirimage

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zzt.loadfiledirimage.act.LoadDirImgAct
import com.zzt.loadfiledirimage.ui.theme.AppFrame10Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppFrame10Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        this@MainActivity,
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(context: Context? = null, name: String, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start //设置水平居中对齐
    ) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
        Button(
            onClick = {
                context?.let { LoadDirImgAct.start(it) }
            },

            ) {
            Text("跳转到新的页面")
        }
        Button(
            onClick = {
            },
            modifier = Modifier.offset(x = 0.dp, y = 0.dp), // 向右和向下偏移 16dp
            contentPadding = PaddingValues(0.dp) // 去掉内部 Padding
        ) {
            Text(
                text = "第二个按钮",
                modifier = Modifier.padding(0.dp), // 去掉 Padding
            )
        }
        Button(
            onClick = {
            },
            modifier = Modifier.offset(x = 0.dp, y = 0.dp), // 向右和向下偏移 16dp
            contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red, // 设置背景色为红色
                contentColor = Color.White // 设置内容颜色为白色
            )
        ) {
            Text("第二个按钮")
        }
        Button(
            onClick = {
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Yellow, // 设置背景色为红色
                contentColor = Color.Blue // 设置内容颜色为白色
            )
        ) {
            Text("第二个按钮")
        }
        Text(
            text = "11111",
            color = Color.Blue,
            modifier = Modifier
                .background(
                    Color.Yellow,
                    shape = RoundedCornerShape(8.dp) // 设置背景颜色形状
                )
                .clip(RoundedCornerShape(8.dp)) // 需要clip来防止内容超出背景的圆角
                .padding(8.dp) // 添加内边距，使文本与背景边缘之间留有空间)
        )
        Text("22222")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppFrame10Theme {
        Greeting(name = "Android")
    }
}