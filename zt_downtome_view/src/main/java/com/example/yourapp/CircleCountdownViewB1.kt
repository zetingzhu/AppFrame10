package com.example.yourapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import java.util.concurrent.TimeUnit

class CircleCountdownViewB1(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
        color = android.graphics.Color.BLUE
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 60f
        color = android.graphics.Color.BLACK
        textAlign = Paint.Align.CENTER
    }

    private val rectF = RectF()
    private var totalTime = 60000L // 总时间，单位毫秒
    private var remainingTime = totalTime
    private var isCountingDown = false


    private val handler = Handler(Looper.getMainLooper())
    private val countdownRunnable = object : Runnable {
        override fun run() {
            if (remainingTime > 0 && isCountingDown) {
                remainingTime -= 50 // 每 1 毫秒更新一次，每次减少 1 毫秒
                invalidate()
                handler.postDelayed(this, 50)
            }
        }
    }

    private val checkmarkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 10f
        color = android.graphics.Color.GREEN
        style = Paint.Style.STROKE
    }

    private val errorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 10f
        color = android.graphics.Color.RED
        style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)



        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (Math.min(width, height) / 2f) - progressPaint.strokeWidth / 2f

        rectF.set(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )

        // 绘制圆形进度条
        val sweepAngle = (remainingTime.toFloat() / totalTime.toFloat()) * 360f
        canvas.drawArc(rectF, 270f, -sweepAngle, false, progressPaint)

        if (remainingTime > 0) {
            // 绘制倒计时文本
            val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTime)
            val seconds = (TimeUnit.MILLISECONDS.toSeconds(remainingTime) - TimeUnit.MINUTES.toSeconds(minutes))
            val timeText = String.format("%02d:%02d", minutes, seconds)
            canvas.drawText(timeText, centerX, centerY - (textPaint.descent() + textPaint.ascent()) / 2, textPaint)
        } else {
            // 随机选择绘制对勾或错误图案
            if (kotlin.random.Random.nextBoolean()) {
                // 绘制对勾图案
                val checkmarkSize = 60f
                val startX = centerX - checkmarkSize / 2
                val startY = centerY
                val middleX = centerX
                val middleY = centerY + checkmarkSize / 2
                val endX = centerX + checkmarkSize / 2
                val endY = centerY - checkmarkSize / 4

                canvas.drawLine(startX, startY, middleX, middleY, checkmarkPaint)
                canvas.drawLine(middleX, middleY, endX, endY, checkmarkPaint)
            } else {
                // 绘制错误图案
                val errorSize = 60f
                val startX1 = centerX - errorSize / 2
                val startY1 = centerY - errorSize / 2
                val endX1 = centerX + errorSize / 2
                val endY1 = centerY + errorSize / 2

                val startX2 = centerX - errorSize / 2
                val startY2 = centerY + errorSize / 2
                val endX2 = centerX + errorSize / 2
                val endY2 = centerY - errorSize / 2

                canvas.drawLine(startX1, startY1, endX1, endY1, errorPaint)
                canvas.drawLine(startX2, startY2, endX2, endY2, errorPaint)
            }
        }
    }

    fun startCountdown() {
        if (!isCountingDown) {
            isCountingDown = true
            handler.post(countdownRunnable)
        }
    }

    fun stopCountdown() {
        isCountingDown = false
        handler.removeCallbacks(countdownRunnable)
    }

    fun resetCountdown() {
        remainingTime = totalTime
        invalidate()
    }

    fun setTotalTime(seconds: Long) {
        totalTime = seconds * 1000
        remainingTime = totalTime
        invalidate()
    }
}