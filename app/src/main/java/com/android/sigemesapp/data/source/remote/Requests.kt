package com.android.sigemesapp.data.source.remote

data class RegisterRequest(
    val email: String,
    val password: String,
    val fullname: String,
    val phone_number: String,
    val gender: String
)

data class LoginRequest(
    val email: String,
    val password: String,
)

data class SendOtpRequest(
    val email: String
)

data class VerifyOtpRequest(
    val email: String,
    val otp: String
)

data class ChangePasswordRequest(
    val email: String,
    val new_password: String
)