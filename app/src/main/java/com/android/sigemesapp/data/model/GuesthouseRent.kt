package com.android.sigemesapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class GuesthouseRent(

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

