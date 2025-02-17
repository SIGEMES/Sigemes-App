package com.android.sigemesapp.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class DetailRoomResponse(

	@field:SerializedName("data")
	val data: DetailRoom,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)

data class PricingItemRoom(

	@field:SerializedName("is_active")
	val isActive: Boolean,

	@field:SerializedName("retribution_type")
	val retributionType: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("price_per_day")
	val pricePerDay: Int
)

data class DetailRoom(

	@field:SerializedName("area_m2")
	val areaM2: Int,

	@field:SerializedName("available_slot")
	val availableSlot: Int,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("guesthouse_id")
	val guesthouseId: Int,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("media")
	val media: List<DetailMediaItemRoom>,

	@field:SerializedName("type")
	val type: String,

	@field:SerializedName("facilities")
	val facilities: String,

	@field:SerializedName("total_slot")
	val totalSlot: Int,

	@field:SerializedName("pricing")
	val pricing: List<PricingItemRoom>,

	@field:SerializedName("status")
	val status: String
)

data class DetailMediaItemRoom(

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("url")
	val url: String
)
