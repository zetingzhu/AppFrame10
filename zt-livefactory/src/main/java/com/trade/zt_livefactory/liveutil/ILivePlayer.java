package com.trade.zt_livefactory.liveutil;

import android.content.Context;
import android.view.SurfaceView;
import android.view.View;

/**
 * @author: zeting
 * @date: 2025/11/28
 * I直播播放器接口
 */
public interface ILivePlayer {
    //   初始化和设置视图
    void initPlayer(Context context);

    // TXCloudVideoView 或 TextureView
    void setDisplayView(View mView);

    // 开始播放，传入直播流 URL
    void startPlay(String url);

    // 暂停
    void pause();

    // 恢复播放
    void resume();

    // 停止播放
    void stopPlay();

    // 销毁和资源释放
    void destroy();

    // 是否正在播放
    boolean isPlaying();

    // 事件监听 (抽象回调，需要业务层实现)
    void setPlaybackListener(ILivePlayerListener listener);

}