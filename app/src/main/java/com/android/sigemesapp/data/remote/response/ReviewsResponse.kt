package com.android.sigemesapp.data.remote.response

import com.android.sigemesapp.data.model.Review
import com.google.gson.annotations.SerializedName

data class ReviewsResponse(

    @field:SerializedName("data")
    val data: List<Review>,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("status")
    val status: Boolean
)