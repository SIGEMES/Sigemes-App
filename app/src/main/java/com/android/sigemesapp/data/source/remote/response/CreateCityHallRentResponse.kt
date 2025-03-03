package com.android.sigemesapp.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class CreateCityHallRentResponse(

	@field:SerializedName("data")
	val data: CityHallRent,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)

data class CityHallRentMediaItem(

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("url")
	val url: String
)

data class CityHallPricing(

	@field:SerializedName("is_active")
	val isActive: Boolean,

	@field:SerializedName("city_hall")
	val cityHall: CityHall,

	@field:SerializedName("activity_type")
	val activityType: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("facilities")
	val facilities: String,

	@field:SerializedName("price_per_day")
	val pricePerDay: Int
)

data class CityHallRenter(

	@field:SerializedName("gender")
	val gender: String,

	@field:SerializedName("phone_number")
	val phoneNumber: String,

	@field:SerializedName("profile_picture")
	val profilePicture: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("fullname")
	val fullname: String,

	@field:SerializedName("email")
	val email: String
)

data class CityHall(

	@field:SerializedName("address")
	val address: String,

	@field:SerializedName("contact_person")
	val contactPerson: String,

	@field:SerializedName("area_m2")
	val areaM2: Int,

	@field:SerializedName("latitude")
	val latitude: Any,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("media")
	val media: List<CityHallRentMediaItem>,

	@field:SerializedName("people_capacity")
	val peopleCapacity: Int,

	@field:SerializedName("longitude")
	val longitude: Any,

	@field:SerializedName("status")
	val status: String
)

data class CityHallRent(

	@field:SerializedName("renter_id")
	val renterId: Int,

	@field:SerializedName("end_date")
	val endDate: String,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("rent_status")
	val rentStatus: String,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("payment")
	val payment: PaymentCityHall,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("slot")
	val slot: Int,

	@field:SerializedName("renter_gender")
	val renterGender: String,

	@field:SerializedName("renter")
	val renter: CityHallRenter,

	@field:SerializedName("city_hall_pricing")
	val cityHallPricing: CityHallPricing,

	@field:SerializedName("start_date")
	val startDate: String
)

data class PaymentCityHall(

	@field:SerializedName("amount")
	val amount: Int,

	@field:SerializedName("payment_gateway_token")
	val paymentGatewayToken: String,

	@field:SerializedName("method")
	val method: String,

	@field:SerializedName("rent_id")
	val rentId: Int,

	@field:SerializedName("payment_confirmed_at")
	val paymentConfirmedAt: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("payment_triggered_at")
	val paymentTriggeredAt: Any,

	@field:SerializedName("status")
	val status: String
)
