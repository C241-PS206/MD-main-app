package com.capstone.agrovision.data.network


import com.capstone.agrovision.data.models.User
import com.capstone.agrovision.data.models.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @GET("users/{id}")
    fun getUser(@Path("id") id: Int): Call<User>

    @POST("users/login")
    fun loginUser(@Body request: LoginRequest): Call<AuthResponse>

    @POST("users/register")
    fun registerUser(@Body request: RegisterRequest): Call<AuthResponse>
}