package com.android.sigemesapp.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class AllGuesthouseResponse(

	@field:SerializedName("data")
	val data: List<AllDataItem>,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)

data class AllDataItem(

	@field:SerializedName("address")
	val address: String,

	@field:SerializedName("contact_person")
	val contactPerson: String,

	@field:SerializedName("area_m2")
	val areaM2: Float,

	@field:SerializedName("latitude")
	val latitude: Any,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("facilities")
	val facilities: String,

	@field:SerializedName("longitude")
	val longitude: Any,

	@field:SerializedName("guesthouse_media")
	val guesthouseMedia: List<AllGuesthouseMediaItem>
)

data class AllGuesthouseMediaItem(

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("url")
	val url: String
)
