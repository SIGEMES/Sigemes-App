package com.android.sigemesapp.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class CityHallResponse(

	@field:SerializedName("data")
	val data: CityHallData,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)

data class CityHallData(

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

	@field:SerializedName("media")
	val media: List<CityHallMediaItem>,

	@field:SerializedName("people_capacity")
	val peopleCapacity: Int,

	@field:SerializedName("pricing")
	val pricing: List<CityHallPricingItem>,

	@field:SerializedName("longitude")
	val longitude: Double,

	@field:SerializedName("status")
	val status: String
)

data class CityHallMediaItem(

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("url")
	val url: String
)

data class CityHallPricingItem(

	@field:SerializedName("is_active")
	val isActive: Boolean,

	@field:SerializedName("activity_type")
	val activityType: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("facilities")
	val facilities: String,

	@field:SerializedName("price_per_day")
	val pricePerDay: Int
)
