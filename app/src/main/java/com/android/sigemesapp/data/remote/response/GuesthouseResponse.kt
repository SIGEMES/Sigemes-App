package com.android.sigemesapp.data.remote.response

import com.android.sigemesapp.data.model.Guesthouse
import com.google.gson.annotations.SerializedName

data class GuesthouseResponse(

	@field:SerializedName("data")
	val data: Guesthouse,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)
