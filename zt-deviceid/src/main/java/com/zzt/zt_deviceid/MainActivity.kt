package com.zzt.zt_deviceid

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.zzt.zt_deviceid.act.MWebAct
import com.zzt.zt_deviceid.act.MWebActV2
import com.zzt.zt_deviceid.ui.theme.AppFrame10Theme
import com.zzt.zt_deviceid.util.DeviceUtils
import com.zzt.zt_deviceid.util.VpnUtils

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

val TAG = "MainActivity"

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
        Button(onClick = {}) {
            Text(text = "启动第一个页面")
        }
        Button(onClick = {
            val androidId = DeviceUtils.getAndroidId(context)
            Log.d(TAG, " androidId:" + androidId)
        }) {
            Text(text = "获取设备Android Id")
        }
        Button(onClick = {
            val uuidStr = DeviceUtils.getUUIDStr(context)
            Log.d(TAG, " uuidStr:" + uuidStr)
        }) {
            Text(text = "获取设备 UUId")
        }

        Button(onClick = {
            MWebAct.start(context)
        }) {
            Text(text = "打开上次文件浏览器")
        }



        Button(onClick = {
            MWebActV2.start(context)
        }) {
            Text(text = "打开上次文件浏览器 2")
        }


        Button(onClick = {
            val vpnConnected = VpnUtils.isVpnConnected(context)

            Log.d(TAG, " vpn 状态:" + vpnConnected)
        }) {
            Text(text = "检测一下是否开启了vpn")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppFrame10Theme {
        Greeting(name = "Android")
    }
}