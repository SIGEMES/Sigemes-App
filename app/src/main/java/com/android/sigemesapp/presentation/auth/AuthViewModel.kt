package com.android.sigemesapp.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.android.sigemesapp.data.source.local.UserModel
import com.android.sigemesapp.data.source.remote.response.ChangePasswordResponse
import com.android.sigemesapp.data.source.remote.response.LoginResponse
import com.android.sigemesapp.data.source.remote.response.RegisterResponse
import com.android.sigemesapp.data.source.remote.response.SendOtpResponse
import com.android.sigemesapp.data.source.remote.response.VerifyEmailResponse
import com.android.sigemesapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import com.android.sigemesapp.utils.Result
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

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

    fun verifyOtpForgotPasswod(email: String, otp: String) {
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
}