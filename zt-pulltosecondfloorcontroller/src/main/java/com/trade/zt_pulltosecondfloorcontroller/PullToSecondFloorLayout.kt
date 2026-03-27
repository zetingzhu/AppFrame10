package com.trade.zt_pulltosecondfloorcontroller

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator

/**
 * 下拉二楼组件
 *
 * 使用方式 (XML):
 * ```xml
 * <PullToSecondFloorLayout
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent">
 *
 *     <!-- 第一个子 View: 二楼内容 (底层) -->
 *     <FrameLayout ... />
 *
 *     <!-- 第二个子 View: 一楼内容 (上层主页面) -->
 *     <FrameLayout ... />
 * </PullToSecondFloorLayout>
 * ```
 *
 * 代码中通过 [setHeaderView] 设置自定义的下拉头部视图 (70dp 以内展示区域)。
 */
class PullToSecondFloorLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    // ── 状态枚举 ──
    enum class PullState {
        /** 空闲 */
        IDLE,
        /** 下拉刷新 (0 ~ refreshThreshold) */
        PULL_TO_REFRESH,
        /** 松手刷新 (>= refreshThreshold, 二楼关闭时或 < enterThreshold) */
        RELEASE_TO_REFRESH,
        /** 松手刷新 + 下拉进入活动页 (refreshThreshold ~ enterThreshold, 二楼开启) */
        RELEASE_REFRESH_AND_ENTER,
        /** 松手进入活动页 (> enterThreshold, 二楼开启) */
        RELEASE_TO_ENTER,
        /** 刷新中 */
        REFRESHING,
        /** 已进入二楼 */
        SECOND_FLOOR,
        /** 首次自动曝光 */
        AUTO_EXPOSE
    }

    // ── dp 阈值 (会在 init 转 px) ──
    private var refreshThresholdPx = dp2px(70f)
    private var enterThresholdPx = dp2px(110f)
    private var maxPullNoSecondFloorPx = dp2px(150f)
    private var autoExposeHeightPx = dp2px(300f)

    // ── 文案 ──
    private val textPullToRefresh = "下拉刷新"
    private val textReleaseToRefresh = "松手刷新"
    private val textReleaseRefreshAndEnter = "松手刷新, 下拉进入活动页"
    private val textReleaseToEnter = "松手进入活动页"
    private val textRefreshing = "刷新中"
    private val textCollapseTimerSuffix = "秒上滑收起"

    // ── 核心状态 ──
    var currentState: PullState = PullState.IDLE
        private set

    /** 是否允许进入二楼。false 则最多拉到 maxPullNoSecondFloor，松手只做刷新 */
    var secondFloorEnabled: Boolean = true

    /** 首次自动曝光开关 */
    var enableAutoExpose: Boolean = true

    // ── 偏移量 (px) ──
    private var offsetY: Float = 0f

    // ── 触摸 ──
    private var lastTouchY: Float = 0f
    private var isDragging: Boolean = false
    private val dampingFactor = 0.5f

    // ── 子视图引用 ──
    private var secondFloorView: View? = null  // 第一个子 View
    private var maskView: View? = null         // 中间蒙版视图 (第一层背景)
    private var firstFloorView: View? = null   // 第二个子 View (或第三个, 如有蒙版)
    private var headerView: View? = null       // 自定义下拉头部视图

    // ── 回调 ──
    private var onRefreshListener: OnRefreshListener? = null
    private var onStateChangeListener: OnStateChangeListener? = null
    private var onPullProgressListener: OnPullProgressListener? = null

    // ── 自动曝光 ──
    private var autoExposeTriggered = false
    private var countdownTimer: CountDownTimer? = null

    // ─────────────────────────────────────────
    // 回调接口
    // ─────────────────────────────────────────
    interface OnRefreshListener {
        fun onRefresh()
    }

    interface OnStateChangeListener {
        /**
         * 状态改变回调
         * @param state 当前状态
         * @param statusText 当前状态对应的文案
         */
        fun onStateChanged(state: PullState, statusText: String)
    }

    interface OnPullProgressListener {
        /**
         * 下拉进度回调
         * @param offsetPx 当前下拉偏移量 (px)
         * @param progress 在刷新阈值范围内的进度 (0 ~ 1+)
         */
        fun onPullProgress(offsetPx: Float, progress: Float)
    }

    // ─────────────────────────────────────────
    // 公开 API
    // ─────────────────────────────────────────
    fun setOnRefreshListener(listener: OnRefreshListener) {
        onRefreshListener = listener
    }

    fun setOnStateChangeListener(listener: OnStateChangeListener) {
        onStateChangeListener = listener
    }

    fun setOnPullProgressListener(listener: OnPullProgressListener) {
        onPullProgressListener = listener
    }

    /**
     * 设置自定义的下拉头部视图 (70dp 刷新区域)
     * 该视图会被添加到组件内部, 位于一楼上方
     */
    fun setHeaderView(view: View) {
        headerView?.let { removeView(it) }
        headerView = view
        addView(view)
        requestLayout()
    }

    /**
     * 设置中间蒙版视图 (位于一楼和二楼之间)
     * 作为一楼背景使用。当 secondFloorEnabled=true 且下拉超过 110dp 时蒙版消失, 显示二楼。
     * 也可以在 XML 中作为第二个子 View (共3个子View: 二楼、蒙版、一楼)
     */
    fun setMaskView(view: View) {
        maskView?.let { removeView(it) }
        maskView = view
        addView(view)
        requestLayout()
    }

    /** 刷新完成后由外部调用 */
    fun finishRefresh() {
        if (currentState == PullState.REFRESHING) {
            animateOffsetTo(0f) {
                setState(PullState.IDLE)
            }
        }
    }

    /** 手动收起二楼, 回到一楼 */
    fun collapseSecondFloor() {
        if (currentState == PullState.SECOND_FLOOR || currentState == PullState.AUTO_EXPOSE) {
            countdownTimer?.cancel()
            animateOffsetTo(0f) {
                setState(PullState.IDLE)
            }
        }
    }

    /** 手动触发自动曝光 */
    fun triggerAutoExpose() {
        if (currentState == PullState.IDLE && secondFloorEnabled) {
            performAutoExpose()
        }
    }

    // ─────────────────────────────────────────
    // 布局
    // ─────────────────────────────────────────
    override fun onFinishInflate() {
        super.onFinishInflate()
        // 获取 XML 中定义的子视图 (排除通过代码添加的 headerView 和 maskView)
        val xmlChildren = (0 until childCount).map { getChildAt(it) }
            .filter { it !== headerView && it !== maskView }
        when {
            xmlChildren.size >= 3 -> {
                // 3个子View: 二楼、蒙版、一楼
                secondFloorView = xmlChildren[0]
                maskView = xmlChildren[1]
                firstFloorView = xmlChildren[2]
            }
            xmlChildren.size >= 2 -> {
                // 2个子View: 二楼、一楼 (无蒙版)
                secondFloorView = xmlChildren[0]
                firstFloorView = xmlChildren[1]
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        // 测量二楼
        secondFloorView?.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
        // 测量蒙版
        maskView?.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
        // 测量一楼
        firstFloorView?.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
        // 测量 header (使用自身需要的高度，而不是 offsetY)
        headerView?.let {
            it.measure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
        }

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val oy = offsetY.toInt()

        // 二楼: 始终铺满底层
        secondFloorView?.layout(0, 0, measuredWidth, measuredHeight)

        // 蒙版: 铺满, 位于二楼上方、一楼下方
        maskView?.layout(0, 0, measuredWidth, measuredHeight)

        // Header: 在一楼上方, 从 oy - headerHeight 到 oy
        headerView?.let {
            val h = it.measuredHeight
            it.layout(0, oy - h, measuredWidth, oy)
        }

        // 一楼: 偏移 offsetY 向下
        firstFloorView?.layout(0, oy, measuredWidth, oy + measuredHeight)
    }

    // ─────────────────────────────────────────
    // 触摸事件
    // ─────────────────────────────────────────
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (currentState == PullState.REFRESHING ||
            currentState == PullState.SECOND_FLOOR ||
            currentState == PullState.AUTO_EXPOSE
        ) {
            // 二楼状态下允许点击子 View (如按钮)
            return currentState != PullState.SECOND_FLOOR
        }

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchY = ev.y
                isDragging = false
            }
            MotionEvent.ACTION_MOVE -> {
                val dy = ev.y - lastTouchY
                if (dy > 10 && !isDragging) {
                    // 向下拖拽, 只在内容无法继续向上滚动时拦截
                    if (!canContentScrollUp()) {
                        isDragging = true
                        return true
                    }
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (currentState == PullState.REFRESHING || currentState == PullState.AUTO_EXPOSE) {
            return false
        }

        // 二楼状态下, 拦截事件但不处理收回逻辑（交由外部按钮控制）
        if (currentState == PullState.SECOND_FLOOR) {
            return true
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchY = event.y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dy = event.y - lastTouchY
                lastTouchY = event.y

                // 计算阻尼拉伸
                var newOffset = 0f
                if (!secondFloorEnabled && offsetY >= maxPullNoSecondFloorPx && dy > 0) {
                    // 如果二楼关闭, 并且已经拉到了限制高度以上, 继续下拉时阻尼变大 ("拉不动"的效果)
                    val heavyDamping = 0.1f // 阻尼极大
                    newOffset = offsetY + dy * heavyDamping
                } else {
                    // 普通阻尼
                    newOffset = offsetY + dy * dampingFactor
                }
                
                newOffset = newOffset.coerceAtLeast(0f)

                setOffset(newOffset)
                setState(resolveDragState(offsetY))
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                handleRelease()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    // ─────────────────────────────────────────
    // 内部逻辑
    // ─────────────────────────────────────────
    private fun canContentScrollUp(): Boolean {
        return firstFloorView?.canScrollVertically(-1) ?: false
    }

    private fun resolveDragState(currentOffset: Float): PullState {
        if (!secondFloorEnabled) {
            return if (currentOffset <= refreshThresholdPx) PullState.PULL_TO_REFRESH
            else PullState.RELEASE_TO_REFRESH
        }
        return when {
            currentOffset <= refreshThresholdPx -> PullState.PULL_TO_REFRESH
            currentOffset <= enterThresholdPx -> PullState.RELEASE_REFRESH_AND_ENTER
            else -> PullState.RELEASE_TO_ENTER
        }
    }

    private fun handleRelease() {
        when (currentState) {
            PullState.PULL_TO_REFRESH -> {
                // 没到阈值 → 回弹
                animateOffsetTo(0f) {
                    setState(PullState.IDLE)
                }
            }
            PullState.RELEASE_TO_REFRESH,
            PullState.RELEASE_REFRESH_AND_ENTER -> {
                // 触发刷新, 回弹到刷新阈值位置
                setState(PullState.REFRESHING)
                animateOffsetTo(refreshThresholdPx) {
                    onRefreshListener?.onRefresh()
                }
            }
            PullState.RELEASE_TO_ENTER -> {
                // 进入二楼: 一楼整个移出视图
                setState(PullState.SECOND_FLOOR)
                animateOffsetTo(measuredHeight.toFloat())
            }
            else -> {
                animateOffsetTo(0f) {
                    setState(PullState.IDLE)
                }
            }
        }
    }

    private fun setState(newState: PullState) {
        if (currentState != newState) {
            currentState = newState
            val text = when (newState) {
                PullState.IDLE -> ""
                PullState.PULL_TO_REFRESH -> textPullToRefresh
                PullState.RELEASE_TO_REFRESH -> textReleaseToRefresh
                PullState.RELEASE_REFRESH_AND_ENTER -> textReleaseRefreshAndEnter
                PullState.RELEASE_TO_ENTER -> textReleaseToEnter
                PullState.REFRESHING -> textRefreshing
                PullState.SECOND_FLOOR -> ""
                PullState.AUTO_EXPOSE -> ""
            }
            onStateChangeListener?.onStateChanged(newState, text)
        }
    }

    private fun setOffset(newOffset: Float) {
        offsetY = newOffset
        // 通知下拉进度
        val progress = if (refreshThresholdPx > 0) offsetY / refreshThresholdPx else 0f
        onPullProgressListener?.onPullProgress(offsetY, progress)

        // 更新蒙版和 Header alpha:
        // - 二楼关闭时蒙版和 Header 始终可见 (alpha=1)
        // - 二楼开启时, 在 refreshThreshold ~ enterThreshold 之间线性淡出
        // - 超过 enterThreshold 时蒙版和 Header 完全透明
        val targetAlpha = if (!secondFloorEnabled) {
            1f
        } else {
            when {
                offsetY <= refreshThresholdPx -> 1f
                offsetY >= enterThresholdPx -> 0f
                else -> {
                    // 线性插值: refreshThreshold → enterThreshold 映射到 1 → 0
                    1f - (offsetY - refreshThresholdPx) / (enterThresholdPx - refreshThresholdPx)
                }
            }
        }

        maskView?.alpha = targetAlpha
        // 只有在完全进入二楼时才隐藏 header
        headerView?.alpha = if (currentState == PullState.SECOND_FLOOR) 0f else 1f

        // 重新测量 header + 重新布局
        requestLayout()
    }

    private fun animateOffsetTo(target: Float, onEnd: (() -> Unit)? = null) {
        val animator = ValueAnimator.ofFloat(offsetY, target)
        animator.duration = 300
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { anim ->
            setOffset(anim.animatedValue as Float)
        }
        if (onEnd != null) {
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onEnd()
                }
            })
        }
        animator.start()
    }

    // ─────────────────────────────────────────
    // 自动曝光
    // ─────────────────────────────────────────
    private fun performAutoExpose() {
        if (autoExposeTriggered) return
        autoExposeTriggered = true
        setState(PullState.AUTO_EXPOSE)

        // 先动画到 300dp 位置
        animateOffsetTo(autoExposeHeightPx) {
            // 开始 3 秒倒计时
            countdownTimer = object : CountDownTimer(3000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val seconds = (millisUntilFinished / 1000 + 1).toInt()
                    val text = "${seconds}${textCollapseTimerSuffix}"
                    onStateChangeListener?.onStateChanged(PullState.AUTO_EXPOSE, text)
                }

                override fun onFinish() {
                    animateOffsetTo(0f) {
                        setState(PullState.IDLE)
                    }
                }
            }.start()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // 首次附加到窗口时触发自动曝光
        if (enableAutoExpose && secondFloorEnabled && !autoExposeTriggered) {
            post { performAutoExpose() }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        countdownTimer?.cancel()
    }

    // ─────────────────────────────────────────
    // 工具
    // ─────────────────────────────────────────
    private fun dp2px(dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }
}
