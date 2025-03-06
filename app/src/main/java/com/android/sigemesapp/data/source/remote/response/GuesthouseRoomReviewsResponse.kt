package com.android.sigemesapp.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class GuesthouseRoomReviewsResponse(

	@field:SerializedName("data")
	val data: List<GuesthouseRoomReviews>,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)

data class GuesthouseRoomReviews(

	@field:SerializedName("review_reply")
	val reviewReply: ReviewReply?,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("review_media")
	val reviewMedia: List<ReviewMediaItem>,

	@field:SerializedName("rating")
	val rating: Int,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("comment")
	val comment: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("rent")
	val rent: GuesthouseRentData,

	@field:SerializedName("rentId")
	val rentId: Int
)

