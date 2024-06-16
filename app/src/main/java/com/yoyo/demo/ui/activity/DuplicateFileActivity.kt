package com.yoyo.demo.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.Gravity
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.ToastUtils
import com.yoyo.demo.R
import com.yoyo.demo.databinding.ActivityDuplicateFileBinding
import com.yoyo.demo.ui.activity.base.UiState
import com.yoyo.demo.ui.activity.clean.adapter.DuplicateFileAdapter
import com.yoyo.demo.ui.activity.clean.entity.FileItem
import org.libpag.PAGFile
import org.libpag.PAGScaleMode

/**
 *    @author      : zxh
 *    @description :
 *    @date        : 2023/11/14-17:28
 */
class DuplicateFileActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        const val REQUEST_CODE_READ_EXTERNAL_STORAGE = 1

    }

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var viewBinding: ActivityDuplicateFileBinding

    private lateinit var adapter: DuplicateFileAdapter

    private val requestDirectoryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.also { uri ->
                viewModel.startScan(uri)
            }
        }
    }

    private fun requestDirectoryAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                putExtra(
                    DocumentsContract.EXTRA_INITIAL_URI,
                    Uri.parse("content://com.android.externalstorage.documents/document/primary:Android/data")
                )
            }
            requestDirectoryLauncher.launch(intent)
        } else {
            viewModel.startScan(null)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.transparentStatusBar(this)
        BarUtils.setStatusBarLightMode(this, true)
        viewBinding = ActivityDuplicateFileBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        adapter = DuplicateFileAdapter(viewModel)
        viewBinding.rvList.layoutManager = LinearLayoutManager(this)
        viewBinding.rvList.adapter = adapter
        viewBinding.btnScan.setOnClickListener {
            if (viewModel.isScanning()) {
                Log.d(TAG, "onCreate: is scanning")
            } else {
                requestPermissionThenScan()
            }
        }
        viewBinding.ivBack.setOnClickListener {
            onBackPressed()
        }
        viewModel.uiState.observe(this) {
            when (it) {
                is UiState.Loading -> {
                    showScanningUI()
                }

                is UiState.Success -> {
                    showScanResult(it)
                }

                is UiState.Error -> {
                    Log.d(TAG, "出错")
                }
            }
        }

        viewModel.scanningFileName.observe(this) {
            viewBinding.tvFilePath.text = "路径地址:$it"
        }
        viewModel.duplicateFileGroup.observe(this) {
            if (it > 0) {
                viewBinding.tvDuplicateGroup.text = "$it"
            }
        }
        viewModel.progress.observe(this) {
            viewBinding.progressbar.progress = (it * 100).toInt().coerceAtMost(100)
        }
        viewModel.duplicateCount.observe(this) {
            if (it > 0) {
                viewBinding.tvResultCount.text = "${it}个"
            }
        }
        viewModel.duplicateFiles.observe(this) {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }
        viewModel.totalSelectedSize.observe(this) {
            Log.d(TAG, "onCreate: selected size =$it")
        }
    }

    private fun showScanResult(it: UiState.Success<List<FileItem>>) {
        viewBinding.llScanning.isVisible = false
        viewBinding.llScanResult.isVisible = true
        val fileInfoList = it.data
        if (fileInfoList.isEmpty()) {
            viewBinding.llEmpty.isVisible = true
        } else {
            viewBinding.llList.isVisible = true
        }
    }

    private fun showScanningUI() {
        viewBinding.btnScan.isVisible = false
        viewBinding.llScanning.isVisible = true
        viewBinding.pagLoading.apply {
            composition = PAGFile.Load(assets, "pag/anim_duplicate_files_scanning.pag")
            setScaleMode(PAGScaleMode.Stretch)
            setRepeatCount(-1)
            play()
        }
    }

    private fun requestPermissionThenScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_READ_EXTERNAL_STORAGE
            )
        } else {
            scan()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            scan()
        }
    }

    private fun scan() {
        requestDirectoryAccess()
    }

    override fun onBackPressed() {
        if (viewModel.isScanning()) {
            showStopScanDialog(this)
        } else {
            super.onBackPressed()
        }
    }


    private fun showStopScanDialog(context: Context) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_stop_scan, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val btnCancel: Button = dialogView.findViewById(R.id.btn_cancel)
        val btnContinue: Button = dialogView.findViewById(R.id.btn_continue)


        btnCancel.setOnClickListener {
            // 取消扫描按钮逻辑
            dialog.dismiss()
            super.onBackPressed()
        }

        btnContinue.setOnClickListener {
            // 继续扫描按钮逻辑
            dialog.dismiss()
            showDeleteToast()
        }

        dialog.show()
    }

    private fun showDeleteToast() {
        ToastUtils.make()
            .setGravity(Gravity.CENTER, 0, 0)
            .setBgColor(ColorUtils.getColor(R.color.toast_bg))
            .setTopIcon(R.drawable.ic_success)
            .show("删除成功")
    }


}
