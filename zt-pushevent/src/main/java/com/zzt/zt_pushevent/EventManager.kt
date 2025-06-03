package com.zzt.zt_pushevent

import android.content.Context
import android.util.TimeUtils
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * @author: zeting
 * @date: 2025/4/18
 *
 */
class EventManager {
    val context: Context? = null

    constructor()


    private fun scheduleEventResend() {
        if (context != null) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val resendWorkRequest = PeriodicWorkRequestBuilder<EventsWorker>(
                15, TimeUnit.MINUTES // 设置重发间隔
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "resendEvents",
                ExistingPeriodicWorkPolicy.KEEP,
                resendWorkRequest
            )
        }
    }
}