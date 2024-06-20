package com.capstone.agrovision.view.news

sealed class NewsResult<out R> private constructor() {
    data class Success<out T>(val data: T) : NewsResult<T>()
    data class Error(val error: String) : NewsResult<Nothing>()
    object Loading : NewsResult<Nothing>()
}