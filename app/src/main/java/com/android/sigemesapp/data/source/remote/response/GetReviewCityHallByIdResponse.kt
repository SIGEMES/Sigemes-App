package com.android.sigemesapp.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class GetReviewCityHallByIdResponse(

    @field:SerializedName("data")
    val data: CityHallReviews?,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("status")
    val status: Boolean
)


