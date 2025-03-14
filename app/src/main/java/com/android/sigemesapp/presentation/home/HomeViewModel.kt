package com.android.sigemesapp.presentation.home

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.sigemesapp.data.source.remote.response.CityHallData
import com.android.sigemesapp.data.source.remote.response.GuesthouseData
import com.android.sigemesapp.domain.repository.SigemesRepository
import com.android.sigemesapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sigemesRepository: SigemesRepository
) : ViewModel() {

    private val _guesthouseResult = MutableLiveData<Result<GuesthouseData>>()
    val guesthouseResult: LiveData<Result<GuesthouseData>> get() = _guesthouseResult

    private val _cityHall = MutableLiveData<Result<CityHallData>>()
    val cityHall: LiveData<Result<CityHallData>> get() = _cityHall

    private val _dashboardPhoto = mutableListOf<String>()
    val dashboardPhoto: List<String> get() = _dashboardPhoto

    fun addPhotos(newPhotos: String) {
        _dashboardPhoto.add(newPhotos)
    }

    fun clearDashboardPhotos() {
        _dashboardPhoto.clear()
    }

    fun getCityHall(id: Int, startDate: String, endDate: String){
        viewModelScope.launch {
            sigemesRepository.getCityHall(id, startDate, endDate)
                .collect{ result ->
                    _cityHall.value = result
                }
        }
    }

    fun getGuesthouse(id: Int){
        viewModelScope.launch {
            sigemesRepository.getDetailGuesthouse(id)
                .collect{ result ->
                    _guesthouseResult.value = result

                }
        }
    }


}