package com.yoyo.demo.ui.activity.clean

import android.text.TextUtils
import com.yoyo.demo.R
import java.io.File

/**
 * @author : zxh
 * @description :
 * @date : 2024/5/28-15:16
 */
object FileTypeUtils {
    const val TYPE_OTHER = 0 // 其他
    const val TYPE_IMAGE = 1 // 图片
    const val TYPE_VIDEO = 2 // 视频
    const val TYPE_MUSIC = 3 // 音频
    const val TYPE_DOC = 4 // 文档
    const val TYPE_ZIP = 5 // 压缩包
    const val TYPE_APK = 6 // 安装包
    const val TYPE_DIR = 7 // 文件夹

    val docs: ArrayList<String> = ArrayList<String>()
    val images: ArrayList<String> = ArrayList<String>()
    val musics: ArrayList<String> = ArrayList<String>()
    val videos: ArrayList<String> = ArrayList<String>()
    val zips: ArrayList<String> = ArrayList<String>()
    val apks: ArrayList<String> = ArrayList<String>()

    init {
        images.add("jpg")
        images.add("png")
        images.add("gif")
        images.add("tif")
        images.add("bmp")
        docs.add("dwg")
        docs.add("psd")
        docs.add("rtf")
        docs.add("doc")
        docs.add("docx")
        docs.add("ps")
        docs.add("mdb")
        docs.add("pdf")
        docs.add("txt")
        docs.add("xls")
        docs.add("xlsx")
        docs.add("ppt")
        docs.add("pptx")
        zips.add("rar")
        zips.add("zip")
        zips.add("gz")
        videos.add("avi")
        videos.add("mpg")
        videos.add("rm")
        videos.add("mov")
        videos.add("mid")
        videos.add("mp4")
        musics.add("mp3")
        musics.add("wav")
        musics.add("wma")
        musics.add("acc")
        musics.add("ogg")
        musics.add("mpa")
        musics.add("flac")
        musics.add("ape")
        musics.add("wv")
        apks.add("apk")
    }
    fun getFileTypeImage(fileType: Int): Int {
        return when(fileType) {
            TYPE_IMAGE ->
                R.drawable.ic_duplicate_files_image
            TYPE_VIDEO ->
                R.drawable.ic_duplicate_files_video
            TYPE_MUSIC ->
                R.drawable.ic_duplicate_files_music
            TYPE_DOC ->
                R.drawable.ic_duplicate_files_doc
            TYPE_ZIP ->
                R.drawable.ic_duplicate_files_zip
            TYPE_APK ->
                R.drawable.ic_duplicate_files_apk
            else ->
                R.drawable.ic_duplicate_files_other
        }
    }

    /**
     * 获取文件类型
     */
    fun getFileType(filePath: String?): Int {
        val file = File(filePath)
        if (!file.exists()) {
            return TYPE_OTHER
        }
        if (file.isDirectory) {
            return TYPE_DIR
        }
        var type = filePath?.split(".")?.last()?.lowercase()
        if (TextUtils.isEmpty(type)) {
            return TYPE_OTHER
        }
        if (images.contains(type)) {
            return TYPE_IMAGE
        } else if (docs.contains(type)) {
            return TYPE_DOC
        } else if (zips.contains(type)) {
            return TYPE_ZIP
        } else if (musics.contains(type)) {
            return TYPE_MUSIC
        } else if (videos.contains(type)) {
            return TYPE_VIDEO
        } else if (apks.contains(type)) {
            return TYPE_APK
        }
        return TYPE_OTHER
    }

    fun getFileTypeName(filePath: String?): String {
        val fileType = getFileType(filePath)
        return when(fileType) {
            TYPE_IMAGE ->
                "图片"
            TYPE_VIDEO ->
                "视频"
            TYPE_MUSIC ->
                "音频"
            TYPE_DOC ->
                "文档"
            TYPE_ZIP ->
                "压缩包"
            TYPE_APK ->
                "安装包"
            else ->
                "未知"
        }
    }

    /**
     * 是否是图片
     */
    fun isImage(filePath: String?): Boolean {
        return TYPE_IMAGE == getFileType(filePath)
    }

    /**
     * 是否是视频
     */
    fun isVideo(filePath: String?): Boolean {
        return TYPE_VIDEO == getFileType(filePath)
    }
}