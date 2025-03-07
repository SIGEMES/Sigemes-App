package com.android.sigemesapp.presentation.home.search.rent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.sigemesapp.data.source.remote.response.CityHallRent
import com.android.sigemesapp.data.source.remote.response.CreateCityHallRentResponse
import com.android.sigemesapp.data.source.remote.response.CreateGuesthouseRentResponse
import com.android.sigemesapp.data.source.remote.response.GuesthouseRentData
import com.android.sigemesapp.data.source.remote.response.RentsDataItem
import com.android.sigemesapp.domain.repository.SigemesRepository
import com.android.sigemesapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RentViewModel @Inject constructor(
    private val sigemesRepository: SigemesRepository
) : ViewModel() {

    private val _guesthouseRentResult = MutableLiveData<Result<CreateGuesthouseRentResponse>>()
    val guesthouseRentResult: LiveData<Result<CreateGuesthouseRentResponse>> get() = _guesthouseRentResult

    private val _cityHallRentResult = MutableLiveData<Result<CreateCityHallRentResponse>>()
    val cityHallRentResult: LiveData<Result<CreateCityHallRentResponse>> get() = _cityHallRentResult

    private val _cancelCityHallResult = MutableLiveData<Result<CityHallRent>>()
    val cancelCityHallResult: LiveData<Result<CityHallRent>> get() = _cancelCityHallResult

    private val _cancelGuesthouseResult = MutableLiveData<Result<GuesthouseRentData>>()
    val cancelGuesthouseResult: LiveData<Result<GuesthouseRentData>> get() = _cancelGuesthouseResult

    private val _allRentsResult = MutableLiveData<Result<List<RentsDataItem>>>()
    val allRentsResult: LiveData<Result<List<RentsDataItem>>> get() = _allRentsResult

    private val _guesthouseDetailRent = MutableLiveData<Result<GuesthouseRentData>>()
    val guesthouseDetailRent: LiveData<Result<GuesthouseRentData>> get() = _guesthouseDetailRent

    private val _cityhallDetailRent = MutableLiveData<Result<CityHallRent>>()
    val cityhallDetailRent: LiveData<Result<CityHallRent>> get() = _cityhallDetailRent


    fun createGuesthouseRent(guesthouseRoomPricingId: Int, slot: Int, startDate: String, endDate: String, renterGender: String) {
        viewModelScope.launch {
            sigemesRepository.createGuesthouseRent(guesthouseRoomPricingId, slot, startDate, endDate, renterGender)
                .collect { result ->
                    _guesthouseRentResult.value = result
                }
        }
    }

    fun createCityHallRent(cityHallPricingId: Int, slot: Int, startDate: String, endDate: String, renterGender: String) {
        viewModelScope.launch {
            sigemesRepository.createCityHallRent(cityHallPricingId, slot, startDate, endDate, renterGender)
                .collect { result ->
                    _cityHallRentResult.value = result
                }
        }
    }

    fun cancelCityHallRent(id: Int) {
        viewModelScope.launch {
            sigemesRepository.cancelCityHall(id)
                .collect { result ->
                    _cancelCityHallResult.value = result
                }
        }
    }

    fun cancelGuesthouseRent(id: Int) {
        viewModelScope.launch {
            sigemesRepository.cancelGuesthouse(id)
                .collect { result ->
                    _cancelGuesthouseResult.value = result
                }
        }
    }

    fun getAllRents(){
        viewModelScope.launch {
            sigemesRepository.getAllRents()
                .collect { result ->
                    _allRentsResult.value = result
                }
        }
    }

    fun getDetailGuesthouseRent(id: Int){
        viewModelScope.launch {
            sigemesRepository.getGuesthouseDetailRent(id)
                .collect{ result ->
                    _guesthouseDetailRent.value = result
                }
        }
    }

    fun getDetailCityHallRent(id: Int){
        viewModelScope.launch {
            sigemesRepository.getCityHallDetailRent(id)
                .collect{ result ->
                    _cityhallDetailRent.value = result
                }
        }
    }

}
