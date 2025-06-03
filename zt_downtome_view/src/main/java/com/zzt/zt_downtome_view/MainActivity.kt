package com.zzt.zt_downtome_view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.yourapp.CircleCountdownView

class MainActivity : AppCompatActivity() {
    var circleCountdownView: CircleCountdownView? = null
    var vdw_2: CircleCountdownView? = null
    var vdw_3: CircleCountdownView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        circleCountdownView = findViewById(R.id.circleCountdownView)
        vdw_2 = findViewById(R.id.vdw_2)
        vdw_3 = findViewById(R.id.vdw_3)
        // 设置总时间为 60 秒
        circleCountdownView?.setTotalTimeGreed(10 * 1000L)
        vdw_2?.setTotalTimeGreed(10 * 1000L)
        vdw_3?.setTotalTimeGreed(10 * 1000L)
    }

    fun startCountdown(view: android.view.View) {
        circleCountdownView?.startCountdown()
        vdw_2?.startCountdown()
        vdw_3?.startCountdown()
    }

    fun stopCountdown(view: android.view.View) {
        circleCountdownView?.stopCountdown()
        vdw_2?.stopCountdown()
        vdw_3?.stopCountdown()
    }

    fun resetCountdown(view: android.view.View) {
        circleCountdownView?.resetCountdown()
        vdw_2?.resetCountdown()
        vdw_3?.resetCountdown()
    }

    fun setRight(view: android.view.View) {
        circleCountdownView?.setTimeMiddleStatus(1)
        vdw_2?.setTimeMiddleStatus(1)
        vdw_3?.setTimeMiddleStatus(1)
    }

    fun setError(view: android.view.View) {
        circleCountdownView?.setTimeMiddleStatus(2)
        vdw_2?.setTimeMiddleStatus(2)
        vdw_3?.setTimeMiddleStatus(2)
    }
}