package com.android.sigemesapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

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
    val media: List<RoomMediaItem>,

    @field:SerializedName("type")
    val type: String,

    @field:SerializedName("facilities")
    val facilities: String,

    @field:SerializedName("total_slot")
    val totalSlot: Int,

    @field:SerializedName("pricing")
    val pricing: List<PricingItem>
)

@Parcelize
data class RoomMediaItem(

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("url")
    val url: String
): Parcelable

@Parcelize
data class GuesthouseRoom(

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
    val media: List<RoomMediaItem>,

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