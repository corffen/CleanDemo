package com.yoyo.demo.ui.activity.clean

import android.util.Log
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.SDCardUtils
import com.blankj.utilcode.util.TimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date

object CleanUtils {
    private const val TAG = "CleanUtils"
    private const val MIN_FILE_SIZE = 500 * 1024L
    suspend fun scanFiles() {
        withContext(Dispatchers.Default) {
            MeasureUtils.measureTime("scanFile") {
                val directory = File(SDCardUtils.getSDCardPathByEnvironment(), "Android/data")
                val filesMap = mutableMapOf<String, MutableList<File>>()
                //遍历所有的文件，筛选出大于500KB的文件
                directory.walkTopDown().forEach { file ->
                    if (file.isFile && file.length() > MIN_FILE_SIZE) {
                        val md5 = MeasureUtils.measureTime("get md5 for ${file.name}") {
                            FileUtils.getFileMD5ToString(file)
                        }
                        if (md5 != null) {
                            if (!filesMap.containsKey(md5)) {
                                filesMap[md5] = mutableListOf()
                            }
                            filesMap[md5]!!.add(file)
                        }
                    }
                }
                val filterFiles = mutableListOf<FileInfo>()
                //筛选出values个数大于1的，说明有重复的文件
                 filesMap.filter { it.value.size > 1 }.forEach {map->
                    map.value.forEach { file ->
                        filterFiles.add(getFileInfo(file,map.key))
                    }
                }
                //按照文件大小降序排列
                val sortedDuplicateFiles = filterFiles.sortedByDescending { it.size }

                withContext(Dispatchers.Main.immediate) {
                    for (file in sortedDuplicateFiles) {
                        Log.d(TAG, "File: $file")
                    }
                }
            }
        }

    }

    private fun getFileInfo(file: File,md5:String): FileInfo {
        val name = file.name
        val path = file.absolutePath
        val size = file.length()
        val type = FileTypeUtils.getFileType(path)
        val typeName = FileTypeUtils.getFileTypeName(path)
        val timeStamp = file.lastModified()
        val modifyTime = TimeUtils.date2String(Date(timeStamp))
        val fileInfo = FileInfo(
                name = name,
                path = path,
                size = size,
                type = type,
                typeName = typeName,
                icon = 0,
                modifyTime = modifyTime,
                md5 = md5
        )
        return fileInfo
    }
}