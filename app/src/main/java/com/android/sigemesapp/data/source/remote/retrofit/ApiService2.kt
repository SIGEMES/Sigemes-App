package com.android.sigemesapp.data.source.remote.retrofit

import com.android.sigemesapp.data.source.remote.response.TransactionStatusResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ApiService2 {
    @GET("v2/{transaction_id}/status")
    suspend fun getStatus(
        @Path("transactionId") transactionId: String,
        @Header("Authorization") authHeader: String
    ): TransactionStatusResponse
}