package com.android.sigemesapp.presentation.home.search.rent.payment

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.sigemesapp.R
import com.android.sigemesapp.presentation.home.search.rent.payment.PaymentActivity.Companion.EXTRA_BEFORE

class WebViewActivity : AppCompatActivity() {

    companion object {
        const val KEY_TOKEN = "key_token"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        supportActionBar?.hide()

        val token = intent.getStringExtra(KEY_TOKEN) ?: "Default Query"
        Log.e("token", "token : $token")

        val webView = findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl("https://app.sandbox.midtrans.com/snap/v4/redirection/$token")
    }
}