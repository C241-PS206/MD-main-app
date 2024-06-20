package com.capstone.agrovision.view.news

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.agrovision.R
import com.capstone.agrovision.databinding.ActivityNewsBinding

class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupViewModel()
    }

    private fun setupRecyclerView() {
        newsRecyclerView = findViewById(R.id.rvNews)
        newsAdapter = NewsAdapter()

        newsRecyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(this@NewsActivity)

            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = getString(R.string.news_appbar)
        }
    }

    private fun setupViewModel() {
        newsViewModel = ViewModelProvider(this).get(NewsViewModel::class.java)
        newsViewModel.fetchHealthNews()
        newsViewModel.newsList.observe(this, Observer { newsList ->
            newsAdapter.submitList(newsList)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun openNewsUrl(view: View) {
        val url = view.getTag(R.id.tvUrl) as? String

        url?.let {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            startActivity(intent)
        }
    }
}