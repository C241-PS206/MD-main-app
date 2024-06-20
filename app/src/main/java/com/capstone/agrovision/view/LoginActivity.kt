package com.capstone.agrovision.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.agrovision.R
import com.capstone.agrovision.data.models.LoginRequest
import com.capstone.agrovision.data.models.AuthResponse
import com.capstone.agrovision.data.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin = findViewById<Button>(R.id.loginButton)
        val btnSignUp = findViewById<Button>(R.id.signupTab)
        val emailEditText: EditText = findViewById(R.id.emailInput)
        val passwordEditText: EditText = findViewById(R.id.passwordInput)
        val loginButton: Button = findViewById(R.id.loginButton)

        btnLogin.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val loginRequest = LoginRequest(email, password)

            ApiClient.apiService.loginUser(loginRequest).enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    if (response.isSuccessful) {
                        val authResponse = response.body()
                        Log.d("LoginActivity", "Token: ${authResponse?.token}")
                        Toast.makeText(this@LoginActivity, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.e("LoginActivity", "Error: ${response.errorBody()?.string()}")
                        Toast.makeText(this@LoginActivity, getString(R.string.login_error), Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    Log.e("LoginActivity", "Failure: ${t.message}")
                    Toast.makeText(this@LoginActivity, getString(R.string.login_error), Toast.LENGTH_SHORT).show()
                }
            })
        }

        btnSignUp.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}