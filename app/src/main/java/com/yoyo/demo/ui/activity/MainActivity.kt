package com.yoyo.demo.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.BarUtils
import com.yoyo.demo.R

/**
 *    @author      : zxh
 *    @description :
 *    @date        : 2023/11/14-17:28
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BarUtils.transparentStatusBar(this)
        BarUtils.setStatusBarLightMode(this, true)
    }

    override fun onResume() {
        super.onResume()
    }
}
