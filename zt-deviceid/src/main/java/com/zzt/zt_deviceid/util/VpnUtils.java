package com.zzt.zt_deviceid.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

/**
 * @author: zeting
 * @date: 2025/3/27
 * 是否开启 vpn 监听工具
 */
public class VpnUtils {

    public static boolean isVpnConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            if (activeNetwork != null) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
                return capabilities != null &&
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
            }
        }
        return false;
    }
}
