package com.android.sigemesapp.data.source.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class CancelGuesthouseRentResponse(

	@field:SerializedName("data")
	val data: GuesthouseRentData,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)

@Parcelize
data class GuesthouseRoom(

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
	val media: List<MediaItem>,

	@field:SerializedName("type")
	val type: String,

	@field:SerializedName("facilities")
	val facilities: String,

	@field:SerializedName("total_slot")
	val totalSlot: Int,

	@field:SerializedName("guesthouse")
	val guesthouse: Guesthouse,

	@field:SerializedName("status")
	val status: String
): Parcelable

@Parcelize
data class Guesthouse(

	@field:SerializedName("address")
	val address: String,

	@field:SerializedName("contact_person")
	val contactPerson: String,

	@field:SerializedName("area_m2")
	val areaM2: Double,

	@field:SerializedName("latitude")
	val latitude: Double,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("facilities")
	val facilities: String,

	@field:SerializedName("longitude")
	val longitude: Double,

	@field:SerializedName("guesthouse_media")
	val guesthouseMedia: List<GuesthouseMediaItem>
): Parcelable
