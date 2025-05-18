package com.android.sigemesapp.data.remote.retrofit

import com.android.sigemesapp.data.remote.ChangePasswordRequest
import com.android.sigemesapp.data.remote.CreateCityHallRentRequest
import com.android.sigemesapp.data.remote.CreateGuesthouseRentRequest
import com.android.sigemesapp.data.remote.LoginRequest
import com.android.sigemesapp.data.remote.RegisterRequest
import com.android.sigemesapp.data.remote.SendOtpRequest
import com.android.sigemesapp.data.remote.VerifyOtpRequest
import com.android.sigemesapp.data.remote.response.AddedCityHallResponse
import com.android.sigemesapp.data.remote.response.AddedGuesthouseRoomResponse
import com.android.sigemesapp.data.remote.response.CancelCityHallRentResponse
import com.android.sigemesapp.data.remote.response.CancelGuesthouseRentResponse
import com.android.sigemesapp.data.remote.response.CityHallRentResponse
import com.android.sigemesapp.data.remote.response.CityHallResponse
import com.android.sigemesapp.data.remote.response.CityHallReviewsResponse
import com.android.sigemesapp.data.remote.response.DetailCityHallReviewResponse
import com.android.sigemesapp.data.remote.response.DetailGuesthouseRoomReviewResponse
import com.android.sigemesapp.data.remote.response.DetailRoomResponse
import com.android.sigemesapp.data.remote.response.GuesthouseRentResponse
import com.android.sigemesapp.data.remote.response.GuesthouseResponse
import com.android.sigemesapp.data.remote.response.GuesthouseRoomReviewsResponse
import com.android.sigemesapp.data.remote.response.GuesthouseRoomsResponse
import com.android.sigemesapp.data.remote.response.LoginResponse
import com.android.sigemesapp.data.remote.response.MessageResponse
import com.android.sigemesapp.data.remote.response.RenterDataResponse
import com.android.sigemesapp.data.remote.response.RentsResponse
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
    ): RenterDataResponse

    @POST("renters/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginResponse

    @POST("renters/email/send-otp")
    suspend fun sendOtpEmailVerification(
        @Body sendOtpRequest: SendOtpRequest
    ): MessageResponse

    @POST("renters/email/verify-otp")
    suspend fun verifyOtpEmailVerification(
        @Body verifyEmail: VerifyOtpRequest
    ): MessageResponse

    @POST("renters/forgot-password/send-otp")
    suspend fun sendOtpResetPass(
        @Body sendOtpRequest: SendOtpRequest
    ): MessageResponse

    @POST("renters/forgot-password/verify-otp")
    suspend fun verifyOtpResetPass(
        @Body verifyOtpRequest: VerifyOtpRequest
    ): MessageResponse

    @PUT("renters/forgot-password/change-password")
    suspend fun changePassword(
        @Body changePassword: ChangePasswordRequest
    ): MessageResponse

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
    ) : RenterDataResponse

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
    suspend fun getDetailRoom(
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
    ): GuesthouseRentResponse

    @POST("rents")
    suspend fun createCityHallRent(
        @Body createCityHallRentRequest: CreateCityHallRentRequest
    ): CityHallRentResponse

    @PUT("rents/{id}")
    suspend fun cancelCityHallRent(
        @Path("id") id :Int
    ): CancelCityHallRentResponse

    @PUT("rents/{id}")
    suspend fun cancelGuesthouseRent(
        @Path("id") id :Int
    ): CancelGuesthouseRentResponse

    @GET("rents")
    suspend fun getAllRents(): RentsResponse

    @GET("rents/{id}")
    suspend fun getDetailGuesthouseRent(
        @Path("id") id : Int
    ): GuesthouseRentResponse

    @GET("rents/{id}")
    suspend fun getDetailCityHallRent(
        @Path("id") id : Int
    ): CityHallRentResponse

    @GET("guesthouses/{guesthouse_id}/rooms/{room_id}/reviews")
    suspend fun getRoomReviews(
        @Path("guesthouse_id") guesthouse_id: Int,
        @Path("room_id") room_id: Int,
    ): GuesthouseRoomReviewsResponse

    @GET("city-halls/{id}/reviews")
    suspend fun getCityHallReviews(
        @Path("id") id: Int
    ): CityHallReviewsResponse

    @Multipart
    @POST("rents/{rent_id}/reviews")
    suspend fun addRoomReview(
        @Path("rent_id") rent_id: Int,
        @Part("rating") rating: RequestBody,
        @Part("comment") comment: RequestBody,
        @Part media: List<MultipartBody.Part> = emptyList()
    ): AddedGuesthouseRoomResponse

    @Multipart
    @POST("rents/{rent_id}/reviews")
    suspend fun addCityHallReview(
        @Path("rent_id") rent_id: Int,
        @Part("rating") rating: RequestBody,
        @Part("comment") comment: RequestBody,
        @Part media: List<MultipartBody.Part>  = emptyList()
    ): AddedCityHallResponse

    @GET("rents/{rent_id}/reviews")
    suspend fun getRoomReviewById(
        @Path("rent_id") rent_id: Int
    ): DetailGuesthouseRoomReviewResponse

    @GET("rents/{rent_id}/reviews")
    suspend fun getCityHallReviewById(
        @Path("rent_id") rent_id: Int
    ): DetailCityHallReviewResponse

    @Multipart
    @PUT("rents/{rent_id}/reviews/{review-id}")
    suspend fun updateRoomReview(
        @Path("rent_id") rent_id: Int,
        @Path("review-id") review_id: Int,
        @Part("rating") rating: RequestBody,
        @Part("comment") comment: RequestBody,
        @Part media: List<MultipartBody.Part>  = emptyList(),
        @Part("deleted_media_object") deletedMediaObject: RequestBody? = null
    ): AddedGuesthouseRoomResponse

    @Multipart
    @PUT("rents/{rent_id}/reviews/{review-id}")
    suspend fun updateReviewCityHall(
        @Path("rent_id") rent_id: Int,
        @Path("review-id") review_id: Int,
        @Part("rating") rating: RequestBody,
        @Part("comment") comment: RequestBody,
        @Part media: List<MultipartBody.Part>  = emptyList(),
        @Part("deleted_media_object") deletedMediaObject: RequestBody? = null
    ): AddedCityHallResponse
}