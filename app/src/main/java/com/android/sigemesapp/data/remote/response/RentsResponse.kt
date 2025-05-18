package com.android.sigemesapp.data.remote.response

import com.android.sigemesapp.data.model.Rents
import com.google.gson.annotations.SerializedName

data class RentsResponse(
	@field:SerializedName("data")
	val data: List<Rents>,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)


