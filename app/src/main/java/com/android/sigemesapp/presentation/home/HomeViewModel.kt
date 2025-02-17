package com.android.sigemesapp.presentation.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.sigemesapp.data.source.remote.response.GuesthouseResponse
import com.android.sigemesapp.data.source.remote.response.RoomItem
import com.android.sigemesapp.domain.repository.SigemesRepository
import com.android.sigemesapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sigemesRepository: SigemesRepository
) : ViewModel() {

    private val _allGuesthousesResult = MutableLiveData<Result<List<GuesthouseResponse>>>()
    val allGuesthousesResult: LiveData<Result<List<GuesthouseResponse>>> get() = _allGuesthousesResult

    private val _allRooms = MutableLiveData<Result<List<RoomItem>>>()
    val allRooms: LiveData<Result<List<RoomItem>>> get() = _allRooms

    fun getGuesthouses(){
        viewModelScope.launch {
            sigemesRepository.getAllGuesthousesWithDetails()
                .collect{ result ->
                    _allGuesthousesResult.value = result

                }
        }
    }

    fun getRooms(id: Int){
        viewModelScope.launch {
            sigemesRepository.getGuesthouseRooms(id)
                .collect{ result ->
                    _allRooms.value = result
                }
        }
    }


}