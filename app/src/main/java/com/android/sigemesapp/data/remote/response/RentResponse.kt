package com.android.sigemesapp.data.remote.response

import com.android.sigemesapp.data.model.Rent
import com.google.gson.annotations.SerializedName

data class RentResponse(
	@field:SerializedName("data")
	val data: Rent,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)

