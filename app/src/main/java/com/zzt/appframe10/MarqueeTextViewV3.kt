package com.zzt.appframe10

/**
 * @author: zeting
 * @date: 2025/4/7
 * 跑马灯 3
 */
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.doOnLayout
import kotlin.math.ceil
import kotlin.math.max
import androidx.core.view.doOnLayout
import kotlin.math.abs
import kotlin.math.max
import androidx.core.view.doOnLayout
import kotlin.math.abs
import kotlin.math.max

class MarqueeTextViewV3 : AppCompatTextView {

    private var textToDraw: String = ""
    private val textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG) // Initialize directly here
    private var textWidth: Float = 0f
    private var currentOffset: Float = 0f
    private var animator: ValueAnimator? = null
    private var isFocusedInternal: Boolean = false
    private var isAttachedToWindowInternal: Boolean = false
    private var scrollSpeedFactor: Float = 0.03f
    private var marqueeMode: MarqueeMode = MarqueeMode.WHEN_FOCUSED
    private var restartDelay: Long = 1500

    enum class MarqueeMode {
        WHEN_FOCUSED,
        ALWAYS
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet? = null) {
        // Initialize textPaint properties from the TextView's paint
        paint?.let {
            textPaint.color = it.color
            textPaint.textSize = it.textSize
            textPaint.typeface = it.typeface
        }

        attrs?.let {
            val typedArray =
                context.obtainStyledAttributes(it, R.styleable.MarqueeTextViewV3)
            val mode = typedArray.getInt(
                R.styleable.MarqueeTextViewV3_marqueeMode,
                MarqueeMode.WHEN_FOCUSED.ordinal
            )
            marqueeMode = MarqueeMode.values()[mode]
            scrollSpeedFactor = typedArray.getFloat(
                R.styleable.MarqueeTextViewV3_scrollSpeedFactor,
                scrollSpeedFactor
            )
            restartDelay = typedArray.getInt(
                R.styleable.MarqueeTextViewV3_restartDelay,
                restartDelay.toInt()
            ).toLong()
            typedArray.recycle()
        }

        doOnLayout {
            startMarqueeIfNeeded()
        }
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        textToDraw = text?.toString() ?: ""
        if (textPaint != null) {
            textWidth = textPaint.measureText(textToDraw) // textPaint should now be initialized
        }
        currentOffset = 0f
        stopMarquee()
        startMarqueeIfNeeded()
    }

    override fun onDraw(canvas: Canvas) {
        val y = baseline.toFloat()
        if (textWidth > 0) {
            canvas.drawText(textToDraw, currentOffset, y, textPaint)
        }
    }

    // ... (rest of your code remains the same)

    companion object {
        @JvmStatic
        val R_styleable_MarqueeTextView =
            intArrayOf(R.attr.marqueeMode, R.attr.scrollSpeedFactor, R.attr.restartDelay)
        const val R_styleable_MarqueeTextViewV3_marqueeMode = 0
        const val R_styleable_MarqueeTextViewV3_scrollSpeedFactor = 1
        const val R_styleable_MarqueeTextViewV3_restartDelay = 2
    }

    fun setMarqueeMode(mode: MarqueeMode) {
        marqueeMode = mode
        startMarqueeIfNeeded()
    }

    fun setScrollSpeedFactor(factor: Float) {
        scrollSpeedFactor = max(0f, factor)
        startMarqueeIfNeeded()
    }

    fun setRestartDelay(delayMillis: Long) {
        restartDelay = max(0L, delayMillis)
        startMarqueeIfNeeded()
    }

    private fun startMarqueeIfNeeded() {
        if (textWidth > width) {
            startMarquee()
        }
    }

    private fun startMarquee() {
        if (animator?.isRunning == true) {
            return
        }

        animator = ValueAnimator.ofFloat(0f, -(textWidth + width)).apply {
            duration = (80 * ceil((textWidth + width) / (width / 20))).toLong() // 调整速度
            repeatCount = ValueAnimator.INFINITE
            interpolator = null // LinearInterpolator for constant speed
            addUpdateListener { animation ->
                currentOffset = animation.animatedValue as Float
                invalidate()
            }
            start()
        }
    }


    private fun stopMarquee() {
        animator?.cancel()
        animator = null
        currentOffset = 0f
        invalidate()
    }

    // 允许在代码中手动设置焦点
    fun requestMarqueeFocus() {
        if (!isFocused) {
            requestFocus()
        }
    }

    // 允许在代码中手动移除焦点
    fun clearMarqueeFocus() {
        if (isFocused) {
            clearFocus()
        }
    }
}