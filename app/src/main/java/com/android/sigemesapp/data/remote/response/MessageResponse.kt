package com.android.sigemesapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class MessageResponse(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("status")
    val status: Boolean
)
