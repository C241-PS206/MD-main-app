package com.capstone.agrovision.view.upload

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.capstone.agrovision.R
import com.capstone.agrovision.view.timeline.TimelineActivity
import com.capstone.agrovision.view.upload.Utils.uriToFile
import java.io.File
import java.io.FileOutputStream

class UploadActivity : AppCompatActivity() {

    private lateinit var messageEditText: EditText
    private lateinit var previewImage: ImageView
    private lateinit var iconButton1: ImageButton
    private lateinit var iconButton2: ImageButton
    private lateinit var postButton: Button
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        messageEditText = findViewById(R.id.messageEditText)
        previewImage = findViewById(R.id.previewImage)
        iconButton1 = findViewById(R.id.iconButton1)
        iconButton2 = findViewById(R.id.iconButton2)
        postButton = findViewById(R.id.postButton)

        iconButton1.setOnClickListener {
            startGallery()
        }

        iconButton2.setOnClickListener {
            startCamera()
        }

        postButton.setOnClickListener {
            saveImageAndDescription()
        }

        setUpActionBar()
    }

    private fun setUpActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.upload_timeline)
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

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        launcherGallery.launch(intent)
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            result.data?.data?.let { uri ->
                currentImageUri = uri
                showImage()
            }
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            previewImage.setImageURI(it)
        }
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        launcherCamera.launch(intent)
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                val uri = saveBitmapToFile(it)
                currentImageUri = uri
                showImage()
            }
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap): Uri {
        val file = File(cacheDir, "${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        return file.toUri()
    }

    private fun saveImageAndDescription() {
        currentImageUri?.let { uri ->
            val description = messageEditText.text.toString()
            val intent = Intent().apply {
                putExtra("imageUri", uri.toString())
                putExtra("description", description)
            }
            setResult(RESULT_OK, intent)
            finish()
        } ?: showToast("No image selected")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
