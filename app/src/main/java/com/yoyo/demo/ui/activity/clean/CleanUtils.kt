package com.yoyo.demo.ui.activity.clean

import android.util.Log
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.SDCardUtils
import com.blankj.utilcode.util.TimeUtils
import com.yoyo.demo.ui.activity.clean.entity.FileInfo
import com.yoyo.demo.ui.activity.clean.listener.ScanEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date

object CleanUtils {
    private const val TAG = "CleanUtils"
    private const val MIN_FILE_SIZE = 500 * 1024L
    suspend fun scanFiles(listener: ScanEventListener? = null): Map<String, List<FileInfo>> {
        return withContext(Dispatchers.Default) {
            MeasureUtils.measureTime("scan files") {
                listener?.onStart()
                val directory = File(SDCardUtils.getSDCardPathByEnvironment(), "Android/data")
                val filesMap = mutableMapOf<String, MutableList<File>>()
                val sizeMap = mutableMapOf<Long, MutableList<File>>()
                val duplicateSet = HashSet<String>() //统计重复的组数
                var progress = 0
                //遍历所有的文件，筛选出大于500KB的文件,并按照文件大小进行分组
                directory.walkTopDown().forEach { file ->
                    if (file.isFile && file.length() > MIN_FILE_SIZE) {
                        val fileLen = file.length()
                        if (!sizeMap.containsKey(fileLen)) {
                            sizeMap[fileLen] = mutableListOf()
                        }
                        sizeMap[fileLen]!!.add(file)
                    }
                }
                val total = sizeMap.size
                //当文件大小相同时,再去获取md5值,并按照md5进行分组
                sizeMap.forEach { (_, files) ->
                    val scanningProgress = (++progress) * 1.0f / total
                    if (files.size > 1) {
                        files.forEach { file ->
                            listener?.onScanFileName(file.name) //遍历时,显示当前文件路径
                            val md5 = MeasureUtils.measureTime("calculate md5 for ${file.name}") {
                                FileUtils.getFileMD5ToString(file)
                            }
                            if (md5 != null) {
                                if (!filesMap.containsKey(md5)) {
                                    filesMap[md5] = mutableListOf()
                                }
                                filesMap[md5]!!.add(file)
                                //遍历过程中更新重复的文件的组数
                                if (filesMap[md5]!!.size > 1) {
                                    duplicateSet.add(md5)
                                    listener?.onDuplicateCount(duplicateSet.size)
                                }
                            }
                        }
                    }
                    listener?.onProgress(scanningProgress)
                }
                val filterFiles = mutableListOf<FileInfo>()
                val resultMap = mutableMapOf<String, List<FileInfo>>()
                //筛选出values个数大于1的，也即有重复的文件
                filesMap.filter { it.value.size > 1 }.forEach { map ->
                    val fileInfoList = ArrayList<FileInfo>(map.value.size)
                    map.value.forEach { file ->
                        filterFiles.add(getFileInfo(file, map.key))
                        fileInfoList.add(getFileInfo(file, map.key))
                    }
                    resultMap[map.key] = fileInfoList
                }

                withContext(Dispatchers.Main.immediate) {
                    for (file in filterFiles) {
                        Log.d(TAG, "File: $file")
                    }
                    listener?.onEnd()
                    resultMap
                }
            }
        }
    }

    private fun getFileInfo(file: File, md5: String): FileInfo {
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
                icon = FileTypeUtils.getFileTypeImage(type),
                modifyTime = modifyTime,
                md5 = md5
        )
        return fileInfo
    }

}