package com.capstone.agrovision.view.bookmark

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.agrovision.R
import com.capstone.agrovision.data.local.AppDatabase
import com.capstone.agrovision.data.local.Bookmark
import com.capstone.agrovision.view.DetailBookmarkActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookmarkActivity : AppCompatActivity() {

    private lateinit var rvBookmark: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var bookmarkAdapter: BookmarkAdapter
    private var bookmarkList: MutableList<Bookmark> = mutableListOf()

    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)

        rvBookmark = findViewById(R.id.rvBookmark)
        progressBar = findViewById(R.id.progressBar)

        bookmarkAdapter = BookmarkAdapter(bookmarkList) { bookmark ->
            navigateToDetailBookmark(bookmark)
        }

        rvBookmark.layoutManager = LinearLayoutManager(this)
        rvBookmark.adapter = bookmarkAdapter

        database = AppDatabase.getDatabase(this)

        setUpActionBar()
        loadBookmarks()
    }

    private fun setUpActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.bookmark_card)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadBookmarks() {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            val bookmarks = database.bookmarkDao().getAllBookmarks()
            bookmarkList.clear()
            bookmarkList.addAll(bookmarks)
            withContext(Dispatchers.Main) {
                bookmarkAdapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun navigateToDetailBookmark(bookmark: Bookmark) {
        val intent = Intent(this, DetailBookmarkActivity::class.java)
        intent.putExtra("bookmark_data", bookmark)
        startActivity(intent)
    }
}
