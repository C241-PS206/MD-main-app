package com.capstone.agrovision.view.timeline

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.agrovision.view.HomeActivity
import com.capstone.agrovision.R
import com.capstone.agrovision.view.SettingsActivity
import com.capstone.agrovision.view.upload.UploadActivity
import com.capstone.agrovision.view.upload.Upload
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TimelineActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var uploads: MutableList<Upload>
    private lateinit var adapter: TimelineAdapter

    companion object {
        private const val UPLOAD_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        recyclerView = findViewById(R.id.rvTimeline)
        recyclerView.layoutManager = LinearLayoutManager(this)
        uploads = getUploads().toMutableList()
        adapter = TimelineAdapter(uploads)
        recyclerView.adapter = adapter

        supportActionBar?.title = getString(R.string.timeline_appbar)

        setupBottomNavigation()

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivityForResult(intent, UPLOAD_REQUEST_CODE)
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.menuBar)
        bottomNavigationView.selectedItemId = R.id.timeline
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    navigateTo(HomeActivity::class.java)
                    true
                }
                R.id.timeline -> true
                R.id.settings -> {
                    navigateTo(SettingsActivity::class.java)
                    true
                }
                else -> false
            }
        }
        bottomNavigationView.itemIconTintList = ContextCompat.getColorStateList(this, R.color.menu_icon_color_selector)
    }

    private fun navigateTo(activityClass: Class<*>) {
        startActivity(Intent(this, activityClass))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu, menu)
//        return true
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPLOAD_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageUriString = data?.getStringExtra("imageUri")
            val description = data?.getStringExtra("description")

            if (imageUriString != null && description != null) {
                val imageUri = Uri.parse(imageUriString)

                val newUpload = Upload("Current User", "Just Now", description, imageUri)
                uploads.add(0, newUpload)

                adapter.notifyDataSetChanged()

                showToast("Upload berhasil")
            } else {
                showToast("Gagal memproses upload")
            }
        }
    }

    private fun getUploads(): List<Upload> {
        return listOf(
            Upload("User1", "2 hours ago", "This is the first Upload", Uri.parse("android.resource://com.capstone.agrovision/" + R.drawable.ic_launcher_background)),
            Upload("User2", "5 hours ago", "This is the second Upload", Uri.parse("android.resource://com.capstone.agrovision/" + R.drawable.ic_launcher_background)),
            Upload("User3", "1 day ago", "This is the third Upload", Uri.parse("android.resource://com.capstone.agrovision/" + R.drawable.ic_launcher_background))
        )
    }
}
