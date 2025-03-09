package com.android.sigemesapp.presentation.auth.register

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.sigemesapp.R
import com.android.sigemesapp.databinding.ActivityRegisterBinding
import com.android.sigemesapp.presentation.auth.AuthViewModel
import com.android.sigemesapp.presentation.auth.login.LoginActivity
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.dialog.FailedDialog
import com.android.sigemesapp.utils.dialog.LoadingDialog
import com.android.sigemesapp.utils.dialog.SuccessDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var genderSpinner: Spinner
    private val successDialog = SuccessDialog(this)
    private val loadingDialog = LoadingDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        genderSpinner = binding.spinnerCrops
        setupView()
        setupAction()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction(){
        binding.registerButton.setOnClickListener {
            register()
        }

        binding.belumPunyaAkun.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        Intent(this@RegisterActivity, LoginActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }

    private fun register() {
        val fullname = binding.edRegisterName.text.toString()
        val phoneNumber = binding.edRegisterPnum.text.toString()
        val checkgender = genderSpinner.selectedItem.toString()
        var gender = ""
        if (checkgender == "Perempuan"){
            gender = "perempuan"
        } else if (checkgender == "Laki-Laki"){
            gender = "laki_laki"
        }
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edLoginPassword.text.toString()

        if (fullname.isEmpty() || email.isEmpty() || password.isEmpty() || phoneNumber.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, R.string.all_req, Toast.LENGTH_SHORT).show()
        } else {
            registerUser(fullname, email, password, phoneNumber, gender)
        }
    }

    private fun registerUser(fullname: String, email: String, password: String, phoneNumber: String, gender: String) {

        authViewModel.register(email, password, fullname, phoneNumber, gender)
        authViewModel.registerResult.observe(this@RegisterActivity) { result ->
            when (result) {
                is Result.Loading -> {
                    loadingDialog.startLoadingDialog()
                }
                is Result.Success -> {
                    loadingDialog.dismissDialog()
                    successDialog.startSuccessDialog(getString(R.string.register_success))
                    lifecycleScope.launch {
                        delay(2000)
                        sendOtpEmailVerif(email)
                    }
                }
                is Result.Error -> {
                    loadingDialog.dismissDialog()
                    val failedDialog = FailedDialog(this)
                    failedDialog.startFailedDialog(getString(R.string.register_failed))
                    lifecycleScope.launch {
                        delay(2000)
                        failedDialog.dismissDialog()
                    }
                }
            }
        }

    }

    private fun sendOtpEmailVerif(email: String) {
        authViewModel.sendOtpEmailVerification(email)

        authViewModel.sendOtpResult.observe(this) { result ->
            when(result) {
                is Result.Loading -> {
                    loadingDialog.startLoadingDialog()
                }
                is Result.Success -> {
                    loadingDialog.dismissDialog()
                    val successDialog = SuccessDialog(this)
                    successDialog.startSuccessDialog(getString(R.string.otp_sent_success))
                    navigateToVerificationEmail(email)
                }
                is Result.Error -> {
                    val failedDialog = FailedDialog(this)
                    failedDialog.startFailedDialog(getString(R.string.otp_sent_failed))
                    lifecycleScope.launch {
                        delay(2000)
                        failedDialog.dismissDialog()
                    }
                }
            }
        }
    }

    private fun navigateToVerificationEmail(email: String) {
        lifecycleScope.launch {
            delay(2000)
            successDialog.dismissDialog()
            Intent(this@RegisterActivity, EmailVerificationOtpActivity::class.java).also {
                it.putExtra("EMAIL", email)
                startActivity(it)
            }
        }
    }
}