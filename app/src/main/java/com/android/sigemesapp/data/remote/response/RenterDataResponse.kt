package com.android.sigemesapp.data.remote.response

import com.android.sigemesapp.data.model.RenterData
import com.google.gson.annotations.SerializedName

data class RenterDataResponse(
	@field:SerializedName("data")
	val data: RenterData,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)





