package com.android.sigemesapp.presentation.auth

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.android.sigemesapp.data.local.UserSession
import com.android.sigemesapp.data.model.RenterData
import com.android.sigemesapp.data.remote.response.LoginResponse
import com.android.sigemesapp.data.remote.response.MessageResponse
import com.android.sigemesapp.data.remote.response.RenterDataResponse
import com.android.sigemesapp.repository.AuthRepository
import com.android.sigemesapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var currentImageUri: Uri? = null
    var fullname: String? = null
    var gender: String? = null
    var phoneNumber: String? = null

    private val _emptyFieldError = MutableLiveData<String>()
    val emptyFieldError: LiveData<String> = _emptyFieldError

    private val _emailValidationError = MutableLiveData<String?>()
    val emailValidationError: LiveData<String?> = _emailValidationError

    private val _registerResult = MutableLiveData<Result<RenterDataResponse>>()
    val registerResult: LiveData<Result<RenterDataResponse>> get() = _registerResult

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> get() = _loginResult

    private val _sendOtpResult = MutableLiveData<Result<MessageResponse>>()
    val sendOtpResult: LiveData<Result<MessageResponse>> get() = _sendOtpResult

    private val _verifyOtpResult = MutableLiveData<Result<MessageResponse>>()
    val verifyOtpResult: LiveData<Result<MessageResponse>> get() = _verifyOtpResult

    private val _sendOtpResetPass = MutableLiveData<Result<MessageResponse>>()
    val sendOtpResetPass: LiveData<Result<MessageResponse>> get() = _sendOtpResetPass

    private val _verifyOtpResetPassResult = MutableLiveData<Result<MessageResponse>>()
    val verifyOtpResetPassResult: LiveData<Result<MessageResponse>> get() = _verifyOtpResetPassResult

    private val _changePasswordResult = MutableLiveData<Result<MessageResponse>>()
    val changePasswordResult: LiveData<Result<MessageResponse>> get() = _changePasswordResult

    private val _renterData = MutableLiveData<Result<RenterData>>()
    val renterData: LiveData<Result<RenterData>> get() = _renterData

    private val _updatedDataResult = MutableLiveData<Result<RenterDataResponse>>()
    val updatedDataResult: LiveData<Result<RenterDataResponse>> get() = _updatedDataResult

    fun register(email: String, password: String, fullname: String, phoneNumber: String, gender: String) {
        viewModelScope.launch {
            authRepository.register(email, password, fullname, phoneNumber, gender)
                .collect { result ->
                    _registerResult.value = result
                }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _emptyFieldError.value = "Alamat email dan kata sandi tidak boleh kosong!"
            return
        }

        viewModelScope.launch {
            authRepository.login(email, password)
                .collect { result ->
                    _loginResult.value = result
                }
        }
    }


    fun validateEmail(email: String): Boolean {
        return if (email.isBlank()) {
            _emailValidationError.value = "Email tidak boleh kosong"
            false
        } else {
            _emailValidationError.value = null
            true
        }
    }

    fun getSession(): LiveData<UserSession> {
        return authRepository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun sendOtpEmailVerification(email: String) {
        viewModelScope.launch {
            authRepository.sendOtpEmailVerification(email)
                .collect { result ->
                    _sendOtpResult.value = result
                }
        }
    }

    fun sendOtpResetPass(email: String){
        viewModelScope.launch {
            authRepository.sendOtpResetPass(email)
                .collect{ result ->
                    _sendOtpResetPass.value = result
                }
        }
    }

    fun verifyOtpEmail(email: String, otp: String) {
        if (email.isBlank() || otp.isBlank()) {
            _emptyFieldError.value = "Kode OTP tidak boleh kosong!"
            return
        }

        viewModelScope.launch {
            authRepository.verifyOtpEmail(email, otp)
                .collect { result ->
                    _verifyOtpResult.value = result
                }
        }
    }

    fun validateRegisterInputs(
        fullname: String,
        email: String,
        password: String,
        phoneNumber: String,
        selectedGender: String
    ): Pair<Boolean, String> {
        val gender = when (selectedGender) {
            "Laki-Laki" -> "laki_laki"
            "Perempuan" -> "perempuan"
            else -> ""
        }

        return if (fullname.isEmpty() || email.isEmpty() || password.isEmpty() || phoneNumber.isEmpty() || gender.isEmpty()) {
            _emptyFieldError.value = "Semua field wajib diisi"
            Pair(false, "")
        } else {
            Pair(true, gender)
        }
    }


    fun verifyOtpResetPass(email: String, otp: String) {
        if (email.isBlank() || otp.isBlank()) {
            _emptyFieldError.value = "Kode OTP tidak boleh kosong!"
            return
        }

        viewModelScope.launch {
            authRepository.verifyOtpResetPass(email, otp)
                .collect { result ->
                    _verifyOtpResetPassResult.value = result
                }
        }
    }

    fun changePassword(email: String, password: String){
        viewModelScope.launch {
            authRepository.changePassword(email, password)
                .collect{ result ->
                    _changePasswordResult.value = result
                }
        }
    }

    fun getRenterData(id: Int){
        viewModelScope.launch {
            authRepository.getRenterData(id)
                .collect{ result ->
                    _renterData.value = result
                }
        }
    }

    fun updateRenterData(id: Int,
                         fullname: String,
                         phone_number: String,
                         gender: String,
                         profile_picture: File?){
        viewModelScope.launch {
            authRepository.updateRenterData(id, fullname, phone_number, gender, profile_picture)
                .collect{ result ->
                    _updatedDataResult.value = result
                }
        }
    }

    fun validateInputs(newPassword: String, confirmPassword: String): Boolean {
        return when {
            newPassword.isEmpty() -> {
                _emptyFieldError.value = "Kata sandi baru tidak boleh kosong"
                false
            }
            confirmPassword.isEmpty() -> {
                _emptyFieldError.value = "Konfirmasi kata sandi tidak boleh kosong"
                false
            }
            newPassword != confirmPassword -> {
                _emptyFieldError.value = "Kata sandi dan konfirmasi tidak sama"
                false
            }
            else -> {
                true
            }
        }
    }
}