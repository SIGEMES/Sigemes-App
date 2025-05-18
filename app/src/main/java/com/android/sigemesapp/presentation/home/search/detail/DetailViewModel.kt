package com.android.sigemesapp.presentation.home.search.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.sigemesapp.data.model.CityHall
import com.android.sigemesapp.data.model.CityHallPricings
import com.android.sigemesapp.data.model.Guesthouse
import com.android.sigemesapp.data.model.PricingItem
import com.android.sigemesapp.data.model.RoomItem
import com.android.sigemesapp.repository.SigemesRepository
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.extractFacilities
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val sigemesRepository: SigemesRepository
) : ViewModel() {
    private val _guesthouseResult = MutableLiveData<Result<Guesthouse>>()
    val guesthouseResult: LiveData<Result<Guesthouse>> get() = _guesthouseResult

    private val _detailRoom = MutableLiveData<Result<RoomItem>>()
    val detailRoom: LiveData<Result<RoomItem>> get() = _detailRoom

    private val _detailCityHall = MutableLiveData<Result<CityHall>>()
    val detailCityHall: LiveData<Result<CityHall>> get() = _detailCityHall

    fun getDetailGuesthouseRoom(guesthouseId: Int, roomId: Int, startDate: String,endDate: String, renterGender: String){
        viewModelScope.launch {
            sigemesRepository.getDetailRooms(guesthouseId, roomId, startDate, endDate, renterGender)
                .collect{ result ->
                    _detailRoom.value = result
                }
        }
    }

    fun getDetailGuesthouse(guesthouseId: Int){
        viewModelScope.launch {
            sigemesRepository.getGuesthouse(guesthouseId)
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

    fun formatFacilityList(facilities: String): String {
        val formatted = extractFacilities(facilities)
        return formatted.joinToString("\n- ", prefix = "- ")
    }

    fun formatCityHallFacilities(cityHall: CityHall): String {
        val area = "${cityHall.areaM2} m²"
        val capacity = "${cityHall.peopleCapacity} orang"
        val facilities = extractFacilities(cityHall.pricing[1].facilities).toMutableList()
        facilities.add(0, "Luas: $area")
        facilities.add(1, "Kapasitas: $capacity")
        return facilities.joinToString("\n- ", prefix = "- ")
    }

    fun calculateTotalPrice(pricePerDay: Int, duration: Int): Int {
        return pricePerDay * duration
    }

    fun formatPrice(value: Int): String {
        return "Rp " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(value)
    }

    fun formatCapacity(capacity: Int): String = "$capacity orang"
    fun formatArea(area: Double): String = "$area m²"
    fun formatDuration(duration: Int): String = "$duration hari"
    fun formatString(num: Int): String = "$num"

    fun getDefaultPricingIndex(pricing: List<CityHallPricings>, defaultType: String = "Komersial"): Int {
        return pricing.indexOfFirst { it.activityType == defaultType }.takeIf { it != -1 } ?: 0
    }

    fun getDefaultPricingRoomIndex(pricing: List<PricingItem>, defaultType: String = "Umum"): Int {
        return pricing.indexOfFirst { it.retributionType == defaultType }.takeIf { it != -1 } ?: 0
    }

    fun calculateSlot(rentType: String, totalSlot: Int): Int {
        return if (rentType.contains("kamar", ignoreCase = true) || rentType.contains("pejabat", ignoreCase = true)) {
            totalSlot
        } else {
            1
        }
    }

}