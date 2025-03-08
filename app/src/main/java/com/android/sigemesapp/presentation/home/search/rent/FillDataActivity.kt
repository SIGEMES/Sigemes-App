package com.android.sigemesapp.presentation.home.search.rent

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.sigemesapp.R
import com.android.sigemesapp.databinding.ActivityFillDataBinding
import com.android.sigemesapp.presentation.auth.AuthViewModel
import com.android.sigemesapp.presentation.home.search.detail.DetailViewModel
import com.android.sigemesapp.presentation.home.search.detail.review.ReviewActivity
import com.android.sigemesapp.presentation.home.search.rent.payment.PaymentActivity
import com.android.sigemesapp.utils.Result
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class FillDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFillDataBinding
    private val authViewModel: AuthViewModel by viewModels()
    private var startDate : Long = 0
    private var endDate : Long = 0
    private var startDateApi = ""
    private var endDateApi = ""
    private var gender = ""
    private var rentType = ""
    private var receivedDuration = -1
    private var pricePerDay = -1
    private var itemName = ""
    private var pricingId = -1
    private var category = ""

    companion object {
        const val KEY_ROOM_ID = "key_room_id"
        const val KEY_GUESTHOUSE_ID = "key_guesthouse_id"
        const val EXTRA_START_DATE = "extra_start_date"
        const val EXTRA_END_DATE = "extra_end_date"
        const val EXTRA_GENDER = "extra_gender"
        const val KEY_DURATION = "key_duration"
        const val EXTRA_CATEGORY = "extra_category"
        const val KEY_CITYHALL_ID = "key_cityhall_id"
        const val EXTRA_RENT_TYPE = "extra_rent_type"
        const val EXTRA_PRICE_PER_NIGHT = "extra_price_per_night"
        const val EXTRA_ITEM_NAME = "extra_item_name"
        const val EXTRA_PRICING_ID = "extra_pricing_id"
        const val EXTRA_SLOT = "extra_slot"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFillDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Pesan"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val roomId = intent.getIntExtra(KEY_ROOM_ID, -1)
        val guesthouseId = intent.getIntExtra(KEY_GUESTHOUSE_ID, -1)
        val cityHallId = intent.getIntExtra(KEY_CITYHALL_ID, -1)
        category = intent.getStringExtra(EXTRA_CATEGORY) ?: "Default Query"
        rentType = intent.getStringExtra(EXTRA_RENT_TYPE) ?: "Default Query"
        gender = intent.getStringExtra(EXTRA_GENDER) ?: "Default Query"
        itemName = intent.getStringExtra(EXTRA_ITEM_NAME) ?: "Default Query"
        startDate = intent.getLongExtra(EXTRA_START_DATE, -1)
        endDate = intent.getLongExtra(EXTRA_END_DATE, -1)
        receivedDuration = intent.getIntExtra(KEY_DURATION, -1)
        pricePerDay = intent.getIntExtra(EXTRA_PRICE_PER_NIGHT, -1)
        pricingId = intent.getIntExtra(EXTRA_PRICING_ID, -1)

        val ymf = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))

        startDateApi = ymf.format(Date(startDate))
        endDateApi = ymf.format(Date(endDate))

        setupData()

        if(category == "Mess"){
            setupUiRentMess(roomId, guesthouseId)
        }else if (category == "Gedung"){
            setupUiRentCityhall(cityHallId)
        }

    }


    private fun setupData() {
        val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale("id", "ID"))

        binding.checkInDate.text = sdf.format(Date(startDate))
        binding.checkOutDate.text = sdf.format(Date(endDate))
        binding.renterType.text = rentType
        binding.pricePerNight.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(pricePerDay))

        binding.priceRange.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(pricePerDay * receivedDuration))

        authViewModel.getSession().observe(this) {user ->
            if(user.isLogin){
                binding.userName.text = user.fullname
                binding.userEmail.text = user.email
                binding.userPhone.text = user.phone_number
                if(user.gender == "perempuan"){
                    binding.userGender.text = "Perempuan"
                }else{
                    binding.userGender.text = "Laki-laki"
                }
            }
        }

    }

    private fun setupUiRentMess(roomId: Int, guesthouseId: Int) {
        binding.itemTitle.text = getString(R.string.mess_kota)
        binding.checkInTime.visibility = View.VISIBLE
        binding.checkOutTime.visibility = View.VISIBLE
        binding.roomTitle.visibility = View.VISIBLE
        binding.roomType.visibility = View.VISIBLE
        binding.roomType.text = String.format("$itemName")
        binding.rincianItemName.text = getString(R.string.mess_kota)
        binding.pricePerNightTitle.text = "Harga per malam"
        binding.renterTypeTitle.text = "Kategori Penyewa: "
        binding.rincianItem.text = String.format("Kamar ${itemName}, ${rentType}, ${receivedDuration} malam")
        binding.durationCount.text = String.format("$receivedDuration malam")
        binding.cardKebijakanMess.visibility = View.VISIBLE
        binding.nextButton.setOnClickListener {
            navigateToPaymentGuesthouse()
        }
    }

    private fun setupUiRentCityhall(cityHallId: Int) {
        binding.itemTitle.text = getString(R.string.gedung_adam_malik)
        binding.checkInTitle.text = "Mulai sewa"
        binding.checkOutTitle.text = "Selesai sewa"
        binding.checkInTime.visibility = View.GONE
        binding.checkOutTime.visibility = View.GONE
        binding.roomTitle.visibility = View.GONE
        binding.roomType.visibility = View.GONE
        binding.renterTypeTitle.text = "Kategori Acara: "
        binding.renterType.text = String.format("$rentType")
        binding.rincianItemName.text = getString(R.string.gedung_adam_malik)
        binding.pricePerNightTitle.text = "Harga per hari"
        binding.rincianItem.text = String.format("Acara ${rentType}, ${receivedDuration} hari")
        binding.durationCount.text = String.format("$receivedDuration hari")
        binding.moonIcon.setImageResource(R.drawable.day)
        binding.cardKebijakanGedung.visibility = View.VISIBLE
        binding.nextButton.setOnClickListener {
            navigateToPaymentCityHall()
        }
    }

    private fun navigateToPaymentCityHall() {
        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra(EXTRA_PRICING_ID, pricingId)
        intent.putExtra(EXTRA_SLOT, 1)
        intent.putExtra(EXTRA_START_DATE, startDateApi)
        intent.putExtra(EXTRA_END_DATE, endDateApi)
        intent.putExtra(EXTRA_GENDER, gender)
        intent.putExtra(KEY_DURATION, receivedDuration)
        intent.putExtra(EXTRA_CATEGORY, category)
        startActivity(intent)
        finish()
    }

    private fun navigateToPaymentGuesthouse() {
        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra(EXTRA_PRICING_ID, pricingId)
        intent.putExtra(EXTRA_SLOT, 1)
        intent.putExtra(EXTRA_START_DATE, startDateApi)
        intent.putExtra(EXTRA_END_DATE, endDateApi)
        intent.putExtra(EXTRA_GENDER, gender)
        intent.putExtra(KEY_DURATION, receivedDuration)
        intent.putExtra(EXTRA_CATEGORY, category)
        startActivity(intent)
        finish()
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
}