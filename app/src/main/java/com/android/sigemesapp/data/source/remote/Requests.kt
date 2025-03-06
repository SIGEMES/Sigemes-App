package com.android.sigemesapp.data.source.remote

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

data class CreateGuesthouseRentRequest(
    val guesthouse_room_pricing_id: Int,
    val city_hall_pricing_id: Int?,
    val slot: Int,
    val start_date: String,
    val end_date: String,
    val renter_gender: String
)

data class CreateCityHallRentRequest(
    @SerializedName("guesthouse_room_pricing_id")
    val guesthouseRoomPricingId: Int?,

    @SerializedName("city_hall_pricing_id")
    val cityHallPricingId: Int,

    @SerializedName("slot")
    val slot: Int,

    @SerializedName("start_date")
    val startDate: String,

    @SerializedName("end_date")
    val endDate: String,

    @SerializedName("renter_gender")
    val renterGender: String
)