package com.yoyo.demo.ui.activity.clean.entity

sealed class FileItem {
    data class Header(val md5: String, val count: Int, var size: Long = 0L) : FileItem()
    data class FileEntry(val fileInfo: FileInfo, var selected: Boolean = false) : FileItem()
}