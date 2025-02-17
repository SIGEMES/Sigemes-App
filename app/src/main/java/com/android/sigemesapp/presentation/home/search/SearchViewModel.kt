package com.android.sigemesapp.presentation.home.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.sigemesapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _countRoom = MutableLiveData(0)
    val countRoom: LiveData<Int> get() = _countRoom

    private val _countGuest = MutableLiveData(1)
    val countGuest: LiveData<Int> get() = _countGuest

    fun incrementRoom() {
        if ((_countRoom.value ?: 0) < 7) {
            _countRoom.value = (_countRoom.value ?: 0) + 1
        }
    }

    fun decrementRoom() {
        if ((_countRoom.value ?: 0) > 0) {
            _countRoom.value = (_countRoom.value ?: 0) - 1
        }
    }

    fun incrementGuest() {
        _countGuest.value = (_countGuest.value ?: 0) + 1
    }

    fun decrementGuest() {
        if ((_countGuest.value ?: 0) > 1) {
            _countGuest.value = (_countGuest.value ?: 0) - 1
        }
    }

    fun setRoomCount(count: Int) {
        _countRoom.value = count
    }

    fun setGuestCount(count: Int) {
        _countGuest.value = count
    }

}
