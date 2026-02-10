package com.trade.zt_livefactory.liveutil;

import android.content.Context;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import com.tencent.live2.V2TXLiveDef;
import com.tencent.live2.V2TXLivePlayer;
import com.tencent.live2.V2TXLivePlayerObserver;
import com.tencent.live2.impl.V2TXLivePlayerImpl;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * @author: zeting
 * @date: 2025/11/28
 */
public class TencentLivePlayerImpl implements ILivePlayer {
    private static final String TAG = TencentLivePlayerImpl.class.getSimpleName();
    private V2TXLivePlayer mLivePlayer;
    private ILivePlayerListener mListener;

    @Override
    public void initPlayer(Context context) {
        // 实例化腾讯云播放器
        mLivePlayer = new V2TXLivePlayerImpl(context);
        // 铺满 or 适应
        mLivePlayer.setRenderFillMode(V2TXLiveDef.V2TXLiveFillMode.V2TXLiveFillModeFill);
        // 视频画面顺时针旋转角度
        mLivePlayer.setRenderRotation(V2TXLiveDef.V2TXLiveRotation.V2TXLiveRotation0);

        //极速模式
        mLivePlayer.setCacheParams(1.0f, 1.0f);
    }

    @Override
    public void setDisplayView(View mView) {
        if (mView instanceof TXCloudVideoView && mLivePlayer != null) {
            //关键 player 对象与界面 view
            mLivePlayer.setRenderView((TXCloudVideoView) mView);
            mLivePlayer.setObserver(new V2TXLivePlayerObserver() {

                @Override
                public void onError(V2TXLivePlayer player, int code, String msg, Bundle extraInfo) {
                    Log.e(TAG, "[Player] onError: player-" + player + " code-" + code + " msg-" + msg + " info-" + extraInfo);
                }

                @Override
                public void onVideoLoading(V2TXLivePlayer player, Bundle extraInfo) {
                    Log.i(TAG, "[Player] onVideoLoading: player-" + player + ", extraInfo-" + extraInfo);
                }

                @Override
                public void onVideoPlaying(V2TXLivePlayer player, boolean firstPlay, Bundle extraInfo) {
                    Log.i(TAG, "[Player] onVideoPlaying: player-" + player + " firstPlay-" + firstPlay + " info-" + extraInfo);
                }

                @Override
                public void onVideoResolutionChanged(V2TXLivePlayer player, int width, int height) {
                    Log.i(TAG, "[Player] onVideoResolutionChanged: player-" + player + " width-" + width + " height-" + height);
                }
            });
        }
    }

    @Override
    public void startPlay(String url) {
        if (mLivePlayer != null) {
            mLivePlayer.startLivePlay(url);
        }
    }

    @Override
    public void pause() {
        if (mLivePlayer != null) {
            // 暂停
            mLivePlayer.pauseAudio();
            mLivePlayer.pauseVideo();
        }
    }

    @Override
    public void resume() {
        if (mLivePlayer != null) {
            // 继续
            mLivePlayer.resumeAudio();
            mLivePlayer.resumeVideo();
        }
    }

    @Override
    public void stopPlay() {
        if (mLivePlayer != null) {
            mLivePlayer.stopPlay();
        }
    }

    @Override
    public void destroy() {

    }

    @Override
    public boolean isPlaying() {
        if (mLivePlayer != null) {
            int playing = mLivePlayer.isPlaying();
            return playing == 1;
        }
        return false;
    }

    @Override
    public void setPlaybackListener(ILivePlayerListener listener) {

    }


}