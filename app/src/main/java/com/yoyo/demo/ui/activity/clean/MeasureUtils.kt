package com.yoyo.demo.ui.activity.clean

import android.util.Log

object MeasureUtils {
    const val TAG = "MeasureUtils"

    /**
     * 测量方法的执行时间
     * 默认如果超过50毫秒,就打印执行此任务的日志
     */
    inline fun <T> measureTime(taskName: String, threshold: Long = 50, block: () -> T): T {
        val startTime = System.currentTimeMillis()
        val result = block()
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        if (duration > threshold) {
            Log.d(TAG, "$taskName took $duration ms")
        }
        return result
    }
}