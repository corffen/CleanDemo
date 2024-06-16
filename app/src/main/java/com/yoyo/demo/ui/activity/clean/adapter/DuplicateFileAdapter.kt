package com.yoyo.demo.ui.activity.clean.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yoyo.demo.R
import com.yoyo.demo.databinding.ItemDuplicateFileEntryBinding
import com.yoyo.demo.databinding.ItemDuplicateFileHeaderBinding
import com.yoyo.demo.ui.activity.MainViewModel
import com.yoyo.demo.ui.activity.clean.entity.FileItem

class DuplicateFileAdapter(private val viewModel: MainViewModel) :
    ListAdapter<FileItem, RecyclerView.ViewHolder>(FileDiffCallback()) {

    class FileDiffCallback : DiffUtil.ItemCallback<FileItem>() {
        override fun areItemsTheSame(oldItem: FileItem, newItem: FileItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: FileItem, newItem: FileItem): Boolean {
            return oldItem == newItem
        }

    }

    companion object {
        private const val ITEM_TYPE_HEADER = 0
        private const val ITEM_TYPE_FILE = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is FileItem.Header -> ITEM_TYPE_HEADER
            is FileItem.FileEntry -> ITEM_TYPE_FILE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_HEADER -> {
                val binding = ItemDuplicateFileHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                HeaderViewHolder(binding)
            }

            ITEM_TYPE_FILE -> {
                val binding = ItemDuplicateFileEntryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                FileViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is FileItem.Header -> (holder as HeaderViewHolder).bind(item)
            is FileItem.FileEntry -> (holder as FileViewHolder).bind(item, viewModel)
        }
    }

    class HeaderViewHolder(private val binding: ItemDuplicateFileHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(header: FileItem.Header) {
            binding.titleCount.text = "共${header.count}个重复文件"
            binding.titleSize.text = "已选择${header.size}"
            binding.selectedCount.text = "选中${header.selectedCount}"
        }
    }

    class FileViewHolder(private val binding: ItemDuplicateFileEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: FileItem.FileEntry, viewModel: MainViewModel) {
            val fileInfo = item.fileInfo
            binding.fileName.text = fileInfo.name
            binding.fileType.text = "类型:${fileInfo.typeName}"
            binding.fileSize.text = "${fileInfo.size}"
            binding.ivType.setImageResource(fileInfo.icon)
//            binding.checkBox.isChecked = item.selected
//            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
//                viewModel.onFileSelectionChanged(adapterPosition, item, isChecked)
//            }
            binding.ivSelect.setImageResource(if (item.selected) R.drawable.cb_file_selected else R.drawable.cb_file_unselected)
            binding.ivSelect.setOnClickListener {
                viewModel.onFileSelectionChanged(adapterPosition,item,!item.selected)
            }
        }
    }
}