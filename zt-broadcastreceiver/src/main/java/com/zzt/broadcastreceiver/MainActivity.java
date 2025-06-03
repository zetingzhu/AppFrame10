package com.zzt.broadcastreceiver;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyReceiver-act";
    private Button button;
    private MyBroadcastReceiver myBroadcastReceiver;
    private TextView tv_msg;

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
                button.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Random random = new Random();
                        sendBroadcast("发送内容:" + random.nextInt(100), true);
                        Log.w(TAG, "开始发送 >> ");
                    }
                }, 500L);

            }
        });

//        registerBroadcastReceiver();

        tv_msg = findViewById(R.id.tv_msg);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

//        unregisterBroadcastReceiver();
    }


    public void registerBroadcastReceiver() {
        myBroadcastReceiver = new MyBroadcastReceiver();

        IntentFilter filter = new IntentFilter(MyBroadcastReceiver.ACTION_RECEIVE);
        boolean listenToBroadcastsFromOtherApps = false;
        int receiverFlags = listenToBroadcastsFromOtherApps
                ? ContextCompat.RECEIVER_EXPORTED
                : ContextCompat.RECEIVER_NOT_EXPORTED;

        ContextCompat.registerReceiver(this, myBroadcastReceiver, filter, receiverFlags);

        ContextCompat.registerReceiver(
                this, myBroadcastReceiver, filter,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                null, // scheduler that defines thread, null means run on main thread
                receiverFlags
        );
    }

    public void unregisterBroadcastReceiver() {
        if (myBroadcastReceiver != null) {
            unregisterReceiver(myBroadcastReceiver);
        }
    }

    public void sendBroadcast(String newData, boolean usePermission) {
        if (usePermission) {
            Intent intent = new Intent(MyBroadcastReceiver.ACTION_RECEIVE);
            intent.putExtra(MyBroadcastReceiver.ACTION_DATA, newData);
            intent.setPackage(getPackageName());
            sendBroadcast(intent);
        } else {
            Intent intent = new Intent(MyBroadcastReceiver.ACTION_RECEIVE);
            intent.putExtra(MyBroadcastReceiver.ACTION_DATA, newData);
            intent.setPackage(getPackageName());
            sendBroadcast(intent, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        // Do something
        if (tv_msg != null && event != null) {
            tv_msg.append("\n> " + event.getSendMsg());
        }
    }
}