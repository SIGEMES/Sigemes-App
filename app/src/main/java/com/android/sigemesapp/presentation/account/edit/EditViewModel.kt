package com.android.sigemesapp.presentation.account.edit

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.sigemesapp.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val fullname = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val gender = MutableLiveData<String>()
    val currentImageUri = MutableLiveData<Uri?>()

    private val _isNameChanged = MutableLiveData(false)
    val isNameChanged: LiveData<Boolean> = _isNameChanged

    private val _isPhoneChanged = MutableLiveData(false)
    val isPhoneChanged: LiveData<Boolean> = _isPhoneChanged

    private val _isGenderChanged = MutableLiveData(false)
    val isGenderChanged: LiveData<Boolean> = _isGenderChanged

    private val _isPhotoChanged = MutableLiveData(false)
    val isPhotoChanged: LiveData<Boolean> = _isPhotoChanged

    // Fungsi check perubahan input text
    fun checkNameChanged(initial: String) {
        _isNameChanged.value = fullname.value?.trim() != initial
    }

    fun checkPhoneChanged(initial: String) {
        _isPhoneChanged.value = phoneNumber.value?.trim() != initial
    }

    fun checkGenderChanged(initial: String) {
        _isGenderChanged.value = gender.value?.equals(initial, ignoreCase = true)?.not() ?: false
    }

    fun setPhotoChanged(isChanged: Boolean) {
        _isPhotoChanged.value = isChanged
    }

}