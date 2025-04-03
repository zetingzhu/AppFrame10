//package com.zzt.zt_downtosecondview.widget
//
///**
// * @author: zeting
// * @date: 2025/4/3
// *
// */
//import android.animation.ValueAnimator
//import android.content.Context
//import android.util.AttributeSet
//import android.view.MotionEvent
//import android.view.View
//import android.view.ViewConfiguration
//import android.view.ViewGroup
//
//class PullToSecondFloorLayoutKotlin(
//    context: Context,
//    attrs: AttributeSet? = null,
//    defStyleAttr: Int = 0
//) :
//    ViewGroup(context, attrs, defStyleAttr) {
//
//    private var mContentView: View? = null
//    private var mSecondFloorView: View? = null
//    private val mTouchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop
//    private var mDownY: Float = 0f
//    private var mCurrentY: Float = 0f
//    private var mIsDragging: Boolean = false
//    private var mSecondFloorHeight: Int = (500 * resources.displayMetrics.density).toInt()
//    private var mInSecondFloor: Boolean = false
//    private var mListener: PullToSecondFloorLayout.OnSecondFloorActionListener? = null
//    private var mScrollAnimator: ValueAnimator? = null
//
//    fun setOnSecondFloorActionListener(listener: PullToSecondFloorLayout.OnSecondFloorActionListener) {
//        mListener = listener
//    }
//
//    override fun onFinishInflate() {
//        super.onFinishInflate()
//        if (childCount > 2) {
//            throw IllegalStateException("PullToSecondFloorLayout can only have two direct child views.")
//        }
//        if (childCount == 2) {
//            mContentView = getChildAt(0)
//            mSecondFloorView = getChildAt(1)
//            mSecondFloorView?.visibility = GONE // 初始隐藏二楼页面
//        }
//    }
//
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        mContentView?.let {
//            measureChildWithMargins(it, widthMeasureSpec, 0, heightMeasureSpec, 0)
//        }
//        mSecondFloorView?.let {
//            measureChildWithMargins(
//                it,
//                widthMeasureSpec,
//                0,
//                MeasureSpec.makeMeasureSpec(mSecondFloorHeight, MeasureSpec.EXACTLY),
//                0
//            )
//        }
//    }
//
//    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
//        mContentView?.let {
//            it.layout(left, 0, right, it.measuredHeight)
//        }
//        mSecondFloorView?.let {
//            it.layout(left, -mSecondFloorHeight, right, 0) // 初始在内容上方隐藏
//        }
//    }
//
//    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
//        if (mInSecondFloor) {
//            return super.onInterceptTouchEvent(ev) // 二楼页面显示时，正常处理触摸事件
//        }
//
//        when (ev.action) {
//            MotionEvent.ACTION_DOWN -> {
//                mDownY = ev.y
//                mCurrentY = mDownY
//            }
//        }
//        return super.onInterceptTouchEvent(ev)
//    }
//}