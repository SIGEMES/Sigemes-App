package com.android.sigemesapp.data.remote.response

import com.android.sigemesapp.data.model.Rent
import com.google.gson.annotations.SerializedName

data class RentsResponse(
	@field:SerializedName("data")
	val data: List<Rent>,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)


