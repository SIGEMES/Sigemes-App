package com.android.sigemesapp.data.source.remote.retrofit

import com.android.sigemesapp.data.source.remote.ChangePasswordRequest
import com.android.sigemesapp.data.source.remote.CreateCityHallRentRequest
import com.android.sigemesapp.data.source.remote.CreateGuesthouseRentRequest
import com.android.sigemesapp.data.source.remote.LoginRequest
import com.android.sigemesapp.data.source.remote.RegisterRequest
import com.android.sigemesapp.data.source.remote.SendOtpRequest
import com.android.sigemesapp.data.source.remote.VerifyOtpRequest
import com.android.sigemesapp.data.source.remote.response.AllGuesthouseResponse
import com.android.sigemesapp.data.source.remote.response.CancelCityHallResponse
import com.android.sigemesapp.data.source.remote.response.ChangePasswordResponse
import com.android.sigemesapp.data.source.remote.response.CityHallResponse
import com.android.sigemesapp.data.source.remote.response.CreateCityHallRentResponse
import com.android.sigemesapp.data.source.remote.response.CreateGuesthouseRentResponse
import com.android.sigemesapp.data.source.remote.response.DetailRoomResponse
import com.android.sigemesapp.data.source.remote.response.GetAllRentsResponse
import com.android.sigemesapp.data.source.remote.response.GuesthouseResponse
import com.android.sigemesapp.data.source.remote.response.GuesthouseRoomsResponse
import com.android.sigemesapp.data.source.remote.response.LoginResponse
import com.android.sigemesapp.data.source.remote.response.RegisterResponse
import com.android.sigemesapp.data.source.remote.response.RenterDataResponse
import com.android.sigemesapp.data.source.remote.response.SendOtpResponse
import com.android.sigemesapp.data.source.remote.response.UpdateProfileResponse
import com.android.sigemesapp.data.source.remote.response.VerifyEmailResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("renters/{id}")
    suspend fun getRenterData(
        @Path("id") id : Int
    ) : RenterDataResponse

    @Multipart
    @PUT("renters/{id}")
    suspend fun updateRenterData(
        @Path("id") id : Int,
        @Part("fullname") fullname: RequestBody,
        @Part("phone_number") phone_number: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part profile_picture: MultipartBody.Part?
    ) : UpdateProfileResponse

    @GET("guesthouses")
    suspend fun getAllGuesthouses() : AllGuesthouseResponse

    @GET("guesthouses/{id}")
    suspend fun getGuesthouse(
        @Path("id") id: Int
    ) : GuesthouseResponse

    @GET("guesthouses/{guesthouse-id}/rooms")
    suspend fun getGuesthouseRooms(
        @Path("guesthouse-id") id: Int,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("renter_gender") renterGender: String
    ): GuesthouseRoomsResponse

    @GET("guesthouses/{guesthouse_id}/rooms/{room_id}")
    suspend fun getDetailGuesthouseRoom(
        @Path("guesthouse_id") guesthouse_id: Int,
        @Path("room_id") room_id: Int,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("renter_gender") renterGender: String
    ) : DetailRoomResponse

    @GET("city-halls/{id}")
    suspend fun getCityHall(
        @Path("id") id: Int,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
    ) : CityHallResponse

    @POST("rents")
    suspend fun createGuesthouseRent(
        @Body createGuesthouseRentRequest: CreateGuesthouseRentRequest
    ): CreateGuesthouseRentResponse

    @POST("rents")
    suspend fun createCityHallRent(
        @Body createCityHallRentRequest: CreateCityHallRentRequest
    ): CreateCityHallRentResponse

    @PUT("rents/{id}")
    suspend fun cancelRent(
        @Path("id") id :Int
    ): CancelCityHallResponse

    @GET("rents")
    suspend fun getAllRents(): GetAllRentsResponse

}