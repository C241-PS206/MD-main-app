package com.capstone.agrovision.view.news

import com.capstone.agrovision.data.remote.retrofit.ApiConfig
import com.capstone.agrovision.data.remote.response.NewsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsRepository {

    fun getHealthNews(
        onSuccess: (List<NewsItem>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val call = ApiConfig.getApiNews().getHealthNews("cancer", "health", "en", ApiConfig.API_KEY_NEWS)

        call.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    val articles = response.body()?.articles.orEmpty()
                    val newsItems = articles.mapNotNull { article ->
                        if (article.title.isNullOrEmpty() || article.urlToImage.isNullOrEmpty() || article.url.isNullOrEmpty()) {
                            null
                        } else {
                            NewsItem(article.title, article.urlToImage, article.url)
                        }
                    }
                    onSuccess(newsItems)
                } else {
                    onFailure("Failed to fetch news")
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                onFailure(t.message ?: "Unknown error")
            }
        })
    }
}
