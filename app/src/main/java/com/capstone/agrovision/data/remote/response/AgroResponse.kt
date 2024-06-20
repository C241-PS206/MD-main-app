package com.capstone.agrovision.data.remote.response

import com.google.gson.annotations.SerializedName

data class AgroResponse(
    @field:SerializedName("result")
    val result: String,

    @field:SerializedName("accuracy")
    val accuracy: String,

    @field:SerializedName("solutions")
    val solutions: String
)