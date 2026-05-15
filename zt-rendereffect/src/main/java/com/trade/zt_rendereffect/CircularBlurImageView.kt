package com.trade.zt_rendereffect

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import android.widget.ImageView
import kotlin.math.max

class CircularBlurImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ImageView(context, attrs) {

    private val clipPath = Path()
    private var blurCenterX = 0f
    private var blurCenterY = 0f
    private var blurRadius = 0f
    private var hasBlurArea = false

    fun updateBlurArea(centerX: Float, centerY: Float, radius: Float) {
        blurCenterX = centerX
        blurCenterY = centerY
        blurRadius = max(radius, 0f)
        hasBlurArea = true
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (!hasBlurArea || blurRadius <= 0f) {
            return
        }

        val saveCount = canvas.save()
        clipPath.reset()
        clipPath.addCircle(blurCenterX, blurCenterY, blurRadius, Path.Direction.CW)
        canvas.clipPath(clipPath)
        super.onDraw(canvas)
        canvas.restoreToCount(saveCount)
    }
}
