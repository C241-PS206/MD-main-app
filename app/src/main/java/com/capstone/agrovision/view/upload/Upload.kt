package com.capstone.agrovision.view.upload

import android.net.Uri

data class Upload(
    val userName: String,
    val postTime: String,
    val postContent: String,
    val postImage: Uri
)
