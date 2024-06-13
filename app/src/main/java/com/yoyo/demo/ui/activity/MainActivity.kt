package com.yoyo.demo.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.BarUtils
import com.yoyo.demo.R

/**
 *    @author      : zxh
 *    @description :
 *    @date        : 2023/11/14-17:28
 */
class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_READ_EXTERNAL_STORAGE = 1

    }
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BarUtils.transparentStatusBar(this)
        BarUtils.setStatusBarLightMode(this, true)
        findViewById<TextView>(R.id.btn_scan).setOnClickListener {
            performScan()
        }
    }

    private fun performScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE_READ_EXTERNAL_STORAGE)
        } else {
           viewModel.startScan()
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            viewModel.startScan()
        }
    }

}
