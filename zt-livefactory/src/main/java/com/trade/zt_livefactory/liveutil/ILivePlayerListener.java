package com.trade.zt_livefactory.liveutil;

/**
 * @author: zeting
 * @date: 2025/11/28
 */
public interface ILivePlayerListener {

    // 直播连接成功
    void onPlayStart();

    // 直播播放停止/结束
    void onPlayStop();

    // 发生错误 (code: 错误码，msg: 错误信息)
    void onPlayError(int code, String msg);

    // 播放状态变化 (例如：LOADING, PLAYING)
    void onStateChanged(int state);

}