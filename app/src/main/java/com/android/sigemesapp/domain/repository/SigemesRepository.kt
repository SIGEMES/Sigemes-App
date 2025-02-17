package com.android.sigemesapp.domain.repository

import android.util.Log
import com.android.sigemesapp.data.source.local.UserPreference
import com.android.sigemesapp.data.source.remote.response.DetailRoom
import com.android.sigemesapp.data.source.remote.response.DetailRoomResponse
import com.android.sigemesapp.data.source.remote.response.GuesthouseData
import com.android.sigemesapp.data.source.remote.response.GuesthouseResponse
import com.android.sigemesapp.data.source.remote.response.RoomItem
import com.android.sigemesapp.data.source.remote.retrofit.ApiService
import com.android.sigemesapp.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    fun getGuesthouseRooms(id : Int): Flow<Result<List<RoomItem>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getGuesthouseRooms(id)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun getDetailGuesthouseRoom(guesthouseId: Int, roomId: Int): Flow<Result<DetailRoom>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getDetailGuesthouseRoom(guesthouseId, roomId)
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
}