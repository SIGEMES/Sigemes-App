package com.android.sigemesapp.repository

import com.android.sigemesapp.data.local.UserPreference
import com.android.sigemesapp.data.local.UserSession
import com.android.sigemesapp.data.model.RenterData
import com.android.sigemesapp.data.remote.ChangePasswordRequest
import com.android.sigemesapp.data.remote.ErrorResponse
import com.android.sigemesapp.data.remote.LoginRequest
import com.android.sigemesapp.data.remote.RegisterRequest
import com.android.sigemesapp.data.remote.SendOtpRequest
import com.android.sigemesapp.data.remote.VerifyOtpRequest
import com.android.sigemesapp.data.remote.response.LoginResponse
import com.android.sigemesapp.data.remote.response.MessageResponse
import com.android.sigemesapp.data.remote.response.RenterDataResponse
import com.android.sigemesapp.data.remote.retrofit.ApiService
import com.android.sigemesapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class AuthRepository @Inject constructor (
    private val userPreference : UserPreference,
    private val apiService: ApiService
){

    fun register(
        email: String,
        password: String,
        fullname: String,
        phoneNumber: String,
        gender: String
    ): Flow<Result<RenterDataResponse>> = flow {
        emit(Result.Loading)
        try {
            val request = RegisterRequest(
                email = email,
                password = password,
                fullname = fullname,
                phone_number = phoneNumber,
                gender = gender
            )
            val response = apiService.register(request)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }.flowOn(Dispatchers.IO)

    fun login(
        email: String,
        password: String
    ): Flow<Result<LoginResponse>> = flow {
        emit(Result.Loading)
        try {
            val request = LoginRequest(email = email, password = password)
            val response = apiService.login(request)

            if (response.status) {
                val user = UserSession(
                    email = response.data.email,
                    fullname = response.data.fullname,
                    token = response.data.token,
                    gender = response.data.gender,
                    profile_picture = response.data.profilePicture,
                    phone_number = response.data.phoneNumber,
                    id = response.data.id
                )
                userPreference.saveSession(user)
                emit(Result.Success(response))
            } else {
                emit(Result.Error(response.message))
            }
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                val gson = com.google.gson.Gson()
                val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                errorResponse.message
            } catch (ex: Exception) {
                "Login error"
            }
            emit(Result.Error(errorMessage))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }.flowOn(Dispatchers.IO)


    suspend fun logout() {
        userPreference.logout()
    }

    fun getSession(): Flow<UserSession> {
        return userPreference.getSession()
    }

    fun sendOtpEmailVerification(email: String): Flow<Result<MessageResponse>> = flow {
        emit(Result.Loading)
        try {
            val request = SendOtpRequest(email = email)
            val response = apiService.sendOtpEmailVerification(request)

            if (response.status) {
                emit(Result.Success(response))
            } else {
                emit(Result.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }.flowOn(Dispatchers.IO)

    fun verifyOtpEmail(email: String, otp: String): Flow<Result<MessageResponse>> = flow {
        emit(Result.Loading)
        try{
            val request = VerifyOtpRequest(email = email, otp = otp)
            val response = apiService.verifyOtpEmailVerification(request)

            if(response.status) {
                emit(Result.Success(response))
            } else {
                emit(Result.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun sendOtpResetPass(email: String): Flow<Result<MessageResponse>> = flow {
        emit(Result.Loading)
        try{
            val request = SendOtpRequest(email = email)
            val response = apiService.sendOtpResetPass(request)
            if(response.status){
                emit(Result.Success(response))
            } else {
                emit(Result.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun verifyOtpResetPass(email: String, otp: String): Flow<Result<MessageResponse>> = flow {
        emit(Result.Loading)
        try{
            val request = VerifyOtpRequest(email = email, otp = otp)
            val response = apiService.verifyOtpResetPass(request)
            if (response.status){
                emit(Result.Success(response))
            } else {
                emit(Result.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun changePassword(email: String, newPassword: String): Flow<Result<MessageResponse>> = flow {
        emit(Result.Loading)
        try{
            val request = ChangePasswordRequest(email = email, new_password = newPassword)
            val response = apiService.changePassword(request)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun getRenterData(id: Int): Flow<Result<RenterData>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getRenterData(id)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun updateRenterData(
        id: Int,
        fullname: String,
        phoneNumber: String,
        gender: String,
        profilePicture: File?
    ): Flow<Result<RenterDataResponse>> = flow {
        emit(Result.Loading)
        try {
            val requestImageFile = profilePicture?.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = requestImageFile?.let {
                MultipartBody.Part.createFormData(
                    "profile_picture",
                    profilePicture.name,
                    it
                )
            }
            val fullnameBody = fullname.toRequestBody("text/plain".toMediaType())
            val phoneNumberBody = phoneNumber.toRequestBody("text/plain".toMediaType())
            val genderBody = gender.toRequestBody("text/plain".toMediaType())
            val response = apiService.updateRenterData(id, fullnameBody, phoneNumberBody, genderBody, multipartBody)

            if (response.status) {
                val user = UserSession(
                    email = response.data.email,
                    fullname = response.data.fullname,
                    token = userPreference.getToken().first(),
                    gender = response.data.gender,
                    profile_picture = response.data.profilePicture,
                    phone_number = response.data.phoneNumber,
                    id = response.data.id
                )
                userPreference.saveSession(user)
                emit(Result.Success(response))
            } else {
                emit(Result.Error("Login failed: ${response.message}"))
            }

            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }.flowOn(Dispatchers.IO)
}