package com.zzt.zt_pushevent

/**
 * @author: zeting
 * @date: 2025/4/18
 *
 */
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventsWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val eventLists = getAllEventList()



        Result.success()
    }

    fun getAllEventList(): MutableList<EventData> {

        return mutableListOf()
    }

}