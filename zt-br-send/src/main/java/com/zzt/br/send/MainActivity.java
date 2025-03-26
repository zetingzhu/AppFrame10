package com.zzt.br.send;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyReceiver.Send-act";
    private Button button;

    public static final String ACTION_RECEIVE = "com.RECEIVE";
    public static final String ACTION_DATA = "com.DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(4000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Random random = new Random();
                        sendBroadcast("发送内容1:" + random.nextInt(100), true);
                        Log.w(TAG, "开始发送 >1> ");
                    }
                }).start();
                button.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Random random = new Random();
                        sendBroadcast("发送内容2:" + random.nextInt(100), true);
                        Log.w(TAG, "开始发送 >2> ");
                    }
                }, 5000L);

            }
        });
    }


    public void sendBroadcast(String newData, boolean usePermission) {
        if (usePermission) {
            Intent intent = new Intent(ACTION_RECEIVE);
            intent.putExtra(ACTION_DATA, newData);
            intent.setPackage("com.zzt.broadcastreceiver");
            sendBroadcast(intent);
        } else {
            Intent intent = new Intent(ACTION_RECEIVE);
            intent.putExtra(ACTION_DATA, newData);
            intent.setPackage("com.zzt.broadcastreceiver");
            sendBroadcast(intent, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
    }
}