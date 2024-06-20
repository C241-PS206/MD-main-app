package com.capstone.agrovision.view

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.capstone.agrovision.R
import com.capstone.agrovision.data.local.AppDatabase
import com.capstone.agrovision.data.local.Bookmark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailBookmarkActivity : AppCompatActivity() {

    private lateinit var savedImage: ImageView
    private lateinit var tvSavedResult: TextView
    private lateinit var tvSavedResultDesc: TextView
    private lateinit var bookmarkButton: ImageButton

    private var isBookmarked: Boolean = false
    private lateinit var bookmarkData: Bookmark

    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_bookmark)

        savedImage = findViewById(R.id.savedImage)
        tvSavedResult = findViewById(R.id.tvSavedResult)
        tvSavedResultDesc = findViewById(R.id.tvSavedResultDesc)
        bookmarkButton = findViewById(R.id.bookmark)

        // Retrieve the Bookmark object passed from BookmarkActivity
        bookmarkData = intent.getParcelableExtra("bookmark_data") ?: Bookmark(0, "", "", "")

        // Initialize the database
        database = AppDatabase.getDatabase(this)

        savedImage.setImageURI(Uri.parse(bookmarkData.imageUri))
        tvSavedResult.text = bookmarkData.result
        tvSavedResultDesc.text = bookmarkData.description

        checkBookmarkStatus()

        bookmarkButton.setOnClickListener {
            toggleBookmark()
        }

        setUpActionBar()
    }

    private fun setUpActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.detail_bookmark)
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

    private fun toggleBookmark() {
        if (isBookmarked) {
            removeFromBookmark()
        } else {
            saveToBookmark()
        }
        isBookmarked = !isBookmarked
        updateBookmarkIcon()
    }

    private fun saveToBookmark() {
        lifecycleScope.launch(Dispatchers.IO) {
            database.bookmarkDao().insertBookmark(bookmarkData)
            isBookmarked = true
            withContext(Dispatchers.Main) {
                updateBookmarkIcon()
            }
        }
    }

    private fun removeFromBookmark() {
        lifecycleScope.launch(Dispatchers.IO) {
            database.bookmarkDao().deleteBookmark(bookmarkData.id)
            isBookmarked = false
            withContext(Dispatchers.Main) {
                updateBookmarkIcon()
            }
        }
    }

    private fun checkBookmarkStatus() {
        lifecycleScope.launch(Dispatchers.IO) {
            val bookmark = database.bookmarkDao().getBookmarkByUriAndDescription(bookmarkData.imageUri, bookmarkData.description)
            isBookmarked = bookmark != null
            withContext(Dispatchers.Main) {
                updateBookmarkIcon()
            }
        }
    }

    private fun updateBookmarkIcon() {
        val drawableId = if (isBookmarked) R.drawable.ic_bookmark else R.drawable.ic_bookmark_outline
        bookmarkButton.setImageDrawable(ContextCompat.getDrawable(this, drawableId))
    }
}
