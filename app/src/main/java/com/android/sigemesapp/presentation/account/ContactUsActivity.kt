package com.android.sigemesapp.presentation.account

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.sigemesapp.databinding.ActivityContactUsBinding
import com.android.sigemesapp.presentation.auth.AuthViewModel
import com.android.sigemesapp.presentation.home.HomeViewModel
import com.android.sigemesapp.utils.Result
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ContactUsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactUsBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Hubungi Kami"
        setupWelcome()
        setupContactPersonMess()
        setupContactPersonGedung()
    }

    private fun setupWelcome() {
        authViewModel.getSession().observe(this) { user ->
            if (user.isLogin){
                binding.helloUsername.text = String.format("Halo, ${user.fullname}")
            }
        }
    }

    private fun setupContactPersonGedung() {
        val today = Calendar.getInstance().timeInMillis
        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.timeInMillis

        val ymf = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))

        val startDate = ymf.format(Date(today))
        val endDate = ymf.format(Date(tomorrow))

        homeViewModel.getCityHall(1, startDate, endDate)

        homeViewModel.cityHall.observe(this){ result ->
            when(result){
                is Result.Loading -> {

                }

                is Result.Success -> {
                    binding.phoneGedung.text = result.data.contactPerson
                    binding.buttonWhatsappGedung.setOnClickListener {
                        sendMessage(result.data.contactPerson)
                    }

                }

                is Result.Error -> {
                    Toast.makeText(this, "Tidak dapat mengambil data tentang gedung adam malik", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupContactPersonMess() {
        homeViewModel.getGuesthouse(1)

        homeViewModel.guesthouseResult.observe(this){ result ->
            when(result){
                is Result.Loading -> {
                }

                is Result.Success -> {
                    binding.phoneMess.text = result.data.contactPerson
                    binding.buttonWhatsappMess.setOnClickListener {
                        sendMessage(result.data.contactPerson)
                    }
                }

                is Result.Error -> {
                    Toast.makeText(this, "Tidak dapat mengambil data tentang Mess Pemko", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun sendMessage(contactPerson: String) {
        val cleanPhoneNumber = contactPerson.replace("[^0-9]".toRegex(), "")

        val formattedPhoneNumber = if (cleanPhoneNumber.startsWith("+62")) {
            cleanPhoneNumber
        } else if (cleanPhoneNumber.startsWith("0")) {
            "+62${cleanPhoneNumber.substring(1)}"
        } else {
            "+62$cleanPhoneNumber"
        }

        val uri = Uri.parse("https://wa.me/$formattedPhoneNumber")
        val intent = Intent(Intent.ACTION_VIEW, uri)

        if (isAppInstalled("com.whatsapp")) {
            intent.setPackage("com.whatsapp")
        } else if (isAppInstalled("com.whatsapp.w4b")) {
            intent.setPackage("com.whatsapp.w4b")
        } else {
            Toast.makeText(
                this,
                "WhatsApp tidak ditemukan, silakan install terlebih dahulu.",
                Toast.LENGTH_SHORT
            ).show()
            openPlayStore("com.whatsapp")
            return
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Tidak dapat membuka WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun openPlayStore(packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Play Store tidak tersedia", Toast.LENGTH_SHORT).show()
        }
    }

}