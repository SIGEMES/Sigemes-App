package com.android.sigemesapp.presentation.home.search.rent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.sigemesapp.data.model.CityHallRent
import com.android.sigemesapp.data.model.GuesthouseRent
import com.android.sigemesapp.data.model.Rents
import com.android.sigemesapp.data.remote.response.CityHallRentResponse
import com.android.sigemesapp.data.remote.response.GuesthouseRentResponse
import com.android.sigemesapp.repository.SigemesRepository
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.isoToTimestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RentViewModel @Inject constructor(
    private val sigemesRepository: SigemesRepository
) : ViewModel() {

    private val _guesthouseRentResult = MutableLiveData<Result<GuesthouseRentResponse>>()
    val guesthouseRentResult: LiveData<Result<GuesthouseRentResponse>> get() = _guesthouseRentResult

    private val _cityHallRentResult = MutableLiveData<Result<CityHallRentResponse>>()
    val cityHallRentResult: LiveData<Result<CityHallRentResponse>> get() = _cityHallRentResult

    private val _cancelCityHallResult = MutableLiveData<Result<CityHallRent>>()
    val cancelCityHallResult: LiveData<Result<CityHallRent>> get() = _cancelCityHallResult

    private val _cancelGuesthouseResult = MutableLiveData<Result<GuesthouseRent>>()
    val cancelGuesthouseResult: LiveData<Result<GuesthouseRent>> get() = _cancelGuesthouseResult

    private val _allRentsResult = MutableLiveData<Result<List<Rents>>>()
    val allRentsResult: LiveData<Result<List<Rents>>> get() = _allRentsResult

    private val _guesthouseDetailRent = MutableLiveData<Result<GuesthouseRent>>()
    val guesthouseDetailRent: LiveData<Result<GuesthouseRent>> get() = _guesthouseDetailRent

    private val _cityhallDetailRent = MutableLiveData<Result<CityHallRent>>()
    val cityhallDetailRent: LiveData<Result<CityHallRent>> get() = _cityhallDetailRent

    private val _navigateTo = MutableLiveData<Triple<String, Int?, String?>>()
    val navigateTo: LiveData<Triple<String, Int?, String?>> get() = _navigateTo

    fun onHistorySelected(data: Rents) {
        val createdAtTimestamp = isoToTimestamp(data.createdAt)
        val paymentTriggeredAtTimestamp = data.payment.paymentTriggeredAt?.let { isoToTimestamp(it) }

        val isExpired = data.rentStatus == "pending" &&
                ((System.currentTimeMillis() > createdAtTimestamp + 5 * 60 * 1000) ||
                        (paymentTriggeredAtTimestamp != null && System.currentTimeMillis() > paymentTriggeredAtTimestamp + 24 * 60 * 60 * 1000))

        if (isExpired) {
            _navigateTo.value = Triple("expired", null, null)
            return
        }

        val category = if (data.cityHallPricing == null) "Mess" else "Gedung"

        when (data.rentStatus) {
            "pending" -> _navigateTo.value = Triple("continue_payment", data.id, category)
            "dikonfirmasi", "selesai" -> _navigateTo.value = Triple("detail", data.id, category)
            else -> _navigateTo.value = Triple("failed", null, null)
        }
    }

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
            sigemesRepository.cancelCityHallRent(id)
                .collect { result ->
                    _cancelCityHallResult.value = result
                }
        }
    }

    fun cancelGuesthouseRent(id: Int) {
        viewModelScope.launch {
            sigemesRepository.cancelGuesthouseRent(id)
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
            sigemesRepository.getDetailGuesthouseRent(id)
                .collect{ result ->
                    _guesthouseDetailRent.value = result
                }
        }
    }

    fun getDetailCityHallRent(id: Int){
        viewModelScope.launch {
            sigemesRepository.getDetailCityHallRent(id)
                .collect{ result ->
                    _cityhallDetailRent.value = result
                }
        }
    }


}
