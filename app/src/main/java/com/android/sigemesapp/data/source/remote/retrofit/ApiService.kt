package com.android.sigemesapp.data.source.remote.retrofit

import com.android.sigemesapp.data.source.remote.ChangePasswordRequest
import com.android.sigemesapp.data.source.remote.LoginRequest
import com.android.sigemesapp.data.source.remote.RegisterRequest
import com.android.sigemesapp.data.source.remote.SendOtpRequest
import com.android.sigemesapp.data.source.remote.VerifyOtpRequest
import com.android.sigemesapp.data.source.remote.response.ChangePasswordResponse
import com.android.sigemesapp.data.source.remote.response.LoginResponse
import com.android.sigemesapp.data.source.remote.response.RegisterResponse
import com.android.sigemesapp.data.source.remote.response.SendOtpResponse
import com.android.sigemesapp.data.source.remote.response.VerifyEmailResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

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

}