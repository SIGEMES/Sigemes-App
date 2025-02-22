package com.android.sigemesapp.presentation.auth

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.android.sigemesapp.data.source.local.UserModel
import com.android.sigemesapp.data.source.remote.response.ChangePasswordResponse
import com.android.sigemesapp.data.source.remote.response.LoginResponse
import com.android.sigemesapp.data.source.remote.response.RegisterResponse
import com.android.sigemesapp.data.source.remote.response.RenterData
import com.android.sigemesapp.data.source.remote.response.SendOtpResponse
import com.android.sigemesapp.data.source.remote.response.UpdateProfileResponse
import com.android.sigemesapp.data.source.remote.response.UpdatedData
import com.android.sigemesapp.data.source.remote.response.VerifyEmailResponse
import com.android.sigemesapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import com.android.sigemesapp.utils.Result
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

    private val _registerResult = MutableLiveData<Result<RegisterResponse>>()
    val registerResult: LiveData<Result<RegisterResponse>> get() = _registerResult

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> get() = _loginResult

    private val _sendOtpResult = MutableLiveData<Result<SendOtpResponse>>()
    val sendOtpResult: LiveData<Result<SendOtpResponse>> get() = _sendOtpResult

    private val _verifyOtpResult = MutableLiveData<Result<VerifyEmailResponse>>()
    val verifyOtpResult: LiveData<Result<VerifyEmailResponse>> get() = _verifyOtpResult

    private val _sendChangePassOtpResult = MutableLiveData<Result<SendOtpResponse>>()
    val sendChangePassOtpResult: LiveData<Result<SendOtpResponse>> get() = _sendChangePassOtpResult

    private val _verifyChangePassOtpResult = MutableLiveData<Result<VerifyEmailResponse>>()
    val verifyChangePassOtpResult: LiveData<Result<VerifyEmailResponse>> get() = _verifyChangePassOtpResult

    private val _changePasswordResult = MutableLiveData<Result<ChangePasswordResponse>>()
    val changePasswordResult: LiveData<Result<ChangePasswordResponse>> get() = _changePasswordResult

    private val _renterData = MutableLiveData<Result<RenterData>>()
    val renterData: LiveData<Result<RenterData>> get() = _renterData

    private val _updatedDataResult = MutableLiveData<Result<UpdateProfileResponse>>()
    val updatedDataResult: LiveData<Result<UpdateProfileResponse>> get() = _updatedDataResult

    fun register(email: String, password: String, fullname: String, phoneNumber: String, gender: String) {
        viewModelScope.launch {
            authRepository.register(email, password, fullname, phoneNumber, gender)
                .collect { result ->
                    _registerResult.value = result
                }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authRepository.login(email, password)
                .collect { result ->
                    _loginResult.value = result
                }
        }
    }

    fun getSession(): LiveData<UserModel> {
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

    fun verifyOtpEmail(email: String, otp: String) {
        viewModelScope.launch {
            authRepository.verifyOtpEmail(email, otp)
                .collect { result ->
                    _verifyOtpResult.value = result
                }
        }
    }

    fun sendOtpForgotPassword(email: String){
        viewModelScope.launch {
            authRepository.sendOtpForgotPassword(email)
                .collect{ result ->
                    _sendChangePassOtpResult.value = result
                }
        }
    }

    fun verifyOtpForgotPassword(email: String, otp: String) {
        viewModelScope.launch {
            authRepository.verifyOtpForgotPassword(email, otp)
                .collect{ result ->
                    _verifyChangePassOtpResult.value = result
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
}