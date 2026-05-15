package com.trade.zt_rendereffect

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity

class TouchBlurSpotlightActivity : ComponentActivity() {

    private lateinit var rootTouchArea: View
    private lateinit var imageBlurLayer: CircularBlurImageView
    private lateinit var viewSpotlightOverlay: SpotlightOverlayView
    private lateinit var textTips: TextView

    private val spotlightRadiusPx by lazy { resources.displayMetrics.density * 96f }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_touch_blur_spotlight)

        rootTouchArea = findViewById(R.id.root_touch_area)
        imageBlurLayer = findViewById(R.id.image_blur_layer)
        viewSpotlightOverlay = findViewById(R.id.view_spotlight_overlay)
        textTips = findViewById(R.id.text_tips)

        initBlurLayer()
        initTouchArea()
    }

    private fun initBlurLayer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            imageBlurLayer.setRenderEffect(
                RenderEffect.createBlurEffect(
                    40f,
                    40f,
                    Shader.TileMode.DECAL
                )
            )
            textTips.text = "按住并移动手指，圆形半透明区域下方会显示 RenderEffect 高斯模糊效果。"
        } else {
            imageBlurLayer.setRenderEffect(null)
            textTips.text = "当前设备低于 Android 12，官方 RenderEffect 不可用，因此这里只显示原图与圆形遮罩。"
        }

        rootTouchArea.post {
            updateSpotlight(
                centerX = rootTouchArea.width / 2f,
                centerY = rootTouchArea.height * 0.55f
            )
        }
    }

    private fun initTouchArea() {
        rootTouchArea.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_MOVE -> {
                    updateSpotlight(event.x, event.y)
                    true
                }

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> true

                else -> false
            }
        }
    }

    private fun updateSpotlight(centerX: Float, centerY: Float) {
        imageBlurLayer.updateBlurArea(centerX, centerY, spotlightRadiusPx)
        viewSpotlightOverlay.updateSpotlight(centerX, centerY, spotlightRadiusPx)
    }
}
