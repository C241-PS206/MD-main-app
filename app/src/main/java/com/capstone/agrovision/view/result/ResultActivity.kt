package com.capstone.agrovision.view.result

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.capstone.agrovision.R
import com.capstone.agrovision.data.local.AppDatabase
import com.capstone.agrovision.data.local.Bookmark
import com.capstone.agrovision.view.HomeActivity
import com.capstone.agrovision.view.bookmark.BookmarkActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResultActivity : AppCompatActivity() {

    private lateinit var imageResult: ImageView
    private lateinit var tvResult: TextView
    private lateinit var tvDescriptionResult: TextView
    private lateinit var btnHome: Button
    private lateinit var bookmarkButton: ImageButton

    private var isBookmarked: Boolean = false
    private var imageUri: String? = null
    private var result: String? = null
    private var description: String? = null

    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        imageResult = findViewById(R.id.imageResult)
        tvResult = findViewById(R.id.tvResult)
        tvDescriptionResult = findViewById(R.id.tvDescriptionResult)
        btnHome = findViewById(R.id.btnHome)
        bookmarkButton = findViewById(R.id.bookmark)

        database = AppDatabase.getDatabase(this)

        imageUri = intent.getStringExtra(IMAGE_URI)
        result = intent.getStringExtra(RESULT)
        description = intent.getStringExtra(RESULT_DESCRIPTION)

        if (imageUri != null) {
            imageResult.setImageURI(Uri.parse(imageUri))
        }
        tvResult.text = result ?: getString(R.string.result)
        tvDescriptionResult.text = description ?: getString(R.string.default_result_description)

        updateBookmarkIcon()

        checkBookmarkStatus()

        btnHome.setOnClickListener {
            navigateToHome()
        }

        bookmarkButton.setOnClickListener {
            toggleBookmark()
        }

        setUpActionBar()
    }

    private fun setUpActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.result_appbar)
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

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun toggleBookmark() {
        isBookmarked = !isBookmarked
        updateBookmarkIcon()

        lifecycleScope.launch(Dispatchers.IO) {
            if (isBookmarked) {
                saveToBookmark()
                navigateToBookmarkActivity()
            } else {
                removeFromBookmark()
            }
        }
    }

    private suspend fun saveToBookmark() {
        if (imageUri != null && description != null) {
            val bookmark = Bookmark(imageUri = imageUri!!, result = result ?: "", description = description ?: "")
            withContext(Dispatchers.IO) {
                database.bookmarkDao().insertBookmark(bookmark)
            }
        }
    }

    private suspend fun removeFromBookmark() {
        if (imageUri != null && description != null) {
            withContext(Dispatchers.IO) {
                val bookmark = database.bookmarkDao().getBookmarkByUriAndDescription(imageUri!!, description!!)
                bookmark?.let {
                    database.bookmarkDao().deleteBookmark(it.id)
                }
            }
        }
    }

    private fun checkBookmarkStatus() {
        lifecycleScope.launch(Dispatchers.IO) {
            if (imageUri != null && description != null) {
                val bookmark = database.bookmarkDao().getBookmarkByUriAndDescription(imageUri!!, description!!)
                isBookmarked = bookmark != null
                withContext(Dispatchers.Main) {
                    updateBookmarkIcon()
                }
            }
        }
    }

    private fun updateBookmarkIcon() {
        val drawableId = if (isBookmarked) R.drawable.ic_bookmark else R.drawable.ic_bookmark_outline
        bookmarkButton.setImageDrawable(ContextCompat.getDrawable(this, drawableId))
    }

    private fun navigateToBookmarkActivity() {
        val intent = Intent(this, BookmarkActivity::class.java)
        startActivity(intent)
    }

    companion object {
        const val IMAGE_URI = "image_uri"
        const val RESULT = "result"
        const val RESULT_DESCRIPTION = "result_description"
    }
}
