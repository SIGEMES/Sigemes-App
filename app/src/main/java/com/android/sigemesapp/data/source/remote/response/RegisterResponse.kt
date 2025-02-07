package com.android.sigemesapp.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("data")
	val data: RegisterData,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)

data class RegisterData(

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
