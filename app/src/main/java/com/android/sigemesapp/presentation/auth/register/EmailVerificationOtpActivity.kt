package com.android.sigemesapp.presentation.auth.register

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.android.sigemesapp.R
import com.android.sigemesapp.databinding.ActivityEmailVerificationOtpBinding
import com.android.sigemesapp.presentation.auth.AuthViewModel
import com.android.sigemesapp.presentation.auth.login.LoginActivity
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_EMAIL
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.dialog.FailedDialog
import com.android.sigemesapp.utils.dialog.LoadingDialog
import com.android.sigemesapp.utils.dialog.SuccessDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EmailVerificationOtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmailVerificationOtpBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val successDialog by lazy { SuccessDialog(this) }
    private val failedDialog by lazy { FailedDialog(this) }
    private val loadingDialog by lazy { LoadingDialog(this) }
    private var email = ""

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

        email = intent.getStringExtra(EXTRA_EMAIL) ?: ""
        binding.verifEmail.text = email

        setupView()
        setupAction()
    }

    private fun setupAction() {
        binding.btnVerifyOtp.setOnClickListener {
            verifyOtp()
        }

        binding.resendKode.setOnClickListener {
            sendOtpEmailVerif(email)
        }

    }

    private fun setupView() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.hide(WindowInsets.Type.systemBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.updatePadding(
                    left = systemBars.left,
                    top = systemBars.top,
                    right = systemBars.right,
                    bottom = systemBars.bottom
                )
                insets
            }
        }
        supportActionBar?.hide()
    }

    private fun verifyOtp() {
        val otp = binding.inputOtp.text.toString().trim()
        authViewModel.verifyOtpEmail(email, otp)

        authViewModel.verifyOtpResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    loadingDialog.startLoadingDialog()
                }

                is Result.Success -> {
                    loadingDialog.dismissDialog()
                    successDialog.startSuccessDialog(getString(R.string.otp_verified))
                    lifecycleScope.launch {
                        delay(2000)
                        navigateToLogin()
                    }
                }

                is Result.Error -> {
                    loadingDialog.dismissDialog()
                    failedDialog.startFailedDialog(getString(R.string.otp_verification_failed))
                    lifecycleScope.launch {
                        delay(2000)
                        failedDialog.dismissDialog()
                    }
                }
            }
        }

        authViewModel.emptyFieldError.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
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
                    successDialog.startSuccessDialog(getString(R.string.otp_sent_success))
                    lifecycleScope.launch {
                        delay(2000)
                        successDialog.dismissDialog()
                    }
                }
                is Result.Error -> {
                    loadingDialog.dismissDialog()
                    failedDialog.startFailedDialog(getString(R.string.otp_sent_failed))
                    lifecycleScope.launch {
                        delay(2000)
                        failedDialog.dismissDialog()
                    }
                }
            }
        }
    }

    private fun navigateToLogin() {
        Intent(this@EmailVerificationOtpActivity, LoginActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }
}