package com.zzt.zt_deviceid.util;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * @author: zeting
 * @date: 2022/8/23
 * 此webView为了解决适配问题、在使用修改密度适配方案中使用 webView 会使 density 复原，
 * 原因是由于 WebView 初始化的时候会还原 density 的值导致适配失效
 */
public class MyWebViewDensity extends WebView {
    private static final String TAG = MyWebViewDensity.class.getSimpleName();

    public MyWebViewDensity(Context context) {
        super(context);
        initView(context);
    }

    public MyWebViewDensity(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MyWebViewDensity(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {

    }

    @Override
    public void setOverScrollMode(int mode) {
        super.setOverScrollMode(mode);
    }
}