package com.capstone.agrovision.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.agrovision.R
import com.capstone.agrovision.data.local.BookmarkResult

class DetailBookmarkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_bookmark)

        val bookmark = intent.getParcelableExtra<BookmarkResult>("bookmark_data")

        // Use the bookmark data
        if (bookmark != null) {
            // Do something with the bookmark data
        }
    }
}
