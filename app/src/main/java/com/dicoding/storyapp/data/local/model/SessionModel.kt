package com.dicoding.storyapp.data.local.model

data class SessionModel(
    val name: String,
    val userId: String,
    val token: String,
    val isLogin: Boolean
)
