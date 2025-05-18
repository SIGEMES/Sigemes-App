package com.android.sigemesapp.presentation.auth.otp

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
import com.android.sigemesapp.databinding.ActivityOtpVerificationBinding
import com.android.sigemesapp.presentation.auth.AuthViewModel
import com.android.sigemesapp.presentation.auth.changePassword.ChangePasswordActivity
import com.android.sigemesapp.presentation.auth.forgotPassword.ForgotPasswordActivity
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_EMAIL
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.dialog.FailedDialog
import com.android.sigemesapp.utils.dialog.LoadingDialog
import com.android.sigemesapp.utils.dialog.SuccessDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OtpResetPassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpVerificationBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val successDialog by lazy { SuccessDialog(this) }
    private val failedDialog by lazy { FailedDialog(this) }
    private val loadingDialog by lazy { LoadingDialog(this) }
    private var email = ""

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

        email = intent.getStringExtra(EXTRA_EMAIL) ?: ""
        binding.emailForgotPassword.text = email
    }

//    private fun setupView() {
//        @Suppress("DEPRECATION")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            window.insetsController?.hide(WindowInsets.Type.statusBars())
//        } else {
//            window.setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//            )
//        }
//        supportActionBar?.hide()
//    }

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

    private fun setupAction() {
        binding.btnVerifyOtp.setOnClickListener {
            verifyOtp()
        }

        binding.btnBackToForgotPassword.setOnClickListener {
            backToForgotPassword()
        }

        binding.resendKode.setOnClickListener {
            sendOtpEmailVerif(email)
        }

        authViewModel.emptyFieldError.observe(this) {
            it?.let { msg -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun verifyOtp() {
        val otp = binding.inputOtp.text.toString().trim()
        authViewModel.verifyOtpResetPass(email, otp)
        authViewModel.verifyOtpResetPassResult.observe(this) { result ->
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
                    failedDialog.startFailedDialog(getString(R.string.otp_verification_failed))
                    lifecycleScope.launch {
                        delay(2000)
                        failedDialog.dismissDialog()
                    }
                }
            }
        }
    }

    private fun navigateToChangePassword() {
        lifecycleScope.launch {
            delay(2000)
            successDialog.dismissDialog()
            Intent(this@OtpResetPassActivity, ChangePasswordActivity::class.java).also {
                it.putExtra(EXTRA_EMAIL, email)
                startActivity(it)
            }
        }
    }

    private fun sendOtpEmailVerif(email: String) {
        authViewModel.sendOtpResetPass(email)
        authViewModel.sendOtpResetPass.observe(this) { result ->
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

    private fun backToForgotPassword() {
        Intent(this@OtpResetPassActivity, ForgotPasswordActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
            finish()
        }
    }
}