package com.android.sigemesapp.data.remote.response

import com.android.sigemesapp.data.model.Review
import com.google.gson.annotations.SerializedName

data class ReviewResponse(
    @field:SerializedName("data")
    val data: Review?,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("status")
    val status: Boolean
)