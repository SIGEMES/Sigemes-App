package com.android.sigemesapp.data.source.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GuesthouseRoomsResponse(

	@field:SerializedName("data")
	val data: List<RoomItem>,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)

data class RoomItem(

	@field:SerializedName("area_m2")
	val areaM2: Double,

	@field:SerializedName("available_slot")
	val availableSlot: Int,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("guesthouse_id")
	val guesthouseId: Int,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("media")
	val media: List<MediaItem>,

	@field:SerializedName("type")
	val type: String,

	@field:SerializedName("facilities")
	val facilities: String,

	@field:SerializedName("total_slot")
	val totalSlot: Int,

	@field:SerializedName("pricing")
	val pricing: List<PricingItem>
)

data class PricingItem(

	@field:SerializedName("is_active")
	val isActive: Boolean,

	@field:SerializedName("retribution_type")
	val retributionType: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("price_per_day")
	val pricePerDay: Int
)

@Parcelize
data class MediaItem(

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("url")
	val url: String
): Parcelable
