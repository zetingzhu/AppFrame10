package com.zzt.zt_deviceid.util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.HashMap;

/**
 * @author: zeting
 * @date: 2022/11/10
 * 其他页面一些web设置
 */
public class SimpleWebView extends MyWebViewDensity {
    private Context mContext;

    //点击返回后不再重复显示dlg
    private boolean isToShowDlg = true;
    // 是否能显示加载中
    private boolean canShowLoading = false;
    // 是否网络加载失败
    private boolean isNetError = false;
    private boolean canShowNetErrorMsg = false;
    private NetWebViewListener netWebViewListener;

    public SimpleWebView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public SimpleWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SimpleWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        setDefaultWebSettings(this);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void setDefaultWebSettings(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        //5.0以上开启混合模式加载
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // 设置可以支持缩放
        webSettings.setSupportZoom(true);
        // 设置出现缩放工具
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(true);
        //扩大比例的缩放
        webSettings.setUseWideViewPort(false);//将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(false);// 缩放至屏幕的大小
        //允许js代码
        webSettings.setJavaScriptEnabled(true);
        //  支持通过JS打开新窗口
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //禁用文字缩放
        webSettings.setTextZoom(100);
        // 设置布局
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        //设置缓存模式
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启DOM storage API 功能
        webSettings.setDomStorageEnabled(true);
        // 开启database storage API功能
        webSettings.setDatabaseEnabled(true);
//        String cacheDirPath = webView.getContext().getFilesDir().getAbsolutePath() + "/webcache";
//        Log.i("cachePath", cacheDirPath);
//        // 设置数据库缓存路径
//        webSettings.setAppCachePath(cacheDirPath);
//        webSettings.setAppCacheEnabled(true);
        //  设置编码格式
        webSettings.setDefaultTextEncodingName("UTF-8");
        //允许WebView使用File协议
        webSettings.setAllowFileAccess(true);
        //不保存密码
        webSettings.setSavePassword(false);
        //自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        // 解决跳到系统浏览器
        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                isNetError = true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                isNetError = true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                isNetError = true;
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                isNetError = true;
            }
        });
        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {

                } else if (newProgress < 100) {

                }
            }
        });
    }


    /**
     * 设置默认显示loading 弹框
     *
     * @param canShowLoading
     */
    public void setCanShowLoading(boolean canShowLoading) {
        this.canShowLoading = canShowLoading;
    }

    public void setNetWebViewListener(boolean canShowNetErrorMsg, NetWebViewListener netWebViewListener) {
        this.netWebViewListener = netWebViewListener;
        this.canShowNetErrorMsg = canShowNetErrorMsg;
    }

    public interface NetWebViewListener {
        void showError(boolean isError);
    }
}
