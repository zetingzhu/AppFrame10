package com.zzt.zt_deviceid.act;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.zzt.zt_deviceid.R;
import com.zzt.zt_deviceid.util.SimpleWebView;

public class MWebAct extends AppCompatActivity {
    public static void start(Context context) {
        Intent starter = new Intent(context, MWebAct.class);
        context.startActivity(starter);
    }

    private ActivityResultLauncher<Intent> actResultLauncherFile;// StartActivityForResult 监听
    private ValueCallback<Uri[]> uploadMessageAboveL;// 处理File 标签
    private SimpleWebView web_view;
    int resultCode = 9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mweb);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        web_view = findViewById(R.id.web_view);

        actResultLauncherFile = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            //  选择文件
                            Intent data = result.getData();
                            if (data != null) {
                                Uri[] results = null;
                                String dataString = data.getDataString();
                                if (dataString != null) {
                                    results = new Uri[]{Uri.parse(dataString)};
                                }
                                if (uploadMessageAboveL != null) {
                                    uploadMessageAboveL.onReceiveValue(results);
                                    uploadMessageAboveL = null;
                                }
                            }
                        } else {
                            // 取消
                            // 用户取消了文件选择
                            if (uploadMessageAboveL != null) {
                                uploadMessageAboveL.onReceiveValue(null);
                                uploadMessageAboveL = null;
                            }
                        }
                    }
                });

        // 自定义 WebChromeClient 辅助WebView处理图片上传操作【<input type=file> 文件上传标签】
        web_view.setWebChromeClient(new WebChromeClient() {
            // For Android >= 5.0   才支持多选
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                openFileChooserActivity();
                return true;
            }
        });

        web_view.loadUrl("file:///android_asset/test.html");
    }

    private void openFileChooserActivity() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        actResultLauncherFile.launch(intent);
    }
}