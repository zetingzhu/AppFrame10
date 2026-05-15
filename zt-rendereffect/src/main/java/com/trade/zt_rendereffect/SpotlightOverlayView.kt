package com.trade.zt_rendereffect

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

class SpotlightOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x66FFFFFF
        style = Paint.Style.FILL
    }

    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xCCFFFFFF.toInt()
        style = Paint.Style.STROKE
        strokeWidth = resources.displayMetrics.density * 2f
    }

    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f
    private var isVisibleSpotlight = false

    fun updateSpotlight(spotlightCenterX: Float, spotlightCenterY: Float, spotlightRadius: Float) {
        centerX = spotlightCenterX
        centerY = spotlightCenterY
        radius = max(spotlightRadius, 0f)
        isVisibleSpotlight = true
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!isVisibleSpotlight || radius <= 0f) {
            return
        }

        canvas.drawCircle(centerX, centerY, radius, fillPaint)
        canvas.drawCircle(centerX, centerY, radius, strokePaint)
    }
}
