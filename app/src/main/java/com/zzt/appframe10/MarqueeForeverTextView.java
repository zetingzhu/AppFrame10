package com.zzt.appframe10;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Choreographer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import java.lang.ref.WeakReference;

/**
 * @author: zeting
 * @date: 2022/6/15
 * 自定义可以一直滚动，并且能够调整滚动时间和滚动间隔时间
 * 使用下面几个属性必须设置
 * android:layout_height="wrap_content"
 * android:background="@color/white"
 * android:ellipsize="none"
 * android:singleLine="true"
 * MARQUEE_DELAY 调整两次滚动间隔时间
 * MARQUEE_DP_PER_SECOND 调整滚动速度
 * float gap 调整两次中间间隔距离
 */
public class MarqueeForeverTextView extends AppCompatTextView {
    private static final String TAG = MarqueeForeverTextView.class.getSimpleName();

    private static final int DEFAULT_BG_COLOR = Color.parseColor("#FFFFFFFF");

    private Marquee mMarquee;
    private boolean mRestartMarquee;
    // 是否可滚动状态
    private boolean isMarquee = true;


    public MarqueeForeverTextView(@NonNull Context context) {
        this(context, null);
    }

    public MarqueeForeverTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeForeverTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
//        setTextDirection(TEXT_DIRECTION_FIRST_STRONG_RTL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (getBackground() == null) {
            throw new RuntimeException("必须设置背景");
        }
        if (getWidth() > 0) {
            mRestartMarquee = true;
        }
    }

    private void restartMarqueeIfNeeded() {
        if (mRestartMarquee) {
            mRestartMarquee = false;
            startMarquee();
        }
    }

    public void setMarquee(boolean marquee) {
        boolean wasStart = isMarquee();
        isMarquee = marquee;
        if (wasStart != marquee) {
            if (marquee) {
                startMarquee();
            } else {
                stopMarquee();
            }
        }
    }


    public boolean isMarquee() {
        return isMarquee;
    }

    private void stopMarquee() {
        setHorizontalFadingEdgeEnabled(false);
        requestLayout();
        invalidate();
        if (mMarquee != null && !mMarquee.isStopped()) {
            mMarquee.stop();
        }
    }

    private void startMarquee() {
        if (canMarquee()) {
            setHorizontalFadingEdgeEnabled(true);
            if (mMarquee == null) {
                mMarquee = new Marquee(this);
                mMarquee.start(-1);
            }
        }
    }

    private boolean canMarquee() {
        int viewWidth = getWidth() - getCompoundPaddingLeft() - getCompoundPaddingRight();
        float lineWidth = 0F;
        if (getLayout() != null) {
            lineWidth = getLayout().getLineWidth(0);
        }
        Log.w(TAG, "000 - 获取文本宽度 textW:" + lineWidth + " viewWidth:" + viewWidth);
        return (mMarquee == null || mMarquee.isStopped())
                && (isFocused() || isSelected() || isMarquee())
                && viewWidth > 0
//                && lineWidth > viewWidth
                ;
    }

    /**
     * 仿照TextView#onDraw()方法
     */
    @Override
    protected void onDraw(Canvas canvas) {
        restartMarqueeIfNeeded();
        super.onDraw(canvas);

        // 再次绘制背景色，覆盖下面由TextView绘制的文本，视情况可以不调用`super.onDraw(canvas);`
        // 如果没有背景色则使用默认颜色
        Drawable background = getBackground();
        if (background != null) {
            background.draw(canvas);
        } else {
            canvas.drawColor(DEFAULT_BG_COLOR);
        }
        canvas.save();
        canvas.translate(0, 0);
        // 实现左右跑马灯
        if (mMarquee != null && mMarquee.isRunning()) {
            final float dx = -mMarquee.getScroll();
            canvas.translate(dx, 0.0F);
        }
        getLayout().draw(canvas, null, null, 0);
        if (mMarquee != null && mMarquee.shouldDrawGhost()) {
//            Log.i(TAG, "滚动计算 绘制下一个 true > mStatus:" + mMarquee.mStatus + " mScroll:" + mMarquee.mScroll + " mGhostStart:" + mMarquee.mGhostStart);
            final float dx = mMarquee.getGhostOffset();
            canvas.translate(dx, 0.0F);
            getLayout().draw(canvas, null, null, 0);
        }
        canvas.restore();
    }


    @Override
    public void computeScroll() {
        super.computeScroll();
        Log.d(TAG, "111 + " + System.currentTimeMillis());
    }


    private static final class Marquee {
        // 跑马灯跑完一次后下一次开启间隔
        private static final int MARQUEE_DELAY = 0;
        // 绘制一次跑多长距离因子，此字段与速度相关
        private static final int MARQUEE_DP_PER_SECOND = 30;
        // 跑马灯状态常量
        private static final byte MARQUEE_STOPPED = 0x0;
        private static final byte MARQUEE_STARTING = 0x1;
        private static final byte MARQUEE_RUNNING = 0x2;
        private static final String METHOD_GET_FRAME_TIME = "getFrameTime";
        // 对TextView进行弱引用
        private final WeakReference<MarqueeForeverTextView> mView;
        // 帧率相关
        private final Choreographer mChoreographer;
        // 状态
        private byte mStatus = MARQUEE_STOPPED;
        // 绘制一次跑多长距离
        private final float mPixelsPerSecond;
        // 最大滚动距离
        private float mMaxScroll;
        // 是否可以绘制右阴影, 右侧淡入淡出效果
        private float mMaxFadeScroll;
        // 尾部文本什么时候开始绘制
        private float mGhostStart;
        // 尾部文本绘制位置偏移量
        private float mGhostOffset;
        // 是否可以绘制左阴影，左侧淡入淡出效果
        private float mFadeStop;
        // 重复限制
        private int mRepeatLimit;
        // 跑动距离
        private float mScroll;
        // 最后一次跑动时间，单位毫秒
        private long mLastAnimationMs;


        Marquee(MarqueeForeverTextView v) {
            final float density = v.getContext().getResources().getDisplayMetrics().density;
            mPixelsPerSecond = MARQUEE_DP_PER_SECOND * density;
            mView = new WeakReference<>(v);
            mChoreographer = Choreographer.getInstance();
        }

        // 跑动中间监听
        private final Choreographer.FrameCallback mTickCallback = new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                tick();
            }
        };

        // 开始跑动监听
        private final Choreographer.FrameCallback mStartCallback = new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                mStatus = MARQUEE_RUNNING;
                mLastAnimationMs = getFrameTime();
                tick();
            }
        };

        private long getFrameTime() {
            return System.currentTimeMillis();
        }

        // 重新跑动监听
        private final Choreographer.FrameCallback mRestartCallback = new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                if (mStatus == MARQUEE_RUNNING) {
                    if (mRepeatLimit >= 0) {
                        mRepeatLimit--;
                    }
                    start(mRepeatLimit);
                }
            }
        };

        void tick() {
            if (mStatus != MARQUEE_RUNNING) {
                return;
            }
            mChoreographer.removeFrameCallback(mTickCallback);
            final MarqueeForeverTextView textView = mView.get();
            if (textView != null && (textView.isFocused() || textView.isSelected() || textView.isMarquee())) {
                long currentMs = getFrameTime();
                long deltaMs = currentMs - mLastAnimationMs;
                mLastAnimationMs = currentMs;
                float deltaPx = deltaMs / 1000F * mPixelsPerSecond;
                mScroll += deltaPx;
                if (mScroll > mMaxScroll) {
                    mScroll = mMaxScroll;
                    mChoreographer.postFrameCallbackDelayed(mRestartCallback, MARQUEE_DELAY);
                } else {
                    mChoreographer.postFrameCallback(mTickCallback);
                }
                textView.invalidate();
            }
        }

        void stop() {
            mStatus = MARQUEE_STOPPED;
            mChoreographer.removeFrameCallback(mStartCallback);
            mChoreographer.removeFrameCallback(mRestartCallback);
            mChoreographer.removeFrameCallback(mTickCallback);
            resetScroll();
        }

        private void resetScroll() {
            mScroll = 0.0F;
            final MarqueeForeverTextView textView = mView.get();
            if (textView != null) textView.invalidate();
        }

        void start(int repeatLimit) {
            if (repeatLimit == 0) {
                stop();
                return;
            }
            mRepeatLimit = repeatLimit;
            final MarqueeForeverTextView textView = mView.get();
            if (textView != null && textView.getLayout() != null) {
                mStatus = MARQUEE_STARTING;
                mScroll = 0.0F;

                // 分别计算左右和上下跑动所需的数据
                int viewWidth = Math.max(textView.getMeasuredWidth(), textView.getWidth() - textView.getCompoundPaddingLeft() - textView.getCompoundPaddingRight());
                float lineWidth = Math.max(textView.getMeasuredWidth(), textView.getLayout().getLineWidth(0));
                float gap = viewWidth / 6.0F;
                mGhostStart = lineWidth - viewWidth + gap;
                mMaxScroll = mGhostStart + viewWidth;
                mGhostOffset = lineWidth + gap;
                mFadeStop = lineWidth + viewWidth / 6.0F;
                mMaxFadeScroll = mGhostStart + lineWidth + lineWidth;

                Log.d(TAG, "滚动计算 viewWidth：" + viewWidth + " lineWidth:" + lineWidth + " mGhostStart:" + mGhostStart + " mMaxScroll:" + mMaxScroll
                        + "\n mGhostOffset:" + mGhostOffset + " mFadeStop:" + mFadeStop + " mMaxFadeScroll:" + mMaxFadeScroll);

                textView.invalidate();
                mChoreographer.postFrameCallback(mStartCallback);
            }
        }

        float getGhostOffset() {
            return mGhostOffset;
        }

        float getScroll() {
            return mScroll;
        }

        float getMaxFadeScroll() {
            return mMaxFadeScroll;
        }

        boolean shouldDrawLeftFade() {
            return mScroll <= mFadeStop;
        }

        boolean shouldDrawTopFade() {
            return mScroll <= mFadeStop;
        }

        boolean shouldDrawGhost() {
            return mStatus == MARQUEE_RUNNING && mScroll > mGhostStart;
        }

        boolean isRunning() {
            return mStatus == MARQUEE_RUNNING;
        }

        boolean isStopped() {
            return mStatus == MARQUEE_STOPPED;
        }
    }
}
