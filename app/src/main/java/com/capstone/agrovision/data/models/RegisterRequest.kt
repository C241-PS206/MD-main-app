package com.capstone.agrovision.data.models

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)