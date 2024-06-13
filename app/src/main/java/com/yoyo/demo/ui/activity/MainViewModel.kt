package com.yoyo.demo.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoyo.demo.ui.activity.clean.CleanUtils
import kotlinx.coroutines.launch

class MainViewModel:ViewModel() {

    fun startScan(){
        viewModelScope.launch {
            CleanUtils.scanFiles()
        }
    }

}