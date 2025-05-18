package com.android.sigemesapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class CityHall(

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
    val media: List<CityHallMedia>,

    @field:SerializedName("people_capacity")
    val peopleCapacity: Int,

    @field:SerializedName("longitude")
    val longitude: Double,

    @field:SerializedName("pricing")
    val pricing: List<CityHallPricings>,

    @field:SerializedName("status")
    val status: String
): Parcelable

@Parcelize
data class CityHallMedia(
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("url")
    val url: String
): Parcelable


@Parcelize
data class CityHalls(

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
    val media: List<CityHallMedia>,

    @field:SerializedName("people_capacity")
    val peopleCapacity: Int,

    @field:SerializedName("longitude")
    val longitude: Double,

    @field:SerializedName("status")
    val status: String
): Parcelable