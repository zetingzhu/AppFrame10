package com.trade.zt_smsretriever

import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.phone.SmsRetriever
import java.util.regex.Pattern

class SmsRetrieverActivity : AppCompatActivity() {

    private lateinit var smsBroadcastReceiver: SmsBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sms)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        startSmsRetriever()
        registerSmsBroadcastReceiver()
    }

    private fun startSmsRetriever() {
        val client = SmsRetriever.getClient(this)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener {
            Log.d("SmsActivity", "SMS Retriever started")
        }
        task.addOnFailureListener {
            Log.e("SmsActivity", "Failed to start SMS Retriever", it)
        }
    }

    private fun registerSmsBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver.smsBroadcastReceiverListener = object : SmsBroadcastReceiver.SmsBroadcastReceiverListener {
            override fun onSuccess(message: String?) {
                message?.let {
                    val code = parseCode(it)
                    findViewById<TextView>(R.id.tv_code).text = code
                }
            }

            override fun onFailure() {}
        }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        ContextCompat.registerReceiver(this, smsBroadcastReceiver, intentFilter, ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    private fun parseCode(message: String): String {
        val pattern = Pattern.compile("\\d{6}")
        val matcher = pattern.matcher(message)
        return if (matcher.find()) {
            matcher.group(0)
        } else {
            ""
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(smsBroadcastReceiver)
    }
}