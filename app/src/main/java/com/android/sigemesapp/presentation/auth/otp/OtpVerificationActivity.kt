package com.android.sigemesapp.presentation.auth.otp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.android.sigemesapp.databinding.ActivityOtpVerificationBinding
import com.android.sigemesapp.presentation.auth.AuthViewModel
import com.android.sigemesapp.presentation.auth.changePassword.ChangePasswordActivity
import com.android.sigemesapp.presentation.auth.forgotPassword.ForgotPasswordActivity
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.dialog.FailedDialog
import com.android.sigemesapp.utils.dialog.LoadingDialog
import com.android.sigemesapp.utils.dialog.SuccessDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpVerificationBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val loadingDialog = LoadingDialog(this)
    private val successDialog = SuccessDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.otpverif)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupView()
        setupAction()

        val email = intent.getStringExtra("EMAIL") ?: ""
        binding.emailForgotPassword.text = email
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
        binding.btnVerifyOtp.setOnClickListener {
            verifyOtp()
        }

        binding.btnBackToForgotPassword.setOnClickListener {
            backToForgotPassword()
        }
    }

    private fun verifyOtp() {
        val otp = binding.inputOtp.text.toString().trim()
        val email = intent.getStringExtra("EMAIL") ?: ""

        if (otp.isNotEmpty() && email.isNotEmpty()) {
            authViewModel.verifyOtpForgotPassword(email, otp)
            authViewModel.verifyChangePassOtpResult.observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        loadingDialog.startLoadingDialog()
                    }

                    is Result.Success -> {
                        loadingDialog.dismissDialog()
                        successDialog.startSuccessDialog(getString(R.string.otp_verified))
                        navigateToChangePassword()
                    }

                    is Result.Error -> {
                        loadingDialog.dismissDialog()
                        val failedDialog = FailedDialog(this)
                        failedDialog.startFailedDialog(getString(R.string.otp_verification_failed))
                        lifecycleScope.launch {
                            delay(2000)
                            failedDialog.dismissDialog()
                        }
                    }
                }
            }
        } else {
            Toast.makeText(this, R.string.otp_empty, Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToChangePassword() {
        lifecycleScope.launch {
            delay(2000)
            successDialog.dismissDialog()
            Intent(this@OtpVerificationActivity, ChangePasswordActivity::class.java).also {
                val email = intent.getStringExtra("EMAIL") ?: ""
                it.putExtra("EMAIL", email)
                startActivity(it)
            }
        }
    }

    private fun backToForgotPassword() {
        Intent(this@OtpVerificationActivity, ForgotPasswordActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
            finish()
        }
    }
}