package com.capstone.agrovision.view.result

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Result(
    val id: Int,
    val result: String,
    val description: String,
    val imageResource: Int
) : Parcelable