package com.zzt.appframe10;

/**
 * @author: zeting
 * @date: 2025/4/7
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MarqueeTextViewV4 extends View {
    private String text;
    private Paint paint;
    private float textWidth;
    private float xPosition;
    private float speed = 2;

    public MarqueeTextViewV4(Context context) {
        super(context);
        init();
    }

    public MarqueeTextViewV4(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MarqueeTextViewV4(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(android.graphics.Color.BLACK);
        paint.setTextSize(30);
        text = "Your text here";
        textWidth = paint.measureText(text);
        xPosition = getWidth();
        postInvalidateDelayed(16);
    }

    public void setText(String text) {
        this.text = text;
        textWidth = paint.measureText(text);
        xPosition = getWidth();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(text, xPosition, getHeight() / 2, paint);
        xPosition -= speed;
        if (xPosition < -textWidth) {
            xPosition = getWidth();
        }
        postInvalidateDelayed(16);
    }
}