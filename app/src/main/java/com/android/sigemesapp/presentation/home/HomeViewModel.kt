package com.android.sigemesapp.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.sigemesapp.data.model.CityHall
import com.android.sigemesapp.data.model.Guesthouse
import com.android.sigemesapp.repository.SigemesRepository
import com.android.sigemesapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sigemesRepository: SigemesRepository
) : ViewModel() {

    private val _guesthouseResult = MutableLiveData<Result<Guesthouse>>()
    val guesthouseResult: LiveData<Result<Guesthouse>> get() = _guesthouseResult

    private val _cityHall = MutableLiveData<Result<CityHall>>()
    val cityHall: LiveData<Result<CityHall>> get() = _cityHall

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
            sigemesRepository.getGuesthouse(id)
                .collect{ result ->
                    _guesthouseResult.value = result

                }
        }
    }

    fun tryAddDashboardPhoto(url: String, onComplete: () -> Unit) {
        if (dashboardPhoto.count() < 2 && dashboardPhoto.firstOrNull() != url) {
            addPhotos(url)
            if (dashboardPhoto.count() == 2) {
                onComplete()
            }
        }
    }

}