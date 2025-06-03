package com.zzt.zt_pushevent

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.Continuation

/**
 * @author: zeting
 * @date: 2025/4/18
 */
class C :  CoroutineWorker  {
    constructor(appContext: Context, params: WorkerParameters) : super(appContext, params)

    override suspend fun doWork(): Result {
        return try {
            runBlocking {
                val repository = AnalyticsRepository(getApplicationContext())
                val events = repository.getOldestEvents(10) // 获取最旧的10个事件
                for (event in events) {
                    val success = sendEvent(event) // 发送事件的逻辑
                    if (success) {
                        repository.deleteEventById(event.id) // 删除成功发送的事件
                    } else {
                        repository.incrementAttemptCount(event.id) // 增加尝试次数
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry() // 如果发生异常，重试
        }
    }
}
