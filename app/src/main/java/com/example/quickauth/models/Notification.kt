package com.example.quickauth.models

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val icon: Int,
    val isRead: Boolean = false
)
