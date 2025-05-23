package com.android.sigemesapp.repository

import android.util.Log
import com.android.sigemesapp.data.model.CityHall
import com.android.sigemesapp.data.model.Guesthouse
import com.android.sigemesapp.data.model.Rent
import com.android.sigemesapp.data.model.Review
import com.android.sigemesapp.data.model.RoomItem
import com.android.sigemesapp.data.remote.CreateRentRequest
import com.android.sigemesapp.data.remote.DeletedMediaObject
import com.android.sigemesapp.data.remote.ErrorResponse
import com.android.sigemesapp.data.remote.response.RentResponse
import com.android.sigemesapp.data.remote.response.ReviewResponse
import com.android.sigemesapp.data.remote.retrofit.ApiService
import com.android.sigemesapp.utils.Result
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class SigemesRepository @Inject constructor (
    private val apiService: ApiService
){

    fun getGuesthouse(id : Int): Flow<Result<Guesthouse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getGuesthouse(id)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun getGuesthouseRooms(
        id : Int,
        startDate: String,
        endDate: String,
        renterGender: String): Flow<Result<List<RoomItem>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getGuesthouseRooms(id, startDate, endDate, renterGender)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun getDetailRooms(
        guesthouseId: Int,
        roomId: Int,
        startDate: String,
        endDate: String,
        renterGender: String): Flow<Result<RoomItem>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getDetailRoom(guesthouseId, roomId, startDate, endDate, renterGender)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun getCityHall(id: Int, startDate: String, endDate: String): Flow<Result<CityHall>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getCityHall(id, startDate, endDate)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun createRent(
        pricingId: Int,
        slot: Int,
        startDate: String,
        endDate: String,
        renterGender: String,
        rentType: String
    ): Flow<Result<RentResponse>> = flow {
        emit(Result.Loading)
        try {
            val request = if (rentType == "Gedung") {
                CreateRentRequest(
                    guesthouseRoomPricingId = null,
                    cityHallPricingId = pricingId,
                    slot = slot,
                    startDate = startDate,
                    endDate = endDate,
                    renterGender = renterGender
                )
            } else {
                CreateRentRequest(
                    guesthouseRoomPricingId = pricingId,
                    cityHallPricingId = null,
                    slot = slot,
                    startDate = startDate,
                    endDate = endDate,
                    renterGender = renterGender
                )
            }

            val response = apiService.createRent(request)
            emit(Result.Success(response))

        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }.flowOn(Dispatchers.IO)

    fun cancelRent(id: Int): Flow<Result<Rent>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.cancelRent(id)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun getAllRents(): Flow<Result<List<Rent>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getAllRents()
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun getRent(id: Int): Flow<Result<Rent>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getRent(id)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun getRoomsReviews(guesthouseId: Int, roomId: Int): Flow<Result<List<Review>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getRoomReviewss(guesthouseId, roomId)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun getCityHallReviews(id: Int): Flow<Result<List<Review>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getCityHallReviewss(id)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun addReview(
        rentId: Int,
        rating: Int,
        comment: String,
        media: List<File?> = emptyList()
    ): Flow<Result<Review?>> = flow {
        emit(Result.Loading)
        try {
            val multipartMedia = media.filterNotNull().map { file ->
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
                MultipartBody.Part.createFormData(
                    "media",
                    file.name,
                    requestImageFile
                )
            }
            val ratingBody = rating.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val commentBody = comment.toRequestBody("text/plain".toMediaTypeOrNull())

            Log.e("CheckAddedReview", "$rentId, $ratingBody, $commentBody, $multipartMedia")
            val response = if (multipartMedia.isEmpty()) {
                apiService.addReview(rentId, ratingBody, commentBody, emptyList()).data
            } else {
                apiService.addReview(rentId, ratingBody, commentBody, multipartMedia).data
            }
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun getReviewById(rentId: Int): Flow<Result<ReviewResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getReviewById(rentId)
            if (response.status) {
                emit(Result.Success(response))
            } else {
                emit(Result.Error(response.message))
            }
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                val gson = Gson()
                val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                errorResponse.message
            } catch (ex: Exception) {
                "Unexpected error"
            }
            emit(Result.Error(errorMessage))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }.flowOn(Dispatchers.IO)


    fun updateReview(
        rentId: Int,
        reviewId: Int,
        rating: Int,
        comment: String,
        media: List<File?> = emptyList(),
        deletedMediaObjects: List<DeletedMediaObject> = emptyList()
    ): Flow<Result<Review?>> = flow {
        emit(Result.Loading)
        try {
            val multipartMedia = media.filterNotNull().map { file ->
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
                MultipartBody.Part.createFormData(
                    "media",
                    file.name,
                    requestImageFile
                )
            }

            val deletedMediaObjectJson = if (deletedMediaObjects.isNotEmpty()) {
                val gson = Gson()
                gson.toJson(deletedMediaObjects)
            } else {
                null
            }

            val ratingBody = rating.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val commentBody = comment.toRequestBody("text/plain".toMediaTypeOrNull())
            val deletedMediaObjectBody = deletedMediaObjectJson?.toRequestBody("application/json".toMediaTypeOrNull())

            val response = apiService.updateReview(
                rentId,
                reviewId,
                ratingBody,
                commentBody,
                multipartMedia,
                deletedMediaObjectBody
            ).data

            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

}