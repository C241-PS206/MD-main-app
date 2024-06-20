package com.capstone.agrovision.view

import android.content.Intent
import android.os.Bundle
import android.util.Log

import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.agrovision.R
import com.capstone.agrovision.data.models.RegisterRequest
import com.capstone.agrovision.data.models.AuthResponse
import com.capstone.agrovision.data.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val btnBackToLogin = findViewById<Button>(R.id.loginTab)

        val nameEditText: EditText = findViewById(R.id.usernameInput)
        val emailEditText: EditText = findViewById(R.id.emailInput)
        val passwordEditText: EditText = findViewById(R.id.passwordInput)
        val registerButton: Button = findViewById(R.id.btnSignup)

        registerButton.setOnClickListener {
            val username = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val registerRequest = RegisterRequest(username, email, password)

            ApiClient.apiService.registerUser(registerRequest).enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    if (response.isSuccessful) {
                        val authResponse = response.body()
                        Log.d("RegisterActivity", "Token: ${authResponse?.token}")
                        // Simpan token di SharedPreferences atau lakukan sesuatu dengan token
                        Toast.makeText(this@SignupActivity, getString(R.string.signup_success), Toast.LENGTH_SHORT).show()

                        // Anda bisa langsung login atau kembali ke halaman login
                        finish()
                    } else {
                        Log.e("RegisterActivity", "Error: ${response.errorBody()?.string()}")
                        Toast.makeText(this@SignupActivity, getString(R.string.signup_error), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    Log.e("RegisterActivity", "Failure: ${t.message}")
                    Toast.makeText(this@SignupActivity, getString(R.string.error), Toast.LENGTH_SHORT).show()
                }
            })
        }

        btnBackToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}