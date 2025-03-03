package com.android.sigemesapp.data.source.local

data class UserModel(
    val email: String,
    val fullname: String,
    val token: String,
    val isLogin: Boolean = false,
    val gender: String,
    val profile_picture: String,
    val phone_number: String,
    val id: Int
)