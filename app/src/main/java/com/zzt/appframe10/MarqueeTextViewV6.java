package com.zzt.appframe10;

/**
 * @author: zeting
 * @date: 2025/4/7
 * 跑马灯 6
 */

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

public class MarqueeTextViewV6 extends View {

    private String mText = "";
    private TextPaint mPaint;
    private float mTextWidth;
    private int mViewWidth;
    private float mScrollX = 0f;
    private ValueAnimator mAnimator;
    private boolean mIsMarqueeEnabled = false;
    private int mSpeed = 50; // 滚动速度，单位：像素/秒

    public MarqueeTextViewV6(Context context) {
        super(context);
        init();
    }

    public MarqueeTextViewV6(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MarqueeTextViewV6(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.LEFT);
        // 可以根据需要设置字体大小、颜色等
        mPaint.setTextSize(30);
        mPaint.setColor(Color.parseColor("#0054FF"));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        measureText();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        measureText();
        startOrStopMarquee();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mTextWidth > 0) {
            float startX = mScrollX;
            canvas.drawText(mText, startX, getBaselineA(), mPaint);

            // 绘制第二份文本，实现无缝滚动
            if (mTextWidth > mViewWidth) {
                canvas.drawText(mText, startX + mTextWidth, getBaselineA(), mPaint);
            } else if (mIsMarqueeEnabled) {
                // 短文本也需要循环滚动
                canvas.drawText(mText, startX + mTextWidth, getBaselineA(), mPaint);
            }
        }
    }

    private int getBaselineA() {
        Paint.FontMetricsInt fontMetricsInt = mPaint.getFontMetricsInt();
        return getHeight() / 2 - (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.top;
    }

    public void setText(String text) {
        if (text == null) {
            text = "";
        }
        mText = text;
        measureText();
        requestLayout(); // 重新测量
        startOrStopMarquee();
    }

    public void setTextSize(float size) {
        mPaint.setTextSize(size);
        measureText();
        requestLayout();
        startOrStopMarquee();
    }

    public void setTextColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    public void setSpeed(int speed) {
        mSpeed = speed;
        startOrStopMarquee();
    }

    public void startMarquee() {
        mIsMarqueeEnabled = true;
        startOrStopMarquee();
    }

    public void stopMarquee() {
        mIsMarqueeEnabled = false;
        startOrStopMarquee();
    }

    private void startOrStopMarquee() {
        if (mIsMarqueeEnabled && mTextWidth > 0 && mViewWidth > 0) {
            if (mAnimator != null && mAnimator.isRunning()) {
                mAnimator.cancel();
            }

            int duration;
            float targetScrollX;

            if (mTextWidth > mViewWidth) {
                // 长文本，滚动一个文本的宽度
                duration = (int) (mTextWidth * 1000 / mSpeed);
                targetScrollX = -mTextWidth;
            } else {
                // 短文本，滚动一个文本的宽度
                duration = (int) (mTextWidth * 2 * 1000 / mSpeed); // 滚动两个文本宽度
                targetScrollX = -mTextWidth;
            }

            mAnimator = ValueAnimator.ofFloat(0, targetScrollX);
            mAnimator.setDuration(duration);
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mAnimator.setRepeatMode(ValueAnimator.RESTART);
            mAnimator.addUpdateListener(animation -> {
                mScrollX = (float) animation.getAnimatedValue();
                invalidate();
            });
            mAnimator.start();
        } else {
            if (mAnimator != null) {
                mAnimator.cancel();
                mScrollX = 0;
                invalidate();
            }
        }
    }

    private void measureText() {
        mTextWidth = mPaint.measureText(mText);
    }
}