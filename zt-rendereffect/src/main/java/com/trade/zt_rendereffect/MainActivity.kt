package com.trade.zt_rendereffect

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trade.zt_rendereffect.ui.theme.AppFrame10Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppFrame10Theme {
                DemoListScreen(
                    onOpenViewDemo = {
                        startActivity(Intent(this, ViewRenderEffectActivity::class.java))
                    },
                    onOpenPartialBlurDemo = {
                        startActivity(Intent(this, PartialBlurRenderEffectActivity::class.java))
                    },
                    onOpenTouchBlurDemo = {
                        startActivity(Intent(this, TouchBlurSpotlightActivity::class.java))
                    }
                )
            }
        }
    }
}

@Composable
fun DemoListScreen(
    onOpenViewDemo: () -> Unit,
    onOpenPartialBlurDemo: () -> Unit,
    onOpenTouchBlurDemo: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF6F7FB)
    ) { innerPadding ->
        DemoListPage(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onOpenViewDemo = onOpenViewDemo,
            onOpenPartialBlurDemo = onOpenPartialBlurDemo,
            onOpenTouchBlurDemo = onOpenTouchBlurDemo
        )
    }
}

@Composable
fun DemoListPage(
    modifier: Modifier = Modifier,
    onOpenViewDemo: () -> Unit,
    onOpenPartialBlurDemo: () -> Unit,
    onOpenTouchBlurDemo: () -> Unit
) {
    Column(
        modifier = modifier
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "RenderEffect 示例列表",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )
        Text(
            text = "首页提供多个官方 RenderEffect 示例入口，分别演示整图模糊、局部区域模糊和触摸毛玻璃效果。",
            fontSize = 14.sp,
            color = Color(0xFF4B5563)
        )

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "View 方案高斯模糊",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827)
                )
                Text(
                    text = "新页面使用 XML 布局、ImageView 和 SeekBar，通过官方 RenderEffect 实时调整模糊半径。",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
                Button(
                    onClick = onOpenViewDemo,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "打开 View 高斯模糊页面")
                }
            }
        }

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "下半部分高斯模糊",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827)
                )
                Text(
                    text = "新页面保留图片上半部分原图，只给下半部分叠加模糊层，适合做局部毛玻璃效果。",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
                Button(
                    onClick = onOpenPartialBlurDemo,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "打开下半部分模糊页面")
                }
            }
        }

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "触摸圆形毛玻璃",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827)
                )
                Text(
                    text = "整张图片作为背景，手指触摸的位置出现一个圆形半透明区域，并只对该区域下方内容进行模糊显示。",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
                Button(
                    onClick = onOpenTouchBlurDemo,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "打开触摸毛玻璃页面")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DemoListPreview() {
    AppFrame10Theme {
        DemoListScreen(
            onOpenViewDemo = {},
            onOpenPartialBlurDemo = {},
            onOpenTouchBlurDemo = {}
        )
    }
}
