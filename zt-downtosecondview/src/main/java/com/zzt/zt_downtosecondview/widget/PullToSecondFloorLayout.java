package com.zzt.zt_downtosecondview.widget;

/**
 * @author: zeting
 * @date: 2025/4/3
 */
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

public class PullToSecondFloorLayout extends ViewGroup {

    private View mContentView;
    private View mSecondFloorView;
    private int mTouchSlop;
    private float mDownY;
    private float mCurrentY;
    private boolean mIsDragging = false;
    private int mSecondFloorHeight = 500; // 二楼页面的高度 (dp)
    private boolean mInSecondFloor = false;
    private OnSecondFloorActionListener mListener;
    private ValueAnimator mScrollAnimator;

    public PullToSecondFloorLayout(@NonNull Context context) {
        this(context, null);
    }

    public PullToSecondFloorLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToSecondFloorLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mSecondFloorHeight = (int) (mSecondFloorHeight * getResources().getDisplayMetrics().density);
    }

    public void setOnSecondFloorActionListener(OnSecondFloorActionListener listener) {
        mListener = listener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 2) {
            throw new IllegalStateException("PullToSecondFloorLayout can only have two direct child views.");
        }
        if (getChildCount() == 2) {
            mContentView = getChildAt(0);
            mSecondFloorView = getChildAt(1);
            mSecondFloorView.setVisibility(GONE); // 初始隐藏二楼页面
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mContentView != null) {
            measureChildWithMargins(mContentView, widthMeasureSpec, 0, heightMeasureSpec, 0);
        }
        if (mSecondFloorView != null) {
            measureChildWithMargins(mSecondFloorView, widthMeasureSpec, 0, MeasureSpec.makeMeasureSpec(mSecondFloorHeight, MeasureSpec.EXACTLY), 0);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mContentView != null) {
            mContentView.layout(left, 0, right, mContentView.getMeasuredHeight());
        }
        if (mSecondFloorView != null) {
            mSecondFloorView.layout(left, -mSecondFloorHeight, right, 0); // 初始在内容上方隐藏
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mInSecondFloor) {
            return super.onInterceptTouchEvent(ev); // 二楼页面显示时，正常处理触摸事件
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getY();
                mCurrentY = mDownY;
                mIsDragging = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = ev.getY();
                float dy = moveY - mDownY;
                if (dy > mTouchSlop && !canContentViewScrollUp()) {
                    mIsDragging = true;
                    mDownY = moveY; // 重新设置 downY，避免累积误差
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsDragging = false;
                break;
        }
        return mIsDragging;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mInSecondFloor && mSecondFloorView != null) {
            // 在二楼页面时，如果点击在内容区域上方，则尝试关闭二楼
            if (event.getAction() == MotionEvent.ACTION_UP && event.getY() < 0) {
                scrollToSecondFloor(false);
                return true;
            }
            return super.onTouchEvent(event);
        }

        if (!mIsDragging) {
            return super.onTouchEvent(event);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mCurrentY = event.getY();
                float dy = mCurrentY - mDownY;
                if (dy > 0) {
                    // 下拉，移动二楼页面
                    int targetScrollY = Math.min(0, (int) (dy - mSecondFloorHeight));
                    scrollTo(0, targetScrollY);
                } else {
                    // 上滑，恢复
                    scrollTo(0, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsDragging = false;
                if (getScrollY() < -mSecondFloorHeight / 2) {
                    // 下拉超过一半，进入二楼
                    scrollToSecondFloor(true);
                } else {
                    // 回弹
                    scrollToSecondFloor(false);
                }
                break;
        }
        return true;
    }

    private boolean canContentViewScrollUp() {
        if (mContentView == null) {
            return false;
        }
        return ViewCompat.canScrollVertically(mContentView, -1);
    }

    private void scrollToSecondFloor(boolean open) {
        if (mScrollAnimator != null && mScrollAnimator.isRunning()) {
            mScrollAnimator.cancel();
        }

        int targetScrollY = open ? -mSecondFloorHeight : 0;
        mScrollAnimator = ValueAnimator.ofInt(getScrollY(), targetScrollY);
        mScrollAnimator.setDuration(300);
        mScrollAnimator.setInterpolator(new DecelerateInterpolator());
        mScrollAnimator.addUpdateListener(animation -> scrollTo(0, (int) animation.getAnimatedValue()));
        mScrollAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mInSecondFloor = open;
                if (mSecondFloorView != null) {
                    mSecondFloorView.setVisibility(open ? VISIBLE : GONE);
                }
                if (mListener != null) {
                    if (open) {
                        mListener.onEnterSecondFloor();
                    } else {
                        mListener.onLeaveSecondFloor();
                    }
                }
            }
        });
        mScrollAnimator.start();
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    public interface OnSecondFloorActionListener {
        void onEnterSecondFloor();
        void onLeaveSecondFloor();
    }
}