package com.yoyo.demo.ui.activity.clean.entity

data class FileInfo(val name: String, val path: String,
                    val size: Long, val type: Int,
                    val typeName: String,
                    val icon: Int,
                    val modifyTime: String,
                    val md5: String)
