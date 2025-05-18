package com.android.sigemesapp.data.remote.response

import com.android.sigemesapp.data.model.CityHallReviews
import com.google.gson.annotations.SerializedName

data class DetailCityHallReviewResponse(

    @field:SerializedName("data")
    val data: CityHallReviews?,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("status")
    val status: Boolean
)


