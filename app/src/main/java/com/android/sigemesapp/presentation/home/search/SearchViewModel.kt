package com.android.sigemesapp.presentation.home.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.sigemesapp.data.source.remote.response.CityHallData
import com.android.sigemesapp.data.source.remote.response.GuesthouseResponse
import com.android.sigemesapp.data.source.remote.response.RoomItem
import com.android.sigemesapp.domain.repository.SigemesRepository
import com.android.sigemesapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val sigemesRepository: SigemesRepository
) : ViewModel() {

    private val _allRooms = MutableLiveData<Result<List<RoomItem>>>()
    val allRooms: LiveData<Result<List<RoomItem>>> get() = _allRooms

    private val _startDate = MutableLiveData<Long>()
    val startDate: LiveData<Long> = _startDate

    private val _endDate = MutableLiveData<Long>()
    val endDate: LiveData<Long> = _endDate

    private val _allGuesthousesResult = MutableLiveData<Result<List<GuesthouseResponse>>>()
    val allGuesthousesResult: LiveData<Result<List<GuesthouseResponse>>> get() = _allGuesthousesResult

    private val _cityHall = MutableLiveData<Result<CityHallData>>()
    val cityHall: LiveData<Result<CityHallData>> get() = _cityHall

    fun getGuesthouses(){
        viewModelScope.launch {
            sigemesRepository.getAllGuesthousesWithDetails()
                .collect{ result ->
                    _allGuesthousesResult.value = result
                }
        }
    }

    fun getRooms(id : Int, startDate: String, endDate: String, renterGender: String) {
        viewModelScope.launch {
            sigemesRepository.getGuesthouseRooms(id, startDate, endDate, renterGender)
                .collect { result ->
                    _allRooms.value = result
                }
        }
    }

    fun getCityHall(id: Int, startDate: String, endDate: String){
        viewModelScope.launch {
            sigemesRepository.getCityHall(id, startDate, endDate)
                .collect{ result ->
                    _cityHall.value = result
                }
        }
    }

}
