package com.trade.zt_rendereffect

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.ComponentActivity

class PartialBlurRenderEffectActivity : ComponentActivity() {

    private lateinit var imageBottomBlur: ImageView
    private lateinit var textRadiusValue: TextView
    private lateinit var textTips: TextView
    private lateinit var seekBarBlur: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partial_blur_render_effect)

        imageBottomBlur = findViewById(R.id.image_bottom_blur)
        textRadiusValue = findViewById(R.id.text_radius_value)
        textTips = findViewById(R.id.text_tips)
        seekBarBlur = findViewById(R.id.seek_bar_blur)

        initViewState()
        initListeners()
    }

    private fun initViewState() {
        val defaultRadius = 18
        seekBarBlur.progress = defaultRadius
        updateBlur(defaultRadius.toFloat())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            textTips.text = "当前页面只对图片下半部分应用高斯模糊，上半部分保持原图显示。"
            seekBarBlur.isEnabled = true
        } else {
            textTips.text = "当前设备低于 Android 12，官方 RenderEffect 不可用，已降级为原图显示。"
            seekBarBlur.isEnabled = false
        }
    }

    private fun initListeners() {
        seekBarBlur.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateBlur(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })
    }

    private fun updateBlur(radius: Float) {
        textRadiusValue.text = "${radius.toInt()} px"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            imageBottomBlur.setRenderEffect(
                RenderEffect.createBlurEffect(
                    radius,
                    radius,
                    Shader.TileMode.DECAL
                )
            )
        } else {
            imageBottomBlur.setRenderEffect(null)
        }
    }
}
