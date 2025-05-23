package com.android.sigemesapp.data.remote.response

import com.android.sigemesapp.data.model.RoomItem
import com.google.gson.annotations.SerializedName

data class GuesthouseRoomsResponse(

	@field:SerializedName("data")
	val data: List<RoomItem>,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)



