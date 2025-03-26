package com.zzt.loadfiledirimage;

import android.app.Application;
import android.content.Context;

/**
 * @author: zeting
 * @date: 2025/3/6
 */
public class MyApplication extends Application {
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public Context getAppContext() {
        return this;
    }
}
