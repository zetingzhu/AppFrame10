package com.example.yourapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.Path
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.zzt.zt_downtome_view.R
import java.util.concurrent.TimeUnit

/**
 * @author: zeting
 * @date: 2025/5/19
 * 圆形倒计时
 */
class CircleCountdownView : FrameLayout {
    val TAG = CircleCountdownView::class.java.simpleName

    private val rectF = RectF()
    private var totalTime = 10 * 1000L // 总时间，单位毫秒
    private var remainingTime = totalTime// 剩余时间
    private var isCountingDown = false
    private var animator: ValueAnimator? = null
    var callback: DownTimeCallback? = null // 监听剩余

    var drawStatus = 1;// 中间绘制啥东西 0 ，倒计时，1，正确，2，错误 ,3.显示0
    var downStyle = 0;// 倒计时样式 0 ，绿色倒计时，1 蓝色倒计时

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 12f
        color = Color.BLUE
    }

    // 背景圆圆
    private val bgCircleFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = 12f // 可根据需要调整
        color = Color.parseColor("#25282f")
    }

    // 背景圆圆
    private val bgCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 12f // 可根据需要调整
        color = Color.parseColor("#979797")
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 70f
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val rightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 12f
        color = Color.parseColor("#00CB6F")
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    var rightPath = Path() // 多的路线
    private val errorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 12f
        color = Color.parseColor("#CE3030")
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }


    constructor(context: Context) : super(context) {
        setWillNotDraw(false)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setWillNotDraw(false)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setWillNotDraw(false)
    }

    override fun dispatchDraw(canvas: Canvas) {
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (Math.min(width, height) / 2f) - progressPaint.strokeWidth / 2f

        // 绘制灰色底色
        canvas.drawCircle(centerX, centerY, radius, bgCircleFill)
        canvas.drawCircle(centerX, centerY, radius, bgCircle)

        rectF.set(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )

        if (downStyle == 0) {
            // 绿色倒计时
            if (drawStatus == 1) {
                progressPaint.setColor(ContextCompat.getColor(context, R.color.color_00CB6F))
            } else if (drawStatus == 2) {
                progressPaint.setColor(ContextCompat.getColor(context, R.color.color_ce3030))
            } else {
                if (remainingTime >= 6000L) {
                    progressPaint.setColor(ContextCompat.getColor(context, R.color.color_00CB6F))
                } else {
                    progressPaint.setColor(ContextCompat.getColor(context, R.color.color_ce3030))
                }
            }
        } else {
            // 蓝色倒计时
            progressPaint.setColor(ContextCompat.getColor(context, R.color.color_327FFF))
        }

        // 绘制圆形进度条
        val sweepAngle = (remainingTime.toFloat() / totalTime.toFloat()) * 360f
        canvas.drawArc(rectF, -90f, sweepAngle, false, progressPaint)

        if (remainingTime >= 0) {
            // 宽度
            val widthSS = (width * 0.5f - 24f)

            if (drawStatus == 1) {
                // 绘制对勾图案
                rightPath.reset()
                rightPath.moveTo(centerX - (widthSS * 0.4f), centerY + (widthSS * 0.1f))
                rightPath.lineTo(centerX - (widthSS * 0.1f), centerY + (widthSS * 0.4f))
                rightPath.lineTo(centerX + (widthSS * 0.4f), centerY - (widthSS * 0.3f))

                canvas.drawPath(rightPath, rightPaint)
            } else if (drawStatus == 2) {
                // 绘制错误图案
                val startX1 = centerX - (widthSS * 0.3f)
                val startY1 = centerY - (widthSS * 0.3f)
                val endX1 = centerX + (widthSS * 0.3f)
                val endY1 = centerY + (widthSS * 0.3f)

                val startX2 = centerX - (widthSS * 0.3f)
                val startY2 = centerY + (widthSS * 0.3f)
                val endX2 = centerX + (widthSS * 0.3f)
                val endY2 = centerY - (widthSS * 0.3f)

                canvas.drawLine(startX1, startY1, endX1, endY1, errorPaint)
                canvas.drawLine(startX2, startY2, endX2, endY2, errorPaint)
            } else {
                // 绘制倒计时文本
                val seconds = TimeUnit.MILLISECONDS.toSeconds(remainingTime)
                val timeText = String.format("%ds", seconds)
                canvas.drawText(
                    timeText,
                    centerX,
                    centerY - (textPaint.descent() + textPaint.ascent()) / 2,
                    textPaint
                )
            }

            // 返回剩余值
            callback?.callBackTime(totalTime, remainingTime)
        }
        super.dispatchDraw(canvas)
    }

    fun startCountdown() {
        if (!isCountingDown) {
            isCountingDown = true
            animator = ValueAnimator().apply {
                setObjectValues(totalTime, 0L)
                setEvaluator { fraction, startValue, endValue ->
                    (startValue as Long - (startValue as Long - endValue as Long) * fraction).toLong()
                }
                interpolator = android.view.animation.LinearInterpolator()
                duration = totalTime
                addUpdateListener { animation ->
                    remainingTime = animation.animatedValue as Long
                    invalidate()
                }
                addListener(object : android.animation.Animator.AnimatorListener {
                    override fun onAnimationStart(animation: android.animation.Animator) {
                        callback?.callBackStart()
                    }

                    override fun onAnimationEnd(animation: android.animation.Animator) {
                        isCountingDown = false
                        callback?.callBackStop()
                    }

                    override fun onAnimationCancel(animation: android.animation.Animator) {
                        isCountingDown = false
                        callback?.callBackStop()
                    }

                    override fun onAnimationRepeat(animation: android.animation.Animator) {}
                })
                start()
            }
        }
    }

    fun stopCountdown() {
        isCountingDown = false
        animator?.cancel()
    }

    fun resetCountdown() {
        remainingTime = totalTime
        animator?.cancel()
        invalidate()
    }

    /**
     * 绿色倒计时
     * @param seconds Long
     */
    fun setTotalTimeGreed(seconds: Long) {
        textPaint?.textSize = 40f
        downStyle = 0
        totalTime = seconds
        remainingTime = totalTime
        invalidate()
    }

    /**
     * 蓝色倒计时
     * @param seconds Long
     */
    fun setTotalTimeBlue(seconds: Long) {
        textPaint?.textSize = 60f
        downStyle = 1
        totalTime = seconds
        remainingTime = totalTime
        invalidate()
    }


    /**
     * 设置倒计时中间状态
     */
    fun setTimeMiddleStatus(ds: Int) {
        drawStatus = ds
        invalidate()
    }

    interface DownTimeCallback {
        fun callBackTime(total: Long, remaining: Long)

        fun callBackStart()
        fun callBackStop()
    }

}