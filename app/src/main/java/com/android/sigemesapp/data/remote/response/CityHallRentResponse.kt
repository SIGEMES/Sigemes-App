package com.android.sigemesapp.data.remote.response

import com.android.sigemesapp.data.model.CityHallRent
import com.google.gson.annotations.SerializedName

data class CityHallRentResponse(

	@field:SerializedName("data")
	val data: CityHallRent,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)




