package com.trade.zt_smsretriever

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.phone.SmsRetriever
import java.util.regex.Pattern

class SMSUserConsentActivity : AppCompatActivity() {

    private lateinit var smsBroadcastReceiver: SmsBroadcastReceiver
    private lateinit var consentLauncher: ActivityResultLauncher<Intent>

    private val SMS_CONSENT_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smsuser_consent)

        startSmsUserConsent()
        registerSmsBroadcastReceiver()

        consentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK && result.data != null) {
                    val message = result.data!!.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                    message?.let {
                        val code = parseCode(it)
                        Log.d("SMSUserConsentActivity", "Retrieved code: $code")
                    }
                }
            }
    }

    private fun startSmsUserConsent() {
        SmsRetriever.getClient(this).startSmsUserConsent(null)
            .addOnSuccessListener { Log.d("SMSUserConsentActivity", "SMS User Consent started") }
            .addOnFailureListener { e ->
                Log.e(
                    "SMSUserConsentActivity",
                    "Error starting SMS User Consent",
                    e
                )
            }
    }

    private fun registerSmsBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver.smsBroadcastReceiverListener =
            object : SmsBroadcastReceiver.SmsBroadcastReceiverListener {
                override fun onSuccess(message: String?) {
                    val consentIntent = Intent(SmsRetriever.SMS_RETRIEVED_ACTION)
                    consentLauncher.launch(consentIntent)
                }

                override fun onFailure() {
                    Log.e("SMSUserConsentActivity", "SMS Retriever timed out.")
                }
            }

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        ContextCompat.registerReceiver(
            this@SMSUserConsentActivity,
            smsBroadcastReceiver,
            intentFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    private fun parseCode(message: String): String {
        val pattern = Pattern.compile("\\d{4,6}") // Common verification codes are 4-6 digits
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
