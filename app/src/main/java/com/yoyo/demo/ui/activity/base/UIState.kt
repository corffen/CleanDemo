package com.yoyo.demo.ui.activity.base

/**
 * UI状态类
 */
sealed interface UiState<out T> {

    data class Success<T>(val data: T) : UiState<T>

    data class Error(val message: String) : UiState<Nothing>

    object Loading : UiState<Nothing>

}