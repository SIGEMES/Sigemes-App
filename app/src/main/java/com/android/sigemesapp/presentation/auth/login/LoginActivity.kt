package com.android.sigemesapp.presentation.auth.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.android.sigemesapp.MainActivity
import com.android.sigemesapp.R
import com.android.sigemesapp.databinding.ActivityLoginBinding
import com.android.sigemesapp.presentation.auth.AuthViewModel
import com.android.sigemesapp.presentation.auth.forgotPassword.ForgotPasswordActivity
import com.android.sigemesapp.presentation.auth.register.EmailVerificationOtpActivity
import com.android.sigemesapp.presentation.auth.register.RegisterActivity
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_EMAIL
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.dialog.FailedDialog
import com.android.sigemesapp.utils.dialog.LoadingDialog
import com.android.sigemesapp.utils.dialog.SuccessDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val successDialog by lazy { SuccessDialog(this) }
    private val failedDialog by lazy { FailedDialog(this) }
    private val loadingDialog by lazy { LoadingDialog(this) }
    private var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        setupView()
        setupAction()
        observeViewModel()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            email = binding.edLoginEmail.text.toString().trim()
            val password = binding.edLoginPassword.text.toString().trim()
            authViewModel.login(email, password)
        }

        binding.belumPunyaAkun.setOnClickListener{
            navigateToRegister()
        }

        binding.lupaPasswordText.setOnClickListener{
            navigateToForgotPassword()
        }
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

    private fun navigateToForgotPassword() {
        Intent(this@LoginActivity, ForgotPasswordActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
            finish()
        }
    }

    private fun navigateToRegister() {
        Intent(this@LoginActivity, RegisterActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
            finish()
        }
    }

    private fun observeViewModel() {
        authViewModel.loginResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> loadingDialog.startLoadingDialog()
                is Result.Success -> {
                    loadingDialog.dismissDialog()
                    successDialog.startSuccessDialog(getString(R.string.login_success))
                    navigateToMain()

                }
                is Result.Error -> {
//                    loadingDialog.dismissDialog()
//                    if(result.message.contains("Email not verified", ignoreCase = true)) {
//                        failedDialog.startFailedDialog(getString(R.string.email_not_verified))
//                        lifecycleScope.launch {
//                            delay(2000)
//                            sendOtpEmailVerif(email)
//                        }
//                    }
                    loadingDialog.dismissDialog()
                    val message = result.message ?: ""
                    if (message.contains("email not verified", ignoreCase = true)) {
                        failedDialog.startFailedDialog(getString(R.string.email_not_verified))
                        lifecycleScope.launch {
                            delay(2000)
                            sendOtpEmailVerif(email)
                            failedDialog.dismissDialog()
                        }
                    } else {
                        failedDialog.startFailedDialog(getString(R.string.login_failed))
                        lifecycleScope.launch {
                            delay(2000)
                            failedDialog.dismissDialog()
                        }
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
                    navigateToVerificationEmail(email)
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

    private fun navigateToVerificationEmail(email: String) {
        lifecycleScope.launch {
            delay(2000)
            successDialog.dismissDialog()
            Intent(this@LoginActivity, EmailVerificationOtpActivity::class.java).also {
                it.putExtra(EXTRA_EMAIL, email)
                startActivity(it)
            }
        }
    }

    private fun navigateToMain() {
        lifecycleScope.launch {
            delay(2000)
            successDialog.dismissDialog()
            Intent(this@LoginActivity, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(it)
                finish()
            }
        }
    }

}