package com.zzt.appframe10

import android.os.Bundle
import android.widget.MultiAutoCompleteTextView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    var tv_guide_authentication: MarqueeForeverTextView? = null;

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
        tv_guide_authentication?.setText("滚")
//        tv_guide_authentication?.setText("滚   11111111111111, 222222222222222222, 333333333333333333, 4444444444444444444, 555555555555555")

    }
}