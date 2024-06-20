package com.capstone.agrovision.view.addGallery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.agrovision.R
import com.capstone.agrovision.databinding.ActivityFiturCameraBinding
import com.capstone.agrovision.ml.Model81OnVal
import com.capstone.agrovision.view.timeline.TimelineActivity
import com.capstone.agrovision.view.HomeActivity
import com.capstone.agrovision.view.result.ResultActivity
import com.capstone.agrovision.view.SettingsActivity
import com.capstone.agrovision.view.upload.Utils.reduceFileImage
import com.capstone.agrovision.view.upload.Utils.uriToFile
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class FeatureCameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFiturCameraBinding
//    private lateinit var bottomNavigationView: BottomNavigationView
    private var currentImageUri: Uri? = null
    private var result: String? = null
    private var resultDescription: String? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, getString(R.string.permission_granted), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityFiturCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        ViewCompat.setOnApplyWindowInsetsListener(binding.menuBar) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        setUpActionBar()
//        setupBottomNavigation()

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.cameraButton.setOnClickListener {
            startCameraX()
        }

        binding.galleryButton.setOnClickListener {
            startGallery()
        }

        binding.buttonAdd.setOnClickListener {
            analyzeImage()
        }
    }

    private fun setUpActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.analyze)
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
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                currentImageUri = uri
                showImage()
            } ?: showToast("Failed to get image URI")
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val uriString = result.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)
            uriString?.let {
                currentImageUri = Uri.parse(it)
                showImage()
            } ?: showToast("Failed to get camera image URI")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d(TAG, "Displaying image: $it")
            binding.previewImageView.setImageURI(it)
        } ?: Log.d(TAG, "No image to display")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun analyzeImage() {
        currentImageUri?.let { uri ->
            val source = ImageDecoder.createSource(contentResolver, uri)
            val bitmap = ImageDecoder.decodeBitmap(source)
            val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
            val softwareBitmap = resized.copy(Bitmap.Config.ARGB_8888, true)
            val imageFile = uriToFile(uri, this).reduceFileImage()

            runInference(softwareBitmap)
            Log.d("inferRes", "inference result: $result") // For testing purposes
            softwareBitmap.recycle()
            saveImageLocally(imageFile)
            navigateTo(ResultActivity::class.java)
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun saveImageLocally(file: File) {
    }

    private fun uploadImageToServer(file: File) {
    }

//    private fun setupBottomNavigation() {
//        bottomNavigationView = findViewById(R.id.menuBar)
//        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.home -> {
//                    navigateTo(HomeActivity::class.java)
//                    true
//                }
//                R.id.timeline -> {
//                    navigateTo(TimelineActivity::class.java)
//                    true
//                }
//                R.id.settings -> {
//                    navigateTo(SettingsActivity::class.java)
//                    true
//                }
//                else -> false
//            }
//        }
//    }

    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        if (activityClass == ResultActivity::class.java) {
            currentImageUri?.let { uri ->
                intent.putExtra(ResultActivity.IMAGE_URI, uri.toString())
            }
            intent.putExtra(ResultActivity.RESULT, result)
            intent.putExtra(ResultActivity.RESULT_DESCRIPTION, resultDescription)
        }
        startActivity(intent)
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun runInference(image: Bitmap) {
        try {
            val model = Model81OnVal.newInstance(applicationContext)

            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
            val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
            byteBuffer.order(ByteOrder.nativeOrder())
            inputFeature0.loadBuffer(byteBuffer)
            val intValues = IntArray(224*224)
            image.getPixels(intValues,0,image.width,0,0,image.width,image.height)
            var pixel = 0

            for (i in 0 until 224) {
                for (j in 0 until 224) {
                    val rgb = intValues[pixel++]
                    byteBuffer.putFloat((rgb shr 16 and 0xFF).toFloat())
                    byteBuffer.putFloat((rgb shr 8 and 0xFF).toFloat())
                    byteBuffer.putFloat(((rgb and 0xFF).toFloat()))
                }
            }
            inputFeature0.loadBuffer(byteBuffer)

            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer
            val confidences = outputFeature0.floatArray

            var maxConfidence = 0f
            var maxPos = 0
            for (i in confidences.indices) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i]
                    maxPos = i
                }
            }

            val classes = arrayOf(
                "Cassava Bacterial Blight",
                "Cassava Brown Streak",
                "Cassava Green Mottle",
                "Cassava Healthy",
                "Cassava Mosaic",
                "Corn Blight",
                "Corn Gray Leaf Spot",
                "Corn Healthy",
                "Corn Rust",
                "Rice Bacterial Blight",
                "Rice Brown Spot",
                "Rice Healthy",
                "Rice Leaf Blast",
                "Rice Tungro",
                "Sugarcane Healthy",
                "Sugarcane Mosaic",
                "Sugarcane Red Rot",
                "Sugarcane Rust",
                "Sugarcane Yellow"
            )

            val classDesc = arrayOf(
                getString(R.string.cassava_bacterial_bright),
                getString(R.string.cassava_brown_streak),
                getString(R.string.cassava_green_mottle),
                getString(R.string.cassava_healthy),
                getString(R.string.cassava_mosaic),
                getString(R.string.corn_blight),
                getString(R.string.corn_gray_leaf_spot),
                getString(R.string.corn_healthy),
                getString(R.string.corn_rust),
                getString(R.string.rice_bacterial_blight),
                getString(R.string.rice_brown_spot),
                getString(R.string.rice_healthy),
                getString(R.string.rice_leaf_blast),
                getString(R.string.rice_tungro),
                getString(R.string.sugarcane_healthy),
                getString(R.string.sugarcane_mosaic),
                getString(R.string.sugarcane_red_rot),
                getString(R.string.sugarcane_rust),
                getString(R.string.sugarcane_yellow)
            )

            result = classes[maxPos]
            resultDescription = classDesc[maxPos]

            model.close()
        } catch (e: IOException){
            Toast.makeText(this,R.string.image_error, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val TAG = "ImagePicker"
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}