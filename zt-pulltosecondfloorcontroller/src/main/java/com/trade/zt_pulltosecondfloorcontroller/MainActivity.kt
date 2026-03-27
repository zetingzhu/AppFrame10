package com.trade.zt_pulltosecondfloorcontroller

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 直接跳转到下拉二楼演示页
        startActivity(Intent(this, PullToSecondFloorActivity::class.java))
        finish()
    }
}