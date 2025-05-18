package com.android.sigemesapp.repository

import android.util.Log
import com.android.sigemesapp.data.model.CityHall
import com.android.sigemesapp.data.model.CityHallRent
import com.android.sigemesapp.data.model.CityHallReviews
import com.android.sigemesapp.data.model.Guesthouse
import com.android.sigemesapp.data.model.GuesthouseRent
import com.android.sigemesapp.data.model.GuesthouseRoomReviews
import com.android.sigemesapp.data.model.Rents
import com.android.sigemesapp.data.model.RoomItem
import com.android.sigemesapp.data.remote.CreateCityHallRentRequest
import com.android.sigemesapp.data.remote.CreateGuesthouseRentRequest
import com.android.sigemesapp.data.remote.DeletedMediaObject
import com.android.sigemesapp.data.remote.response.CityHallRentResponse
import com.android.sigemesapp.data.remote.response.DetailCityHallReviewResponse
import com.android.sigemesapp.data.remote.response.DetailGuesthouseRoomReviewResponse
import com.android.sigemesapp.data.remote.response.GuesthouseRentResponse
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

    fun createGuesthouseRent(
        guesthouseRoomPricingId: Int,
        slot: Int,
        startDate: String,
        endDate: String,
        renterGender: String
    ): Flow<Result<GuesthouseRentResponse>> = flow {
        emit(Result.Loading)
        try {
            val request = CreateGuesthouseRentRequest(
                guesthouse_room_pricing_id = guesthouseRoomPricingId,
                city_hall_pricing_id = null,
                slot = slot,
                start_date = startDate,
                end_date = endDate,
                renter_gender = renterGender
            )
            val response = apiService.createGuesthouseRent(request)
            emit(Result.Success(response))

        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }.flowOn(Dispatchers.IO)

    fun createCityHallRent(
        cityHallPricingId: Int,
        slot: Int,
        startDate: String,
        endDate: String,
        renterGender: String
    ): Flow<Result<CityHallRentResponse>> = flow {
        emit(Result.Loading)
        try {
            val request = CreateCityHallRentRequest(
                guesthouseRoomPricingId = null,
                cityHallPricingId = cityHallPricingId,
                slot = slot,
                startDate = startDate,
                endDate = endDate,
                renterGender = renterGender
            )
            Log.e("CreateCityHallRequest","$request")
            val response = apiService.createCityHallRent(request)

            emit(Result.Success(response))

        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }.flowOn(Dispatchers.IO)

    fun cancelCityHallRent(id: Int): Flow<Result<CityHallRent>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.cancelCityHallRent(id)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun cancelGuesthouseRent(id: Int): Flow<Result<GuesthouseRent>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.cancelGuesthouseRent(id)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun getAllRents(): Flow<Result<List<Rents>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getAllRents()
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun getDetailGuesthouseRent(id: Int): Flow<Result<GuesthouseRent>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getDetailGuesthouseRent(id)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun getDetailCityHallRent(id: Int): Flow<Result<CityHallRent>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getDetailCityHallRent(id)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun getRoomsReview(guesthouseId: Int, roomId: Int): Flow<Result<List<GuesthouseRoomReviews>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getRoomReviews(guesthouseId, roomId)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun getCityHallReview(id: Int): Flow<Result<List<CityHallReviews>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getCityHallReviews(id)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun addRoomReview(
        rentId: Int,
        rating: Int,
        comment: String,
        media: List<File?> = emptyList()
    ): Flow<Result<GuesthouseRoomReviews>> = flow {
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
                apiService.addRoomReview(rentId, ratingBody, commentBody, emptyList()).data
            } else {
                apiService.addRoomReview(rentId, ratingBody, commentBody, multipartMedia).data
            }
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun addCityHallReview(
        rentId: Int,
        rating: Int,
        comment: String,
        media: List<File?> = emptyList()
    ): Flow<Result<CityHallReviews>> = flow {
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
                apiService.addCityHallReview(rentId, ratingBody, commentBody, emptyList()).data
            } else {
                apiService.addCityHallReview(rentId, ratingBody, commentBody, multipartMedia).data
            }
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun getCityHallReviewById(rentId: Int): Flow<Result<DetailCityHallReviewResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getCityHallReviewById(rentId)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun getRoomReviewById(rentId: Int): Flow<Result<DetailGuesthouseRoomReviewResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getRoomReviewById(rentId)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun updateRoomReview(
        rentId: Int,
        reviewId: Int,
        rating: Int,
        comment: String,
        media: List<File?> = emptyList(),
        deletedMediaObjects: List<DeletedMediaObject> = emptyList()
    ): Flow<Result<GuesthouseRoomReviews>> = flow {
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

            val response = apiService.updateRoomReview(
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

    fun updateCityHallReview(
        rentId: Int,
        reviewId: Int,
        rating: Int,
        comment: String,
        media: List<File?> = emptyList(),
        deletedMediaObjects: List<DeletedMediaObject> = emptyList()
    ): Flow<Result<CityHallReviews>> = flow {
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

            Log.e("CheckAddedReview", "$rentId, $ratingBody, $commentBody, $multipartMedia")
            val response = apiService.updateReviewCityHall(
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