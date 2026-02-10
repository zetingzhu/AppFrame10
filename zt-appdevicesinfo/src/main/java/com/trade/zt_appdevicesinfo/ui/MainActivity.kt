package com.trade.zt_appdevicesinfo.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.blankj.utilcode.util.RomUtils
import com.blankj.utilcode.util.ScreenUtils
import com.trade.zt_appdevicesinfo.R
import kotlin.math.log


class MainActivity : AppCompatActivity() {
    val TAG = MainActivity::class.java.simpleName
    var textView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initView()
    }

    private fun initView() {
        textView = findViewById(R.id.textView)

        var sbStr = StringBuffer()

        // 获取屏幕密度
        val screenDensity = ScreenUtils.getScreenDensity()

        //  获取屏幕密度 DPI
        val screenDensityDpi = ScreenUtils.getScreenDensityDpi()

        // 获取屏幕分辨率
        val screenWidth = ScreenUtils.getScreenWidth()
        val screenHeight = ScreenUtils.getScreenHeight()

        val romInfo = RomUtils.getRomInfo()

        val brand = Build.BRAND
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val device = Build.DEVICE
        val product = Build.PRODUCT
        val board = Build.BOARD
        val bootloader = Build.BOOTLOADER
        val hardware = Build.HARDWARE

        // 获取 Android API 级别 (版本号)
        val sdkInt = Build.VERSION.SDK_INT
        val versionRelease = Build.VERSION.RELEASE

        sbStr.append("获取屏幕密度:" + screenDensity).append("\n")
        sbStr.append("获取屏幕密度 DPI:" + screenDensityDpi).append("\n")
        sbStr.append("屏幕分辨率: ${screenWidth}x${screenHeight}").append("\n")
        sbStr.append("获取 ROM 信息 :" + romInfo.toString()).append("\n")
        sbStr.append("设备品牌: $brand").append("\n")
        sbStr.append("设备制造商: $manufacturer").append("\n")
        sbStr.append("设备型号: $model").append("\n")
        sbStr.append("设备名称: $device").append("\n")
        sbStr.append("产品名称: $product").append("\n")
        sbStr.append("主板: $board").append("\n")
        sbStr.append("Bootloader: $bootloader").append("\n")
        sbStr.append("硬件: $hardware").append("\n")
        sbStr.append("Android 版本: $versionRelease").append("\n")
        sbStr.append("Android API 级别: $sdkInt").append("\n")

        textView?.setText(sbStr.toString())
        Log.w(TAG, sbStr.toString())
    }

    private fun isRightRom(brand: String, manufacturer: String, vararg names: String): Boolean {
        for (name in names) {
            if (brand.contains(name) || manufacturer.contains(name)) {
                return true
            }
        }
        return false
    }
}