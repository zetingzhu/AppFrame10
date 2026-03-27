package com.trade.zt_pulltosecondfloorcontroller

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast

/**
 * 下拉二楼组件演示页面
 */
class PullToSecondFloorActivity : Activity() {

    private lateinit var pullLayout: PullToSecondFloorLayout
    private lateinit var tvDebugOffset: TextView
    private lateinit var headerText: TextView
    private lateinit var headerProgress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pull_to_second_floor)

        pullLayout = findViewById(R.id.pull_layout)
        tvDebugOffset = findViewById(R.id.tv_debug_offset)
        val switchSecondFloor = findViewById<Switch>(R.id.switch_second_floor)
        
        findViewById<View>(R.id.btn_back_to_home).setOnClickListener {
            pullLayout.collapseSecondFloor()
        }

        // ── 设置自定义 Header (70dp 内展示的刷新视图) ──
        val headerView = LayoutInflater.from(this)
            .inflate(R.layout.view_pull_header, pullLayout, false)
        headerText = headerView.findViewById(R.id.header_text)
        headerProgress = headerView.findViewById(R.id.header_progress)
        pullLayout.setHeaderView(headerView)

        // ── 开关 ──
        switchSecondFloor.setOnCheckedChangeListener { _, isChecked ->
            pullLayout.secondFloorEnabled = isChecked
            Toast.makeText(
                this,
                if (isChecked) "已开启二楼入口" else "已关闭二楼入口 (最多下拉150dp)",
                Toast.LENGTH_SHORT
            ).show()
        }

        // ── 刷新回调 ──
        pullLayout.setOnRefreshListener(object : PullToSecondFloorLayout.OnRefreshListener {
            override fun onRefresh() {
                // 模拟异步刷新, 2秒后完成
                pullLayout.postDelayed({
                    pullLayout.finishRefresh()
                    Toast.makeText(this@PullToSecondFloorActivity, "刷新完成", Toast.LENGTH_SHORT)
                        .show()
                }, 2000)
            }
        })

        // ── 状态变化回调 → 更新 Header ──
        pullLayout.setOnStateChangeListener(object : PullToSecondFloorLayout.OnStateChangeListener {
            override fun onStateChanged(
                state: PullToSecondFloorLayout.PullState,
                statusText: String
            ) {
                headerText.text = statusText

                // 刷新中显示进度条
                headerProgress.visibility = if (state == PullToSecondFloorLayout.PullState.REFRESHING) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        })

        // ── 下拉进度回调 → 更新 debug 信息 ──
        pullLayout.setOnPullProgressListener(object : PullToSecondFloorLayout.OnPullProgressListener {
            override fun onPullProgress(offsetPx: Float, progress: Float) {
                val offsetDp = (offsetPx / resources.displayMetrics.density).toInt()
                tvDebugOffset.text = "高度${offsetDp}dp"
            }
        })
    }
}
