package com.android.sigemesapp.presentation.auth.changePassword

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
import com.android.sigemesapp.databinding.ActivityChangePasswordBinding
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
class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val successDialog by lazy { SuccessDialog(this) }
    private val failedDialog by lazy { FailedDialog(this) }
    private val loadingDialog by lazy { LoadingDialog(this) }
    private var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.changePassword)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        email = intent.getStringExtra(EXTRA_EMAIL) ?: ""
        setupView()
        setupAction()
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
        binding.btnChangePassword.setOnClickListener {
            val newPassword = binding.edNewPassword.text.toString().trim()
            val confirmPassword = binding.edConfNewPassword.text.toString().trim()

            if (authViewModel.validateInputs(newPassword, confirmPassword)) {
                changePassword(newPassword)
            }
        }

        authViewModel.emptyFieldError.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun changePassword(newPassword: String) {
        authViewModel.changePassword(email, newPassword)
        authViewModel.changePasswordResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    loadingDialog.startLoadingDialog()
                }

                is Result.Success -> {
                    loadingDialog.dismissDialog()
                    successDialog.startSuccessDialog(getString(R.string.save_password_success))
                    navigateToLogin()
                }

                is Result.Error -> {
                    loadingDialog.dismissDialog()
                    failedDialog.startFailedDialog(getString(R.string.save_password_failed))
                    lifecycleScope.launch {
                        delay(2000)
                        failedDialog.dismissDialog()
                    }
                }
            }
        }
    }

    private fun navigateToLogin() {
        lifecycleScope.launch {
            delay(2000)
            successDialog.dismissDialog()
            Intent(this@ChangePasswordActivity, LoginActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(it)
                finish()
            }
        }
    }
}