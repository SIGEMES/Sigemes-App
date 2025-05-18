package com.android.sigemesapp.data.remote.response

import com.android.sigemesapp.data.model.Guesthouse
import com.google.gson.annotations.SerializedName

data class GuesthousesResponse(

	@field:SerializedName("data")
	val data: List<Guesthouse>,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)

