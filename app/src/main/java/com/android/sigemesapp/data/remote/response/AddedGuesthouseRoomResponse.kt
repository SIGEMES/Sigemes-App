package com.android.sigemesapp.data.remote.response

import com.android.sigemesapp.data.model.GuesthouseRoomReviews
import com.google.gson.annotations.SerializedName

data class AddedGuesthouseRoomResponse(

	@field:SerializedName("data")
	val data: GuesthouseRoomReviews,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)

