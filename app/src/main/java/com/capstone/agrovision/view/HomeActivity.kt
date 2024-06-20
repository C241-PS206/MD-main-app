package com.capstone.agrovision.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.capstone.agrovision.R
import com.capstone.agrovision.view.bookmark.BookmarkActivity
import com.capstone.agrovision.view.timeline.TimelineActivity
import com.capstone.agrovision.view.addGallery.FeatureCameraActivity
import com.capstone.agrovision.view.news.NewsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val newsCard: CardView = findViewById(R.id.newsCard)
        val bookmarksCard: CardView = findViewById(R.id.bookmarksCard)
        val fabCamera: FloatingActionButton = findViewById(R.id.fabCamera)

        newsCard.setOnClickListener {
            val intent = Intent(this, NewsActivity::class.java)
            startActivity(intent)
        }

        bookmarksCard.setOnClickListener {
            val intent = Intent(this, BookmarkActivity::class.java)
            startActivity(intent)
        }

        fabCamera.setOnClickListener {
            val intent = Intent(this, FeatureCameraActivity::class.java)
            startActivity(intent)
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.menuBar)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    navigateTo(HomeActivity::class.java)
                    true
                }
                R.id.timeline -> {
                    navigateTo(TimelineActivity::class.java)
                    true
                }
                R.id.settings -> {
                    navigateTo(SettingsActivity::class.java)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupButtonListeners() {
    }

    private fun navigateTo(activityClass: Class<*>) {
        startActivity(Intent(this, activityClass))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu, menu)
        return true
    }
}
