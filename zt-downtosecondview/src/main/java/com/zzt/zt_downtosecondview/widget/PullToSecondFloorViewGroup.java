package com.zzt.zt_downtosecondview.widget;

/**
 * @author: zeting
 * @date: 2025/4/3
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class PullToSecondFloorViewGroup extends ViewGroup {
    private static final String TAG = "Pull";
    private static final int DRAG_THRESHOLD = 200; // 下拉触发阈值，单位：像素
    private float mLastY;
    private boolean isDragging = false;
    private GestureDetector mGestureDetector;

    public PullToSecondFloorViewGroup(Context context) {
        super(context);
        init();
    }

    public PullToSecondFloorViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullToSecondFloorViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (velocityY > 0 && isDragging) { // 向下快速滑动
                    // 执行进入“二楼”页面的逻辑，例如启动新的Activity或显示Fragment
                    startSecondFloorActivity();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = event.getY();
                float dy = currentY - mLastY;
                if (dy > 0) { // 下拉操作
                    isDragging = true;
                    // 这里可以更新视图的位置或者状态，例如将“二楼”页面从隐藏状态逐渐显示出来
                    // 简单示例：只打印下拉距离
                    Log.d(TAG, "下拉距离: " + dy);
                }
                mLastY = currentY;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isDragging = false;
                break;
        }

        return true;
    }

    private void startSecondFloorActivity() {
        // 这里实现启动“二楼”页面的逻辑，例如启动新的Activity
        // 以下是简单示例，实际应用中需要根据项目需求进行修改
        // Intent intent = new Intent(getContext(), SecondFloorActivity.class);
        // getContext().startActivity(intent);
        Log.d(TAG, "进入“二楼”页面");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 布局子视图的逻辑，根据具体需求实现
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 测量子视图的逻辑，根据具体需求实现
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }

        setMeasuredDimension(width, height);
    }


}