package com.android.sigemesapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class RenterData(

    @field:SerializedName("email_verified")
    val emailVerified: Boolean,

    @field:SerializedName("gender")
    val gender: String,

    @field:SerializedName("phone_number")
    val phoneNumber: String,

    @field:SerializedName("profile_picture")
    val profilePicture: String,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("fullname")
    val fullname: String,

    @field:SerializedName("email")
    val email: String
)

@Parcelize
data class Renter(

    @field:SerializedName("gender")
    val gender: String,

    @field:SerializedName("phone_number")
    val phoneNumber: String,

    @field:SerializedName("profile_picture")
    val profilePicture: String,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("fullname")
    val fullname: String,

    @field:SerializedName("email")
    val email: String
): Parcelable


data class UserLoginData(

    @field:SerializedName("email_verified")
    val emailVerified: Boolean,

    @field:SerializedName("gender")
    val gender: String,

    @field:SerializedName("profile_picture")
    val profilePicture: String,

    @field:SerializedName("phone_number")
    val phoneNumber: String,

    @field:SerializedName("fullname")
    val fullname: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("token")
    val token: String,

    @field:SerializedName("id")
    val id: Int
)
