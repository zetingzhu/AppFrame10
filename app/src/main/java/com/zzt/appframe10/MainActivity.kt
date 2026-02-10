package com.zzt.appframe10

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.zzt.appframe10.MarqueeTextViewV3.MarqueeMode
import org.xml.sax.XMLReader

class MainActivity : AppCompatActivity() {

    var tv_guide_authentication: MarqueeForeverTextView? = null;
    var tv_marquee2: MarqueeForeverTextView? = null;
    var strShort = "短文字。"
    var strLong =
        "长文本。 1 2 3 4 5 6 7 8 9 0 11 12 13 14 15 16 17 18 19 20 21 222 23 24 25 26 27 28 29 3031 32 33 34 35 36 37 38 39 40 A S D f g h j k l  "
    var btn_speed: Button? = null
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

//        val str = "<b>这是一段粗体文本</b>，<i>这是一段斜体文本</i>，<br>这是一个<font color=\"#FF0000\">红色的字</font>，<a href=\"https://www.google.com\">这是一个链接</a>。"
        var str = "<style=\"margin-bottom: 16px;\">此操作不可撤销</style>";
        var tv_html: TextView = findViewById(R.id.tv_html)
        val fromHtml = Html.fromHtml(
            str, Html.FROM_HTML_MODE_COMPACT, null, object : Html.TagHandler {
                override fun handleTag(
                    opening: Boolean, tag: String?, output: Editable?, xmlReader: XMLReader?
                ) {
                    Log.d(
                        "zzz",
                        "open:" + opening.toString() + " tag:" + tag + " out:" + output.toString() + " xml:" + xmlReader.toString()
                    )

                }
            })

        tv_html.setText(fromHtml.toString())

    }

    private fun initView() {
        btn_speed = findViewById(R.id.btn_speed)
        btn_speed?.setOnClickListener {
            val packageName = "com.rynatsa.xtrendspeed_debug"
            val activityClassName = "com.trade.eight.moudle.home.activity.LoadingActivity"
            val intent = Intent().apply {
                component = ComponentName(packageName, activityClassName)
//                putExtra(
//                    "data",
//                    "{\"accessUrl\":\"bkfxgo://myHome?channelCode=push&sourceType=4\",\"analyticsLabel\":\"VOUCHER\",\"body\":\"送你\$10代金券，零成本下单\"," +
//                            "\"data\":{\"amount\":\"10\",\"voucherType\":99,\"message\":\"送你\$10代金券，零成本下单\"},\"forcePopup\":false," +
//                            "\"groupPush\":1,\"ifCheckSwitch\":false,\"pushRecordId\":10791994,\"sendType\":1," +
//                            "\"title\":\"XTrend Speed\",\"userId\":88903337,\"uuid\":\"E7AD280774480D460CF41C20B77D31CD\"}"
//                )
                putExtra(
                    "data",
                    "{\"accessUrl\":\"bkfxgo://cowUserInfo?cowUserId=55C868D70B39E9F3\",\"analyticsLabel\":\"VOUCHER\",\"body\":\"送你\$10代金券，零成本下单\"," +
                            "\"data\":{\"amount\":\"10\",\"voucherType\":99,\"message\":\"送你\$10代金券，零成本下单\"},\"forcePopup\":false," +
                            "\"groupPush\":1,\"ifCheckSwitch\":false,\"pushRecordId\":10791994,\"sendType\":1," +
                            "\"action\":\"protocol\"," +
                            "\"backMsg\":1," +
                            "\"url\":\"bkfxgo://cowUserInfo?cowUserId=55C868D70B39E9F3\"," +
                            "\"title\":\"XTrend Speed\",\"userId\":88903337}"
                )
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "无法打开指定的 Activity", Toast.LENGTH_SHORT).show()
            }
        }

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