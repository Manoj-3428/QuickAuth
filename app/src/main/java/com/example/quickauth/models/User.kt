package com.example.quickauth.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isVerified: Boolean = false
) : Parcelable
