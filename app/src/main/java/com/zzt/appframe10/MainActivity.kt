package com.zzt.appframe10

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.zzt.appframe10.MarqueeTextViewV3.MarqueeMode

class MainActivity : AppCompatActivity() {

    var tv_guide_authentication: MarqueeForeverTextView? = null;
    var tv_marquee2: MarqueeForeverTextView? = null;
    var strShort = "短文字。"
    var strLong =
        "长文本。 1 2 3 4 5 6 7 8 9 0 11 12 13 14 15 16 17 18 19 20 21 222 23 24 25 26 27 28 29 3031 32 33 34 35 36 37 38 39 40 A S D f g h j k l  "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var text_title: TextView = findViewById(R.id.text_title)

//        text_title.setText(resources.getString(R.string.s39_78, "qqq"))
//        text_title.setText(resources.getString(R.string.s39_77, "qqq"))
//        text_title.setText(resources.getString(R.string.s39_76, "qqq"))
        text_title.setText(resources.getString(R.string.s39_75))
//        text_title.setText(resources.getString(R.string.s39_74 ))
        initView();
    }

    private fun initView() {

        tv_guide_authentication = findViewById(R.id.tv_guide_authentication)
        tv_guide_authentication?.setText(resources.getString(R.string.s1_0))
//        tv_guide_authentication?.setText(resources.getString(R.string.s1_5))


        tv_marquee2 = findViewById(R.id.tv_marquee2)
        tv_marquee2?.setText(resources.getString(R.string.s1_0))


        findViewById<MarqueeTextViewV3>(R.id.tv_marquee3)?.let {
            it.setText(strShort);
//           it.setText(StrLong);
            it.setMarqueeMode(MarqueeMode.WHEN_FOCUSED)
            it.setScrollSpeedFactor(0.1f)
            it.setRestartDelay(2000L)
            it.requestMarqueeFocus()
        }
        findViewById<MarqueeTextViewV4>(R.id.tv_marquee4)?.let {
//            it.setText("短文字。");
            it.setText(strLong);
        }

        findViewById<MarqueeTextViewV5>(R.id.tv_marquee5)?.let {
//            it.setText("短文字。");
            it.setText(strLong);
            it.startScrolling()
        }
        findViewById<MarqueeTextViewV6>(R.id.tv_marquee6)?.let {
            // 可以设置新的文本
            it.setText(strLong);
            // 可以设置滚动速度 (单位: 像素/秒)
            it.setSpeed(80);
            // 启动跑马灯
            it.startMarquee();
        }
    }
}