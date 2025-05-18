package com.android.sigemesapp.data.remote.response

import com.android.sigemesapp.data.model.GuesthouseRent
import com.google.gson.annotations.SerializedName

data class GuesthouseRentResponse(

	@field:SerializedName("data")
	val data: GuesthouseRent,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)



