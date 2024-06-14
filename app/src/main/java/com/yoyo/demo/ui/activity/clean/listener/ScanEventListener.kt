package com.yoyo.demo.ui.activity.clean.listener

interface ScanEventListener {
    fun onStart()
    fun onScanFileName(fileName: String)
    fun onDuplicateCount(count: Int)
    fun onProgress(progress: Float)
    fun onEnd()
}