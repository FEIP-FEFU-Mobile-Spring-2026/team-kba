package com.example.mobilestore.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Size(
    val id: String,
    val name: String
) : Parcelable