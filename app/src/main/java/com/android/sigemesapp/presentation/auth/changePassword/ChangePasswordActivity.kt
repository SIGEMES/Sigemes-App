package com.android.sigemesapp.presentation.auth.changePassword

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
import com.android.sigemesapp.databinding.ActivityChangePasswordBinding
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
class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val loadingDialog = LoadingDialog(this)
    private val successDialog = SuccessDialog(this)

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
        binding.btnChangePassword.setOnClickListener {
            val newPassword = binding.edNewPassword.text.toString().trim()
            val confirmPassword = binding.edConfNewPassword.text.toString().trim()

            if (validateInputs(newPassword, confirmPassword)) {
                changePassword(newPassword)
            }
        }
    }

    private fun validateInputs(newPassword: String, confirmPassword: String): Boolean {
        return when {
            newPassword.isEmpty() -> {
                Toast.makeText(this, R.string.new_password_empty_error, Toast.LENGTH_SHORT).show()
                false
            }
            confirmPassword.isEmpty() -> {
                Toast.makeText(this, R.string.confirm_new_password, Toast.LENGTH_SHORT).show()
                false
            }
            newPassword != confirmPassword -> {
                Toast.makeText(this, R.string.passwords_same_error, Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun changePassword(newPassword: String) {
        val email = intent.getStringExtra("EMAIL") ?: ""

        if (email.isEmpty()) {
            val failedDialog = FailedDialog(this)
            failedDialog.startFailedDialog(getString(R.string.email_not_found))
            lifecycleScope.launch {
                delay(2000)
                failedDialog.dismissDialog()
            }
            return
        }

        authViewModel.changePassword(email, newPassword)
        authViewModel.changePasswordResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    loadingDialog.startLoadingDialog()
                }

                is Result.Success -> {
                    loadingDialog.dismissDialog()
                    val successDialog = SuccessDialog(this)
                    successDialog.startSuccessDialog(getString(R.string.save_password_success))
                    navigateToLogin()
                }

                is Result.Error -> {
                    loadingDialog.dismissDialog()
                    val failedDialog = FailedDialog(this)
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