package com.capstone.agrovision.view.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.capstone.agrovision.R

class NewsAdapter : ListAdapter<NewsItem, NewsAdapter.NewsViewHolder>(NewsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentNewsItem = getItem(position)
        holder.bind(currentNewsItem)
    }

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val newsTitleTextView: TextView = itemView.findViewById(R.id.tvNews)
        private val newsImageView: ImageView = itemView.findViewById(R.id.newsPict)
        private val newsUrlTextView: TextView = itemView.findViewById(R.id.tvUrl)

        fun bind(newsItem: NewsItem) {
            newsTitleTextView.text = newsItem.title
            newsUrlTextView.apply {
                text = "Read more"
                setTag(R.id.tvUrl, newsItem.url)
                visibility = if (newsItem.url != null) View.VISIBLE else View.GONE
            }
            if (!newsItem.imageUrl.isNullOrEmpty()) {
                Glide.with(itemView)
                    .load(newsItem.imageUrl)
                    .apply(
                        RequestOptions()
                        .placeholder(R.drawable.ic_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(newsImageView)
            } else {
                newsImageView.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_placeholder))
            }
        }
    }

    class NewsDiffCallback : DiffUtil.ItemCallback<NewsItem>() {
        override fun areItemsTheSame(oldItem: NewsItem, newItem: NewsItem): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: NewsItem, newItem: NewsItem): Boolean {
            return oldItem == newItem
        }
    }
}