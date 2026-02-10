package com.trade.zt_livefactory.liveutil;

import android.content.Context;

/**
 * @author: zeting
 * @date: 2025/11/28
 */
public class LivePlayerFactory {

    public static ILivePlayer createPlayer(PlayerType type, Context context) {
        ILivePlayer player = null;
        switch (type) {
            case TENCENT:
                player = new TencentLivePlayerImpl();
                break;
        }

        if (player != null) {
            player.initPlayer(context);
        }
        return player;
    }
}