package com.yoyo.demo.ui.activity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoyo.demo.ui.activity.base.UiState
import com.yoyo.demo.ui.activity.clean.CleanUtils
import com.yoyo.demo.ui.activity.clean.entity.FileInfo
import com.yoyo.demo.ui.activity.clean.entity.FileItem
import com.yoyo.demo.ui.activity.clean.listener.ScanEventAdapter
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _uiState = MutableLiveData<UiState<List<FileItem>>>()

    val uiState: LiveData<UiState<List<FileItem>>>
        get() = _uiState

    private val _scanningFileName = MutableLiveData<String>()
    val scanningFileName: LiveData<String>
        get() = _scanningFileName

    private val _duplicateFileGroupCount = MutableLiveData<Int>()
    val duplicateFileGroup: LiveData<Int>
        get() = _duplicateFileGroupCount

    private val _progress = MutableLiveData<Float>()
    val progress: LiveData<Float>
        get() = _progress

    private val _duplicateCount = MutableLiveData<Int>()
    val duplicateCount: LiveData<Int>
        get() = _duplicateCount

    private val selectedFiles = mutableSetOf<FileInfo>()

    private val _totalSelectedSize = MutableLiveData<Long>()
    val totalSelectedSize: LiveData<Long> get() = _totalSelectedSize

    private val _duplicateFiles = MutableLiveData<List<FileItem>>()
    val duplicateFiles: LiveData<List<FileItem>> get() = _duplicateFiles

    fun startScan() {
        viewModelScope.launch {
            _uiState.postValue(UiState.Loading)
            val fileInfoMap = CleanUtils.scanFiles(object : ScanEventAdapter() {
                override fun onScanFileName(fileName: String) {
                    _scanningFileName.postValue(fileName)
                }

                override fun onProgress(progress: Float) {
                    _progress.postValue(progress)
                }

                override fun onDuplicateCount(count: Int) {
                    _duplicateFileGroupCount.postValue(count)
                }

            })
            val dataList = mutableListOf<FileItem>()
            var count = 0
            fileInfoMap.forEach { (md5, fileInfoList) ->
                dataList.add(FileItem.Header(md5 = md5, count = fileInfoList.size))
                fileInfoList.forEach {
                    dataList.add(FileItem.FileEntry(fileInfo = it))
                    count++
                }
            }
            _duplicateCount.postValue(count)
            _uiState.postValue(UiState.Success(dataList))
            if (dataList.isNotEmpty()) {
                _duplicateFiles.postValue(dataList)
            }
        }
    }

    fun isScanning() = uiState.value == UiState.Loading

    fun onFileSelectionChanged(fileItem: FileItem.FileEntry, isSelected: Boolean) {
        if (isSelected) {
            selectedFiles.add(fileItem.fileInfo)
        } else {
            selectedFiles.remove(fileItem.fileInfo)
        }
        val headerIndex = _duplicateFiles.value?.indexOfFirst {
            it is FileItem.Header &&
                    it.md5 == fileItem.fileInfo.md5
        } ?: -1
        Log.d(TAG, "onFileSelectionChanged: headerIndex=$headerIndex")
        if (headerIndex != -1) {
            val header = _duplicateFiles.value?.get(headerIndex) as FileItem.Header
            header.size = if (isSelected) {
                header.size + fileItem.fileInfo.size
            } else {
                header.size - fileItem.fileInfo.size
            }
            _duplicateFiles.postValue(_duplicateFiles.value)
        }

        _totalSelectedSize.value = selectedFiles.sumOf { it.size }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}