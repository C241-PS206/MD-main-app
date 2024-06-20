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
import com.capstone.agrovision.data.local.BookmarkResult
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
            } else {
                removeFromBookmark()
            }
        }
    }

    private suspend fun saveToBookmark() {
        if (imageUri != null && result != null) {
            val bookmark = BookmarkResult(imagePath = imageUri!!, result = result!!)
            database.bookmarkResultDao().add(bookmark)
        }
    }

    private suspend fun removeFromBookmark() {
        if (imageUri != null && result != null) {
            val bookmark = database.bookmarkResultDao().getBookmarkByUriAndResult(imageUri!!, result!!)
            bookmark?.let {
                database.bookmarkResultDao().delete(it)
            }
        }
    }

    private fun checkBookmarkStatus() {
        lifecycleScope.launch(Dispatchers.IO) {
            if (imageUri != null && result != null) {
                val bookmark = database.bookmarkResultDao().getBookmarkByUriAndResult(imageUri!!, result!!)
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

    companion object {
        const val IMAGE_URI = "image_uri"
        const val RESULT = "result"
        const val RESULT_DESCRIPTION = "result_description"
    }
}
