package com.android.sigemesapp.presentation.auth.register

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
import com.android.sigemesapp.R
import dagger.hilt.android.AndroidEntryPoint
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.databinding.ActivityEmailVerificationOtpBinding
import com.android.sigemesapp.presentation.auth.AuthViewModel
import com.android.sigemesapp.presentation.auth.login.LoginActivity

@AndroidEntryPoint
class EmailVerificationOtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmailVerificationOtpBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailVerificationOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val email = intent.getStringExtra("EMAIL") ?: ""
        binding.verifEmail.text = email

        setupView()
        setupAction()
    }

    private fun setupAction() {
        binding.btnVerifyOtp.setOnClickListener {
            verifyOtp()
        }
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

    private fun verifyOtp() {
        val otp = binding.inputOtp.text.toString().trim()
        val email = intent.getStringExtra("EMAIL") ?: ""

        if (otp.isNotEmpty() && email.isNotEmpty()) {
            authViewModel.verifyOtpEmail(email, otp)
            authViewModel.verifyOtpResult.observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
//                        loadingDialog.startLoadingDialog()
                    }

                    is Result.Success -> {
//                        loadingDialog.dismissDialog()
//                        successDialog.startSuccessDialog(getString(R.string.otp_verified))
                        navigateToLogin()
                    }

                    is Result.Error -> {
//                        loadingDialog.dismissDialog()
//                        val failedDialog = FailedDialog(this)
//                        failedDialog.startFailedDialog(getString(R.string.otp_verification_failed))
                    }
                }
            }
        } else {
            Toast.makeText(this, R.string.otp_empty, Toast.LENGTH_SHORT).show()
        }

    }

    private fun navigateToLogin() {
        Intent(this@EmailVerificationOtpActivity, LoginActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }
}