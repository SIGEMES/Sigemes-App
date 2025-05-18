package com.android.sigemesapp.data.remote.response

import com.android.sigemesapp.data.model.UserLoginData
import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("data")
	val data: UserLoginData,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)
