package com.android.sigemesapp.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class CityHallReviewsResponse(

	@field:SerializedName("data")
	val data: List<CityHallReviews>,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)

data class ReviewMediaItem(

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("url")
	val url: String
)

data class CityHallReviews(

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
	val rent: CityHallRent,

	@field:SerializedName("rentId")
	val rentId: Int
)

data class ReviewReply(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("adminId")
	val adminId: Int,

	@field:SerializedName("comment")
	val comment: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("reviewId")
	val reviewId: Int,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)

