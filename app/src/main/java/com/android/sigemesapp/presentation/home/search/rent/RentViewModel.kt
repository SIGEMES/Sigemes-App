package com.android.sigemesapp.presentation.home.search.rent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.sigemesapp.data.model.Rent
import com.android.sigemesapp.data.remote.response.RentResponse
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

    private val _rentResult = MutableLiveData<Result<Rent>>()
    val rentResult: LiveData<Result<Rent>> get() = _rentResult

    private val _createRentResult = MutableLiveData<Result<RentResponse>>()
    val createRentResult: LiveData<Result<RentResponse>> get() = _createRentResult

    private val _cancelRentResult = MutableLiveData<Result<Rent>>()
    val cancelRentResult: LiveData<Result<Rent>> get() = _cancelRentResult

    private val _allRentResult = MutableLiveData<Result<List<Rent>>>()
    val allRentResult: LiveData<Result<List<Rent>>> get() = _allRentResult

    private val _navigateTo = MutableLiveData<Triple<String, Int?, String?>>()
    val navigateTo: LiveData<Triple<String, Int?, String?>> get() = _navigateTo

    fun onHistorySelected(data: Rent) {
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


    fun createRent(pricingId: Int, slot: Int, startDate: String, endDate: String, renterGender: String, rentType: String) {
        viewModelScope.launch {
            sigemesRepository.createRent(pricingId, slot, startDate, endDate, renterGender, rentType)
                .collect { result ->
                    _createRentResult.value = result
                }
        }
    }

    fun cancelRent(id: Int) {
        viewModelScope.launch {
            sigemesRepository.cancelRent(id)
                .collect { result ->
                    _cancelRentResult.value = result
                }
        }
    }

    fun getAllRents(){
        viewModelScope.launch {
            sigemesRepository.getAllRents()
                .collect { result ->
                    _allRentResult.value = result
                }
        }
    }

    fun getRent(id: Int){
        viewModelScope.launch {
            sigemesRepository.getRent(id)
                .collect{ result ->
                    _rentResult.value = result
                }
        }
    }

    fun checkRentType(rent: Rent): String {
        return when {
            rent.guesthouseRoomPricing == null -> "cityhall"
            rent.cityHallPricing == null -> "guesthouse"
            else -> "unknown"
        }
    }

}
