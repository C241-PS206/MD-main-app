package com.capstone.agrovision.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.capstone.agrovision.R
import com.capstone.agrovision.view.timeline.TimelineActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var imageProfile: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        supportActionBar?.title = getString(R.string.setting)

        setupButtonListeners()
        setupBottomNavigation()

        imageProfile = findViewById(R.id.imageProfile)

        loadSavedImageUri()?.let {
            imageProfile.setImageURI(Uri.parse(it))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout_appbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_logout -> {
                val intent = Intent(this, LandingPageActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.menuBar)
        bottomNavigationView.selectedItemId = R.id.settings
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

                R.id.settings -> true
                else -> false
            }
        }
        bottomNavigationView.itemIconTintList = ContextCompat.getColorStateList(this, R.color.menu_icon_color_selector)
    }

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val imageUri: Uri? = result.data!!.data
            if (imageUri != null) {
                saveImageUri(imageUri.toString())
                imageProfile.setImageURI(imageUri)
            } else {
                showToast("Failed to load image")
            }
        }
    }

    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri = result.data?.getStringExtra(SettingCameraActivity.EXTRA_CAMERAX_IMAGE)
            imageUri?.let {
                saveImageUri(it)
                imageProfile.setImageURI(Uri.parse(it))
            }
        }
    }

    private fun setupButtonListeners() {
        val uploadPhotoButton: ImageButton = findViewById(R.id.uploadPhoto)
        uploadPhotoButton.setOnClickListener {
            val intent = Intent(this, SettingCameraActivity::class.java)
            cameraActivityResultLauncher.launch(intent)
        }

        val uploadImageButton: ImageButton = findViewById(R.id.uploadImage)
        uploadImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryActivityResultLauncher.launch(intent)
        }

        val languageSettingsButton: ImageButton = findViewById(R.id.btnLanguage)
        languageSettingsButton.setOnClickListener {
            val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun saveImageUri(uri: String) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_IMAGE_URI, uri).apply()
    }

    private fun loadSavedImageUri(): String? {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_IMAGE_URI, null)
    }

    private fun navigateTo(activityClass: Class<*>) {
        startActivity(Intent(this, activityClass))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val PREFS_NAME = "settings_prefs"
        private const val KEY_IMAGE_URI = "image_uri"
    }

}