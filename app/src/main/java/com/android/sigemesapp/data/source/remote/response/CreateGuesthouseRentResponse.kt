package com.android.sigemesapp.data.source.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class CreateGuesthouseRentResponse(

	@field:SerializedName("data")
	val data: GuesthouseRentData,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)

@Parcelize
data class GuesthouseRoomPricing(

	@field:SerializedName("is_active")
	val isActive: Boolean,

	@field:SerializedName("guesthouse_room")
	val guesthouseRoom: GuesthouseRoom,

	@field:SerializedName("retribution_type")
	val retributionType: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("price_per_day")
	val pricePerDay: Int
): Parcelable

@Parcelize
data class GuesthouseRentData(

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
	val payment: Payment,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("slot")
	val slot: Int,

	@field:SerializedName("guesthouse_room_pricing")
	val guesthouseRoomPricing: GuesthouseRoomPricing,

	@field:SerializedName("renter_gender")
	val renterGender: String,

	@field:SerializedName("renter")
	val renter: Renter,

	@field:SerializedName("start_date")
	val startDate: String
) : Parcelable

@Parcelize
data class Payment(

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
	val paymentTriggeredAt: String,

	@field:SerializedName("status")
	val status: String
): Parcelable

