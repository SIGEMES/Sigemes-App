package com.android.sigemesapp.data.source.remote.retrofit

import com.android.sigemesapp.data.source.remote.ChangePasswordRequest
import com.android.sigemesapp.data.source.remote.LoginRequest
import com.android.sigemesapp.data.source.remote.RegisterRequest
import com.android.sigemesapp.data.source.remote.SendOtpRequest
import com.android.sigemesapp.data.source.remote.VerifyOtpRequest
import com.android.sigemesapp.data.source.remote.response.AllGuesthouseResponse
import com.android.sigemesapp.data.source.remote.response.ChangePasswordResponse
import com.android.sigemesapp.data.source.remote.response.CityHallResponse
import com.android.sigemesapp.data.source.remote.response.DetailRoomResponse
import com.android.sigemesapp.data.source.remote.response.GuesthouseResponse
import com.android.sigemesapp.data.source.remote.response.GuesthouseRoomsResponse
import com.android.sigemesapp.data.source.remote.response.LoginResponse
import com.android.sigemesapp.data.source.remote.response.RegisterResponse
import com.android.sigemesapp.data.source.remote.response.SendOtpResponse
import com.android.sigemesapp.data.source.remote.response.VerifyEmailResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("renters/register")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): RegisterResponse

    @POST("renters/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginResponse

    @POST("renters/email/send-otp")
    suspend fun sendEmailVerificationOtp(
        @Body sendOtpRequest: SendOtpRequest
    ): SendOtpResponse

    @POST("renters/email/verify-otp")
    suspend fun verifyEmailOtp(
        @Body verifyEmail: VerifyOtpRequest
    ): VerifyEmailResponse

    @POST("renters/forgot-password/send-otp")
    suspend fun sendOtpForgotPassword(
        @Body sendOtpRequest: SendOtpRequest
    ): SendOtpResponse

    @POST("renters/forgot-password/verify-otp")
    suspend fun verifyOtpForgotPassword(
        @Body verifyOtpRequest: VerifyOtpRequest
    ): VerifyEmailResponse

    @PUT("renters/forgot-password/change-password")
    suspend fun changePassword(
        @Body changePassword: ChangePasswordRequest
    ): ChangePasswordResponse

    @GET("guesthouses")
    suspend fun getAllGuesthouses() : AllGuesthouseResponse

    @GET("guesthouses/{id}")
    suspend fun getGuesthouse(
        @Path("id") id: Int
    ) : GuesthouseResponse

    @GET("guesthouses/{guesthouse-id}/rooms")
    suspend fun getGuesthouseRooms(
        @Path("guesthouse-id") id: Int
    ) : GuesthouseRoomsResponse

    @GET("guesthouses/{guesthouse_id}/rooms/{room_id}")
    suspend fun getDetailGuesthouseRoom(
        @Path("guesthouse_id") guesthouse_id: Int,
        @Path("room_id") room_id: Int
    ) : DetailRoomResponse

    @GET(" city-halls/{id}")
    suspend fun getCityHall(
        @Path("id") id: Int
    ) : CityHallResponse

}