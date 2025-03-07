package com.android.sigemesapp.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class GetReviewGuesthouseByIdResponse(

	@field:SerializedName("data")
	val data: GuesthouseRoomReviews?,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)


