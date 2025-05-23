package com.android.sigemesapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Payment(

    @field:SerializedName("amount")
    val amount: Int,

    @field:SerializedName("payment_gateway_token")
    val paymentGatewayToken: String,

    @field:SerializedName("method")
    val method: String,

    @field:SerializedName("rent_id")
    val rentId: Int,

    @field:SerializedName("payment_confirmed_at")
    val paymentConfirmedAt: String,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("payment_triggered_at")
    val paymentTriggeredAt: String?,

    @field:SerializedName("status")
    val status: String
): Parcelable
