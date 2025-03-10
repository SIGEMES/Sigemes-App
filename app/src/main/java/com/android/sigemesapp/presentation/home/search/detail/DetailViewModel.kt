package com.android.sigemesapp.presentation.home.search.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.sigemesapp.data.source.remote.response.CityHallData
import com.android.sigemesapp.data.source.remote.response.DetailRoom
import com.android.sigemesapp.data.source.remote.response.GuesthouseData
import com.android.sigemesapp.domain.repository.SigemesRepository
import com.android.sigemesapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val sigemesRepository: SigemesRepository
) : ViewModel() {
    private val _guesthouseResult = MutableLiveData<Result<GuesthouseData>>()
    val guesthouseResult: LiveData<Result<GuesthouseData>> get() = _guesthouseResult

    private val _detailRoom = MutableLiveData<Result<DetailRoom>>()
    val detailRoom: LiveData<Result<DetailRoom>> get() = _detailRoom

    private val _detailCityHall = MutableLiveData<Result<CityHallData>>()
    val detailCityHall: LiveData<Result<CityHallData>> get() = _detailCityHall

    fun getDetailGuesthouseRoom(guesthouseId: Int, roomId: Int, startDate: String,endDate: String, renterGender: String){
        viewModelScope.launch {
            sigemesRepository.getDetailGuesthouseRoom(guesthouseId, roomId, startDate, endDate, renterGender)
                .collect{ result ->
                    _detailRoom.value = result
                }
        }
    }

    fun getDetailGuesthouse(guesthouseId: Int){
        viewModelScope.launch {
            sigemesRepository.getDetailGuesthouse(guesthouseId)
                .collect{ result ->
                    _guesthouseResult.value = result
                }
        }
    }

    fun getCityHall(id: Int, startDate: String, endDate: String){
        viewModelScope.launch {
            sigemesRepository.getCityHall(id, startDate, endDate)
                .collect{ result ->
                    _detailCityHall.value = result
                }
        }
    }
}