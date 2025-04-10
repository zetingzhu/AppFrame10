package com.zzt.zt_groupfragment

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
import com.zzt.zt_groupfragment.act.ActTabFragment
import com.zzt.zt_groupfragment.act.ActTabV2
import com.zzt.zt_groupfragment.act.ActTabV3
import com.zzt.zt_groupfragment.act.ActTabV4
import com.zzt.zt_groupfragment.ui.theme.AppFrame10Theme

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
            ActTabFragment.start(context)
        }) {
            Text(text = "Tab Fragment")
        }

        Button(onClick = {
            ActTabV2.start(context)
        }) {
            Text(text = "Tab + Group 做出，可以在分组页面中添加 Fragment ")
        }

        Button(onClick = {
        }) {
            Text(text = "Tab + Group 做出，可以在分组页面中添加 Fragment 豆包生成")
        }

        Button(onClick = {
            ActTabV3.start(context)
        }) {
            Text(text = "Vp2 + Fragment 自定义适配器,适配深色")
        }

        Button(onClick = {
            ActTabV4.start(context)
        }) {
            Text(text = "TabLayout + Viewpager2 + Fragment 豆包生成")
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