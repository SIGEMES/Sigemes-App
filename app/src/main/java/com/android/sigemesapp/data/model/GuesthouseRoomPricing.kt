package com.android.sigemesapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


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
