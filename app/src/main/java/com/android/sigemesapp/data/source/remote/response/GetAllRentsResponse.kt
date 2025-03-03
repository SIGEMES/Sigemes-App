package com.android.sigemesapp.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class GetAllRentsResponse(

	@field:SerializedName("data")
	val data: List<RentsDataItem>,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)

data class RentsDataItem(

	@field:SerializedName("renter_id")
	val renterId: Int,

	@field:SerializedName("end_date")
	val endDate: String,

	@field:SerializedName("check_in")
	val checkIn: String,

	@field:SerializedName("rent_status")
	val rentStatus: String,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("slot")
	val slot: Int,

	@field:SerializedName("renter_gender")
	val renterGender: String,

	@field:SerializedName("city_hall_pricing")
	val cityHallPricing: CityHallPricing?,

	@field:SerializedName("check_out")
	val checkOut: String,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("guesthouse_room_pricing")
	val guesthouseRoomPricing: GuesthouseRoomPricing?,

	@field:SerializedName("start_date")
	val startDate: String
)

