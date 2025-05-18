package com.android.sigemesapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


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

@Parcelize
data class GuesthouseMediaItem(
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("url")
    val url: String
): Parcelable