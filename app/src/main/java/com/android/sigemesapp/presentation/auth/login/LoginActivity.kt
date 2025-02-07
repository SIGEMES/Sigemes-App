package com.android.sigemesapp.presentation.auth.login

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
import com.android.sigemesapp.MainActivity
import com.android.sigemesapp.R
import com.android.sigemesapp.databinding.ActivityLoginBinding
import com.android.sigemesapp.presentation.auth.AuthViewModel
import com.android.sigemesapp.presentation.auth.forgotPassword.ForgotPasswordActivity
import com.android.sigemesapp.presentation.auth.register.RegisterActivity
import com.android.sigemesapp.utils.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupView()
        setupAction()

    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            login()
        }

        binding.belumPunyaAkun.setOnClickListener{
            navigateToRegister()
        }

        binding.lupaPasswordText.setOnClickListener{
            navigateToForgotPassword()
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

    private fun login() {
        val email = binding.edLoginEmail.text.toString().trim()
        val password = binding.edLoginPassword.text.toString().trim()

        if (email.isNotEmpty() && password.isNotEmpty()) {

            authViewModel.login(email, password)

            // Observe the login result from ViewModel
            authViewModel.loginResult.observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                    }

                    is Result.Success -> {
                        navigateToMain()
                    }

                    is Result.Error -> {

                    }
                }
            }
        } else {
            Toast.makeText(this, "Alamat email dan kata sandi tidak boleh kosong!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToMain() {
        Intent(this@LoginActivity, MainActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
            finish()
        }
    }

}