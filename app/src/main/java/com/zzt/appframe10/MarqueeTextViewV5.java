package com.zzt.appframe10;

/**
 * @author: zeting
 * @date: 2025/4/7
 * 跑马灯 5
 */

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class MarqueeTextViewV5 extends View {

    private String text;
    private TextPaint textPaint;
    private Layout layout;
    private int x;
    private int y;
    private int scrollSpeed = 2; // 滚动速度
    private boolean isScrolling = false;
    private float textWidth;

    public MarqueeTextViewV5(Context context) {
        super(context);
        init();
    }

    public MarqueeTextViewV5(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MarqueeTextViewV5(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        textPaint = new TextPaint();
        textPaint.setColor(0xFF000000); // 文字颜色
        textPaint.setTextSize(30); // 文字大小
        text = "这是一个自定义跑马灯效果的示例文本，可以是短文字也可以是长文字。";
        x = 0;
        y = 50; // 文字垂直位置
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        layout = new StaticLayout(text, textPaint, w, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        textWidth = textPaint.measureText(text);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (layout != null) {
            canvas.save();
            canvas.translate(x, y);
            layout.draw(canvas);
            canvas.restore();
            if (isScrolling) {
                x -= scrollSpeed;
                if (x + Math.min(textWidth, layout.getWidth()) < 0) {
                    x = getWidth();
                }
                invalidate();
            }
        }
    }


    public void startScrolling() {
        isScrolling = true;
        invalidate();
    }

    public void stopScrolling() {
        isScrolling = false;
    }

    @Override
    public boolean isFocused() {
        return true; // 确保View总是处于焦点状态
    }
}
