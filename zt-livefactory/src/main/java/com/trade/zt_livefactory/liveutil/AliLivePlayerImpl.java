package com.trade.zt_livefactory.liveutil;

import android.content.Context;
import android.view.SurfaceView;

/**
 * @author: zeting
 * @date: 2025/11/28
 */
//public class AliLivePlayerImpl implements ILivePlayer {
//    private AliPlayer mAliPlayer;
//    private ILivePlayerListener mListener;
//
//    @Override
//    public void initPlayer(Context context) {
//        // 实例化阿里云播放器
//        mAliPlayer = new AliPlayer(context);
//        // 设置阿里云的事件回调（内部需要将 IPlayer.On*Listener 转换为 ILivePlayerListener）
//        // mAliPlayer.setOnPreparedListener(new AliPreparedListener(mListener));
//    }
//
//    @Override
//    public void setDisplayView(SurfaceView view) {
//        // 阿里云设置渲染视图
//        // mAliPlayer.setSurface(view.getHolder().getSurface());
//    }
//
//    @Override
//    public void startPlay(String url) {
//        // 阿里云开始播放逻辑
//        // UrlSource source = new UrlSource();
//        // source.setUri(url);
//        // mAliPlayer.setDataSource(source);
//        // mAliPlayer.prepare();
//        // mAliPlayer.start();
//    }
//
//    // 实现 stopPlay, pause, resume, destroy, setPlaybackListener 等接口方法
//    // ...
//}