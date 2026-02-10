package com.trade.zt_livefactory

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tencent.rtmp.ui.TXCloudVideoView
import com.trade.zt_livefactory.liveutil.ILivePlayer
import com.trade.zt_livefactory.liveutil.LivePlayerFactory
import com.trade.zt_livefactory.liveutil.PlayerType


class MainActivity : AppCompatActivity() {
    var mLivePlayer: ILivePlayer? = null
    var video_view: TXCloudVideoView? = null
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
        video_view = findViewById(R.id.video_view)
        val selectedType: PlayerType = PlayerType.TENCENT
        mLivePlayer = LivePlayerFactory.createPlayer(selectedType, this)
        mLivePlayer?.setDisplayView(video_view)
        // 开始播放（业务逻辑保持不变）
        mLivePlayer?.startPlay("rtmp://example.com/live/stream")
    }

    override fun onResume() {
        super.onResume()
        mLivePlayer?.resume()
    }

    override fun onPause() {
        super.onPause()
        mLivePlayer?.pause()
    }

    override fun onStop() {
        super.onStop()
        mLivePlayer?.stopPlay()
        mLivePlayer?.destroy()
    }

}