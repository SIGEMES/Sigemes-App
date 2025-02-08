package com.android.sigemesapp.domain.repository

import com.android.sigemesapp.data.source.local.UserModel
import com.android.sigemesapp.data.source.local.UserPreference
import com.android.sigemesapp.data.source.remote.LoginRequest
import com.android.sigemesapp.data.source.remote.RegisterRequest
import com.android.sigemesapp.data.source.remote.response.RegisterResponse
import com.android.sigemesapp.data.source.remote.response.LoginResponse
import com.android.sigemesapp.data.source.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.android.sigemesapp.utils.Result
import kotlinx.coroutines.flow.flowOn
import android.util.Log
import com.android.sigemesapp.data.source.remote.ChangePasswordRequest
import com.android.sigemesapp.data.source.remote.SendOtpRequest
import com.android.sigemesapp.data.source.remote.VerifyOtpRequest
import com.android.sigemesapp.data.source.remote.response.ChangePasswordResponse
import com.android.sigemesapp.data.source.remote.response.SendOtpResponse
import com.android.sigemesapp.data.source.remote.response.VerifyEmailResponse

class AuthRepository @Inject constructor (
    private val userPreference : UserPreference,
    private val apiService: ApiService
){

    fun register(
        email: String,
        password: String,
        fullname: String,
        phone_number: String,
        gender: String
    ): Flow<Result<RegisterResponse>> = flow {
        emit(Result.Loading)
        try {
            val request = RegisterRequest(
                email = email,
                password = password,
                fullname = fullname,
                phone_number = phone_number,
                gender = gender
            )
            val response = apiService.register(request)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
            Log.d("AuthRepository", "Register Error: ${e.message}")
        }
    }.flowOn(Dispatchers.IO)

    fun login(
        email: String,
        password: String
    ): Flow<Result<LoginResponse>> = flow {
        emit(Result.Loading)
        try {
            val request = LoginRequest(
                email = email,
                password = password
            )
            val response = apiService.login(request)

            if (response.status) {
                val user = UserModel(
                    email = response.data.email,
                    fullname = response.data.fullname,
                    token = response.data.token
                )
                userPreference.saveSession(user)
                emit(Result.Success(response))
            } else {
                emit(Result.Error("Login failed: ${response.message}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
            Log.d("AuthRepository", "Login Error: ${e.message}")
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    fun sendOtpEmailVerification(email: String): Flow<Result<SendOtpResponse>> = flow {
        emit(Result.Loading)
        try {
            val request = SendOtpRequest(email = email)
            val response = apiService.sendEmailVerificationOtp(request)

            if (response.status) {
                emit(Result.Success(response))
            } else {
                emit(Result.Error(response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
            Log.d("SendEmailVerification", "Error: ${e.message}")
        }
    }.flowOn(Dispatchers.IO)

    fun verifyOtpEmail(email: String, otp: String): Flow<Result<VerifyEmailResponse>> = flow {
        emit(Result.Loading)
        try{
            val request = VerifyOtpRequest(email = email, otp = otp)
            val response = apiService.verifyEmailOtp(request)

            if(response.status) {
                emit(Result.Success(response))
            } else {
                emit(Result.Error(response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
            Log.e("VerifyEmailOtp", "Error: ${e.message}", e)
        }
    }

    fun sendOtpForgotPassword(email: String): Flow<Result<SendOtpResponse>> = flow {
        emit(Result.Loading)
        try{
            val request = SendOtpRequest(email = email)
            val response = apiService.sendOtpForgotPassword(request)
            if(response.status){
                emit(Result.Success(response))
            } else {
                emit(Result.Error(response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
            Log.e("sendForgotPasswordOtpData", "Error: ${e.message}", e)
        }
    }

    fun verifyOtpForgotPassword(email: String, otp: String): Flow<Result<VerifyEmailResponse>> = flow {
        emit(Result.Loading)
        try{
            val request = VerifyOtpRequest(email = email, otp = otp)
            val response = apiService.verifyOtpForgotPassword(request)
            if (response.status){
                emit(Result.Success(response))
            } else {
                emit(Result.Error(response.message ?: "Password Changed Sucessfully"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
            Log.e("VerifyForgotPasswordOtpData", "Error: ${e.message}", e)
        }
    }

    fun changePassword(email: String, new_password: String): Flow<Result<ChangePasswordResponse>> = flow {
        emit(Result.Loading)
        try{
            val request = ChangePasswordRequest(email = email, new_password = new_password)
            val response = apiService.changePassword(request)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
            Log.d("ChangeForgotPasswordData", "Error: ${e.message}")
        }
    }
}