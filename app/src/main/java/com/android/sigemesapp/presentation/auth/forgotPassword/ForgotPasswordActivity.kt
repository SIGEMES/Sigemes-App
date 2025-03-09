package com.android.sigemesapp.presentation.auth.forgotPassword

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.android.sigemesapp.R
import com.android.sigemesapp.databinding.ActivityForgotPasswordBinding
import com.android.sigemesapp.presentation.auth.AuthViewModel
import com.android.sigemesapp.presentation.auth.login.LoginActivity
import com.android.sigemesapp.presentation.auth.otp.OtpVerificationActivity
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.dialog.FailedDialog
import com.android.sigemesapp.utils.dialog.LoadingDialog
import com.android.sigemesapp.utils.dialog.SuccessDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val loadingDialog = LoadingDialog(this)
    private val successDialog = SuccessDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.forgot_password)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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

    private fun setupAction() {
        binding.backToLogin.setOnClickListener {
            Intent(this@ForgotPasswordActivity, LoginActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(it)
                finish()
            }
        }

        binding.btnSendOtp.setOnClickListener {
            val email = binding.edLoginEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                sendForgotPasswordOtp(email)
            } else {
                Toast.makeText(this, R.string.email_empty_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendForgotPasswordOtp(email: String) {
        authViewModel.sendOtpForgotPassword(email)
        authViewModel.sendChangePassOtpResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    loadingDialog.startLoadingDialog()
                }
                is Result.Success -> {
                    loadingDialog.dismissDialog()
                    val successDialog = SuccessDialog(this)
                    successDialog.startSuccessDialog(getString(R.string.otp_sent_success))
                    navigateToOtpVerification(email)
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

    private fun navigateToOtpVerification(email: String) {
        lifecycleScope.launch {
            delay(2000)
            successDialog.dismissDialog()
            Intent(this@ForgotPasswordActivity, OtpVerificationActivity::class.java).also {
                it.putExtra("EMAIL", email)
                startActivity(it)
            }
        }
    }
}