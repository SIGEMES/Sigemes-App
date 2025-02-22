package com.android.sigemesapp.data.source.local

data class UserModel(
    val email: String,
    val fullname: String,
    val token: String,
    val isLogin: Boolean = false,
    val id: Int
)