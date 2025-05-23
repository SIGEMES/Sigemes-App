package com.android.sigemesapp.data.remote.response

import com.android.sigemesapp.data.model.CityHall
import com.google.gson.annotations.SerializedName

data class CityHallResponse(

	@field:SerializedName("data")
	val data: CityHall,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)



