package com.capstone.agrovision.view.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewsViewModel : ViewModel() {
    private val newsRepository = NewsRepository()
    private val _newsList = MutableLiveData<List<NewsItem>>()
    val newsList: LiveData<List<NewsItem>> get() = _newsList

    fun fetchHealthNews() {
        newsRepository.getHealthNews(
            onSuccess = { retrievedNews ->
                _newsList.postValue(retrievedNews)
            },
            onFailure = { error ->
            }
        )
    }
}
