package com.android.sigemesapp.data.remote

import com.google.gson.annotations.SerializedName

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

data class CreateRentRequest(
    @SerializedName("guesthouse_room_pricing_id")
    val guesthouseRoomPricingId: Int?,

    @SerializedName("city_hall_pricing_id")
    val cityHallPricingId: Int?,

    @SerializedName("slot")
    val slot: Int,

    @SerializedName("start_date")
    val startDate: String,

    @SerializedName("end_date")
    val endDate: String,

    @SerializedName("renter_gender")
    val renterGender: String
)

data class DeletedMediaObject(
    val id: Int,
    val url: String
)

data class ReviewMedia(
    val id: Int,
    val url: String,
    var uri: String? = null
)

data class ErrorResponse(
    val status: Boolean,
    val message: String
)

data class RegisterValidationResult(
    val isFullnameValid: Boolean,
    val isEmailValid: Boolean,
    val isPasswordValid: Boolean,
    val isPhoneNumberValid: Boolean,
    val isGenderValid: Boolean,
    val genderValue: String = "",
)