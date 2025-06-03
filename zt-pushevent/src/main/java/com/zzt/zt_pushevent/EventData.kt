package com.zzt.zt_pushevent

/**
 * @author: zeting
 * @date: 2025/4/18
 *
 */

data class EventData(
    val eventType: String?, // 类型
    val eventTime: Long?,   // 时间
    val eventValue: String?,// 值
)