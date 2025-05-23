package com.android.sigemesapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CityHallPricings(

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
): Parcelable

@Parcelize
data class CityHallPricing(

    @field:SerializedName("is_active")
    val isActive: Boolean,

    @field:SerializedName("city_hall")
    val cityHall: CityHalls,

    @field:SerializedName("activity_type")
    val activityType: String,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("facilities")
    val facilities: String,

    @field:SerializedName("price_per_day")
    val pricePerDay: Int
): Parcelable