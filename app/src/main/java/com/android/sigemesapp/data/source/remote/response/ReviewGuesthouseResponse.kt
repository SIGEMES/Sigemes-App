package com.android.sigemesapp.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class ReviewGuesthouseResponse(

	@field:SerializedName("data")
	val data: AddedGuesthouseReview,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)

data class AddedGuesthouseReview(

	@field:SerializedName("review_reply")
	val reviewReply: Any,

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
