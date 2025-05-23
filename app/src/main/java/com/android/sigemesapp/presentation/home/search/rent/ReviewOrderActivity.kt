package com.android.sigemesapp.presentation.home.search.rent

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.sigemesapp.R
import com.android.sigemesapp.databinding.ActivityReviewOrderBinding
import com.android.sigemesapp.presentation.MainViewModel
import com.android.sigemesapp.presentation.auth.AuthViewModel
import com.android.sigemesapp.presentation.home.search.rent.payment.PaymentActivity
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_CATEGORY
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_END_DATE
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_FLOW
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_GENDER
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_ITEM_NAME
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_PRICE_PER_NIGHT
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_PRICING_ID
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_RENT_TYPE
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_SLOT
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_START_DATE
import com.android.sigemesapp.utils.ConstantKeys.KEY_CITYHALL_ID
import com.android.sigemesapp.utils.ConstantKeys.KEY_DURATION
import com.android.sigemesapp.utils.ConstantKeys.KEY_GUESTHOUSE_ID
import com.android.sigemesapp.utils.ConstantKeys.KEY_ROOM_ID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewOrderBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
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
    private var slot = -1
    private var roomId = -1
    private var guesthouseId = -1
    private var cityHallId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Pesan"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        roomId = intent.getIntExtra(KEY_ROOM_ID, -1)
        guesthouseId = intent.getIntExtra(KEY_GUESTHOUSE_ID, -1)
        cityHallId = intent.getIntExtra(KEY_CITYHALL_ID, -1)
        category = intent.getStringExtra(EXTRA_CATEGORY) ?: "Default Query"
        rentType = intent.getStringExtra(EXTRA_RENT_TYPE) ?: "Default Query"
        gender = intent.getStringExtra(EXTRA_GENDER) ?: "Default Query"
        itemName = intent.getStringExtra(EXTRA_ITEM_NAME) ?: "Default Query"
        startDate = intent.getLongExtra(EXTRA_START_DATE, -1)
        endDate = intent.getLongExtra(EXTRA_END_DATE, -1)
        receivedDuration = intent.getIntExtra(KEY_DURATION, -1)
        pricePerDay = intent.getIntExtra(EXTRA_PRICE_PER_NIGHT, -1)
        pricingId = intent.getIntExtra(EXTRA_PRICING_ID, -1)
        slot = intent.getIntExtra(EXTRA_SLOT, -1)

        startDateApi = mainViewModel.formatDate(startDate)
        endDateApi = mainViewModel.formatDate(endDate)

        setupData()

        if(category == "Mess"){
            setupUiRentMess()
        }else if (category == "Gedung"){
            setupUiRentCityhall()
        }

    }

    private fun setupData() {
        binding.checkInDate.text = mainViewModel.formatDate2(startDate)
        binding.checkOutDate.text = mainViewModel.formatDate2(endDate)
        binding.renterType.text = rentType
        binding.pricePerNight.text = mainViewModel.formatPrice(pricePerDay)

        val totalPrice = mainViewModel.calculateTotalPrice(pricePerDay, receivedDuration)
        binding.priceRange.text = mainViewModel.formatPrice(totalPrice)

        authViewModel.getSession().observe(this) {user ->
            if(user.isLogin){
                binding.userName.text = user.fullname
                binding.userEmail.text = user.email
                binding.userPhone.text = user.phone_number
                if(user.gender == "perempuan"){
                    binding.userGender.text = getString(R.string.female)
                }else{
                    binding.userGender.text = getString(R.string.male)
                }
            }
        }

    }

    private fun setupUiRentMess() {
        binding.itemTitle.text = getString(R.string.mess_kota)
        binding.checkInTime.visibility = View.VISIBLE
        binding.checkOutTime.visibility = View.VISIBLE
        binding.roomTitle.visibility = View.VISIBLE
        binding.roomType.visibility = View.VISIBLE
        binding.roomType.text = String.format(itemName)
        binding.rincianItemName.text = getString(R.string.mess_kota)
        binding.pricePerNightTitle.text = String.format("Harga per malam")
        binding.renterTypeTitle.text = String.format("Kategori Penyewa: ")
        binding.rincianItem.text = String.format("Kamar ${itemName}, ${rentType}, $receivedDuration malam")
        binding.durationCount.text = String.format("$receivedDuration malam")
        binding.cardKebijakanMess.visibility = View.VISIBLE
        binding.nextButton.setOnClickListener {
            navigateToPaymentGuesthouse()
        }
    }

    private fun setupUiRentCityhall() {
        binding.itemTitle.text = getString(R.string.gedung_adam_malik)
        binding.checkInTitle.text = String.format("Mulai sewa")
        binding.checkOutTitle.text = String.format("Selesai sewa")
        binding.checkInTime.visibility = View.GONE
        binding.checkOutTime.visibility = View.GONE
        binding.roomTitle.visibility = View.GONE
        binding.roomType.visibility = View.GONE
        binding.renterTypeTitle.text = String.format("Kategori Acara: ")
        binding.renterType.text = String.format(rentType)
        binding.rincianItemName.text = getString(R.string.gedung_adam_malik)
        binding.pricePerNightTitle.text = String.format("Harga per hari")
        binding.rincianItem.text = String.format("Acara ${rentType}, $receivedDuration hari")
        binding.durationCount.text = String.format("$receivedDuration hari")
        binding.moonIcon.setImageResource(R.drawable.day)
        binding.cardKebijakanGedung.visibility = View.VISIBLE
        binding.nextButton.setOnClickListener {
            navigateToPaymentCityHall()
        }
    }

    private fun navigateToPaymentCityHall() {
        val intent = Intent(this, PaymentActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(EXTRA_PRICING_ID, pricingId)
            putExtra(EXTRA_SLOT, 1)
            putExtra(EXTRA_START_DATE, startDateApi)
            putExtra(EXTRA_END_DATE, endDateApi)
            putExtra(EXTRA_GENDER, gender)
            putExtra(KEY_DURATION, receivedDuration)
            putExtra(EXTRA_CATEGORY, category)
            putExtra(EXTRA_FLOW, "booking")
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToPaymentGuesthouse() {
        val intent = Intent(this, PaymentActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(EXTRA_PRICING_ID, pricingId)
            putExtra(EXTRA_SLOT, slot)
            putExtra(EXTRA_START_DATE, startDateApi)
            putExtra(EXTRA_END_DATE, endDateApi)
            putExtra(EXTRA_GENDER, gender)
            putExtra(KEY_DURATION, receivedDuration)
            putExtra(EXTRA_CATEGORY, category)
            putExtra(EXTRA_FLOW, "booking")
        }
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