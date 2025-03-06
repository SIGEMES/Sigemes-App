package com.android.sigemesapp.domain.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.android.sigemesapp.data.source.local.UserPreference
import com.android.sigemesapp.data.source.remote.CreateCityHallRentRequest
import com.android.sigemesapp.data.source.remote.CreateGuesthouseRentRequest
import com.android.sigemesapp.data.source.remote.response.CityHall
import com.android.sigemesapp.data.source.remote.response.CityHallData
import com.android.sigemesapp.data.source.remote.response.CityHallRent
import com.android.sigemesapp.data.source.remote.response.CityHallReviews
import com.android.sigemesapp.data.source.remote.response.CreateCityHallRentResponse
import com.android.sigemesapp.data.source.remote.response.CreateGuesthouseRentResponse
import com.android.sigemesapp.data.source.remote.response.DetailRoom
import com.android.sigemesapp.data.source.remote.response.GuesthouseData
import com.android.sigemesapp.data.source.remote.response.GuesthouseRentData
import com.android.sigemesapp.data.source.remote.response.GuesthouseResponse
import com.android.sigemesapp.data.source.remote.response.GuesthouseRoomReviews
import com.android.sigemesapp.data.source.remote.response.RentsDataItem
import com.android.sigemesapp.data.source.remote.response.RoomItem
import com.android.sigemesapp.data.source.remote.retrofit.ApiService
import com.android.sigemesapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SigemesRepository @Inject constructor (
    private val userPreference : UserPreference,
    private val apiService: ApiService
){
    fun getAllGuesthousesWithDetails(): Flow<Result<List<GuesthouseResponse>>> = flow {
        emit(Result.Loading)
        try {
            val allGuesthousesResponse = apiService.getAllGuesthouses()
            val guesthouseIds = allGuesthousesResponse.data.map { it.id }

            val guesthouses = mutableListOf<GuesthouseResponse>()

            for (id in guesthouseIds) {
                try {
                    val guesthouse = apiService.getGuesthouse(id)
                    guesthouses.add(guesthouse)
                } catch (e: Exception) {
                    Log.e("GetGuesthouseDetails", "Failed to fetch guesthouse with ID: $id, Error: ${e.message}")
                }
            }
            emit(Result.Success(guesthouses))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
            Log.e("GetGuesthousesData", "Error: ${e.message}")
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

    fun getDetailGuesthouseRoom(
        guesthouseId: Int,
        roomId: Int,
        startDate: String,
        endDate: String,
        renterGender: String): Flow<Result<DetailRoom>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getDetailGuesthouseRoom(guesthouseId, roomId, startDate, endDate, renterGender)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun getDetailGuesthouse(guesthouseId: Int): Flow<Result<GuesthouseData>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getGuesthouse(guesthouseId)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun getCityHall(id: Int, startDate: String, endDate: String): Flow<Result<CityHallData>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getCityHall(id, startDate, endDate)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun createGuesthouseRent(
        guesthouse_room_pricing_id: Int,
        slot: Int,
        start_date: String,
        end_date: String,
        renter_gender: String
    ): Flow<Result<CreateGuesthouseRentResponse>> = flow {
        emit(Result.Loading)
        try {
            val request = CreateGuesthouseRentRequest(
                guesthouse_room_pricing_id = guesthouse_room_pricing_id,
                city_hall_pricing_id = null,
                slot = slot,
                start_date = start_date,
                end_date = end_date,
                renter_gender = renter_gender
            )
            val response = apiService.createGuesthouseRent(request)
            emit(Result.Success(response))

        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }.flowOn(Dispatchers.IO)

    fun createCityHallRent(
        city_hall_pricing_id: Int,
        slot: Int,
        start_date: String,
        end_date: String,
        renter_gender: String
    ): Flow<Result<CreateCityHallRentResponse>> = flow {
        emit(Result.Loading)
        try {
            val request = CreateCityHallRentRequest(
                guesthouseRoomPricingId = null,
                cityHallPricingId = city_hall_pricing_id,
                slot = slot,
                startDate = start_date,
                endDate = end_date,
                renterGender = renter_gender
            )
            Log.e("CreateCityHallRequest","$request")
            val response = apiService.createCityHallRent(request)

            emit(Result.Success(response))

        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }.flowOn(Dispatchers.IO)

    fun cancelCityHall(id: Int): Flow<Result<CityHallRent>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.cancelRent(id)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun getAllRents(): Flow<Result<List<RentsDataItem>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getAllRents()
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

//    fun getHistory(): LiveData<Result<List<RentsDataItem>>> = liveData(Dispatchers.IO) {
//        emit(Result.Loading)
//        try{
//            val response = apiService.getAllRents().data
//            emit(Result.Success(response))
//        } catch (e: Exception) {
//            emit(Result.Error("ErrorDel: ${e.message}"))
//        }
//    }

    fun getGuesthouseDetailRent(id: Int): Flow<Result<GuesthouseRentData>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getDetailGuesthouseRent(id)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun getCityHallDetailRent(id: Int): Flow<Result<CityHallRent>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getDetailCityHallRent(id)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun getGuesthouseRoomsReview(guesthouseId: Int, roomId: Int): Flow<Result<List<GuesthouseRoomReviews>>> = flow {
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

}