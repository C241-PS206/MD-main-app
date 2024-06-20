package com.capstone.agrovision.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.capstone.agrovision.R

class SplashscreenActivity : AppCompatActivity() {

    private val splashScreenDuration: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LandingPageActivity::class.java)
            startActivity(intent)
            finish()
        }, splashScreenDuration)
    }
}