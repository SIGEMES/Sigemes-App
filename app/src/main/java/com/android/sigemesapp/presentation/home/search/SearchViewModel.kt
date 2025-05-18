package com.android.sigemesapp.presentation.home.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.sigemesapp.data.model.CityHall
import com.android.sigemesapp.data.model.RoomItem
import com.android.sigemesapp.repository.SigemesRepository
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

    private val _cityHall = MutableLiveData<Result<CityHall>>()
    val cityHall: LiveData<Result<CityHall>> get() = _cityHall


    fun getRooms(id: Int, startDate: String, endDate: String, renterGender: String) {
        viewModelScope.launch {
            sigemesRepository.getGuesthouseRooms(id, startDate, endDate, renterGender)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            val filteredList = result.data.filter { it.availableSlot > 0 }
                            _allRooms.value = Result.Success(filteredList)
                        }
                        is Result.Error -> {
                            _allRooms.value = result
                        }
                        is Result.Loading -> {
                            _allRooms.value = result
                        }
                    }
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
