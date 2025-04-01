package com.zzt.zt_deviceid.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import java.util.UUID;

/**
 * @author: zeting
 * @date: 2025/3/26
 */
public class DeviceUtils {
    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private static final String PREFS_FILE = "device_id_prefs";
    private static final String PREFS_DEVICE_ID = "device_id";

    public static String getUUIDStr(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        String deviceId = prefs.getString(PREFS_DEVICE_ID, null);
        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PREFS_DEVICE_ID, deviceId);
            editor.apply();
        }
        return deviceId;
    }
}
