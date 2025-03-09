package com.android.sigemesapp.presentation.home.search.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.sigemesapp.R
import com.android.sigemesapp.data.source.remote.response.CityHallData
import com.android.sigemesapp.data.source.remote.response.CityHallReviews
import com.android.sigemesapp.databinding.ActivityDetailGedungBinding
import com.android.sigemesapp.presentation.home.search.adapter.FacilityAdapter
import com.android.sigemesapp.presentation.home.search.adapter.PhotoAdapter
import com.android.sigemesapp.presentation.home.search.detail.about.AboutActivity
import com.android.sigemesapp.presentation.home.search.detail.review.ReviewActivity
import com.android.sigemesapp.presentation.home.search.detail.review.ReviewViewModel
import com.android.sigemesapp.presentation.home.search.rent.FillDataActivity
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.calculateDays
import com.android.sigemesapp.utils.extractFacilities
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class DetailGedungActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailGedungBinding
    private val detailViewModel: DetailViewModel by viewModels()
    private val reviewViewModel: ReviewViewModel by viewModels()
    private var startDate : Long = 0
    private var endDate : Long = 0
    private var startDateApi = ""
    private var endDateApi = ""
    private var receivedDuration = -1
    private var rentType = ""
    private var pricePerNight = -1
    private var pricingId = -1
    private var gender = ""

    companion object {
        const val KEY_CITYHALL_ID = "key_cityhall_id"
        const val EXTRA_START_DATE = "extra_start_date"
        const val EXTRA_END_DATE = "extra_end_date"
        const val EXTRA_GENDER = "extra_gender"
        const val KEY_DURATION = "key_duration"
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_RENT_TYPE = "extra_rent_type"
        const val EXTRA_PRICE_PER_NIGHT = "extra_price_per_night"
        const val EXTRA_PRICING_ID = "extra_pricing_id"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailGedungBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val cityHallId = intent.getIntExtra(KEY_CITYHALL_ID, -1)
        startDate = intent.getLongExtra(EXTRA_START_DATE, -1)
        endDate = intent.getLongExtra(EXTRA_END_DATE, -1)
        gender = intent.getStringExtra(EXTRA_GENDER) ?: "Default Query"

        receivedDuration = calculateDays(startDate, endDate)

        val ymf = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))

        startDateApi = ymf.format(Date(startDate))
        endDateApi = ymf.format(Date(endDate))

        setupAction(cityHallId)
        observeCityHallData(cityHallId)
        setupReviewCount(cityHallId)
    }

    private fun observeCityHallData(cityHallId: Int) {
        detailViewModel.getCityHall(cityHallId, startDateApi, endDateApi)

        detailViewModel.detailCityHall.observe(this){ result ->
            when (result) {
                is Result.Loading -> {

                }
                is Result.Success -> {
                    val cityHall = result.data
                    setCityHallDetail(cityHall)
                }
                is Result.Error -> {
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupAction(cityHallId: Int) {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.checkoutButton.setOnClickListener {
            val intent = Intent(this, FillDataActivity::class.java)
            intent.putExtra(KEY_CITYHALL_ID, cityHallId)
            intent.putExtra(EXTRA_START_DATE, startDate)
            intent.putExtra(EXTRA_END_DATE, endDate)
            intent.putExtra(EXTRA_GENDER, gender)
            intent.putExtra(KEY_DURATION, receivedDuration)
            Log.e("Durations1", "durations $receivedDuration")
            intent.putExtra(EXTRA_CATEGORY, "Gedung")
            intent.putExtra(EXTRA_RENT_TYPE, rentType)
            intent.putExtra(EXTRA_PRICE_PER_NIGHT, pricePerNight)
            intent.putExtra(EXTRA_PRICING_ID, pricingId)
            startActivity(intent)
        }
    }

    private fun setCityHallDetail(cityHall: CityHallData) {
        binding.rvPhotoCityhall.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val cityHallPhotoUrls = cityHall.media.map { it.url }
        val photoAdapter = PhotoAdapter(cityHallPhotoUrls)
        binding.rvPhotoCityhall.adapter = photoAdapter
        binding.rincianPemesanan.text = cityHall.name

        binding.buildingName.text = cityHall.name
        binding.capacity.text = String.format("${cityHall.peopleCapacity} orang")
        binding.luas.text = String.format("${cityHall.areaM2} m²")
        binding.tentangGedungText.text = cityHall.description

        binding.durationText.text = String.format("$receivedDuration hari")

        val checkboxes = listOf(
            binding.checkOption1,
            binding.checkOption2,
            binding.checkOption3
        )

        val cardViews = listOf(
            binding.eventOption1,
            binding.eventOption2,
            binding.eventOption3
        )

        val pricing = cityHall.pricing

        val defaultStrokeColor = ContextCompat.getColor(this, R.color.onGray)
        val selectedStrokeColor = ContextCompat.getColor(this, R.color.babyblue)

        val defaultIndex = pricing.indexOfFirst { it.activityType == "Komersial" }.takeIf { it != -1 } ?: 0

        fun selectOption(index: Int) {
            checkboxes.forEachIndexed { i, checkbox ->
                val isSelected = i == index
                checkbox.isChecked = isSelected
                cardViews[i].strokeColor = if (isSelected) selectedStrokeColor else defaultStrokeColor
            }

            val pricePerDay = pricing[index].pricePerDay
            pricePerNight = pricePerDay
            val totalPrice = pricePerDay * receivedDuration

            binding.priceRange.text = String.format("Rp %s",
                NumberFormat.getNumberInstance(Locale("id", "ID")).format(totalPrice))

            binding.eventType.text = pricing[index].activityType
            rentType = pricing[index].activityType
            pricingId = pricing[index].id

            binding.pricePerDay.text = String.format("Rp %s",
                NumberFormat.getNumberInstance(Locale("id", "ID")).format(pricePerDay))

            val facility = extractFacilities(cityHall.pricing[index].facilities)
            binding.fasilitasText.text = String.format("- " + facility.joinToString("\n- "))

            binding.facilities.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val facilityAdapter = FacilityAdapter(facility)
            binding.facilities.adapter = facilityAdapter
        }

        for (i in pricing.indices) {
            checkboxes[i].text = pricing[i].activityType
            checkboxes[i].isChecked = i == defaultIndex
            cardViews[i].strokeColor = if (i == defaultIndex) selectedStrokeColor else defaultStrokeColor

            cardViews[i].setOnClickListener { selectOption(i) }
            checkboxes[i].setOnClickListener { selectOption(i) }
        }

        selectOption(defaultIndex)

        binding.maps.setOnClickListener {
            val latitude = cityHall.latitude
            val longitude = cityHall.longitude
            val gmmIntentUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
            val intent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            startActivity(intent)
        }

        binding.cardUlasan.setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java)
            intent.putExtra(KEY_CITYHALL_ID, cityHall.id)
            startActivity(intent)
        }

        binding.cardAbout.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            intent.putExtra(AboutActivity.KEY_CITYHALL_ID, cityHall.id)
            intent.putExtra(AboutActivity.EXTRA_START_DATE, startDate)
            intent.putExtra(AboutActivity.EXTRA_END_DATE, endDate)
            startActivity(intent)
        }

    }

    private fun setupReviewCount(id: Int) {
        reviewViewModel.getCityHallReviews(id)
        reviewViewModel.cityHallReviews.observe(this) { result ->
            when(result){
                is Result.Loading -> {

                }
                is Result.Success -> {
                    val cityHallReviews = result.data
                    setCityHallReviews(cityHallReviews)

                }
                is Result.Error -> {

                }
            }
        }
    }

    private fun setCityHallReviews(cityHallReviews: List<CityHallReviews>) {
        val totalRating = cityHallReviews.sumOf { it.rating }
        val totalItems = cityHallReviews.size

        val averageRating = if (totalItems > 0) totalRating.toDouble() / totalItems else 0

        binding.textReviewCount.text = String.format("$averageRating")
        binding.totalUlasan.text = String.format(" ($totalItems ulasan)")
        binding.textReviewCount2.text = String.format("$averageRating")
        binding.totalUlasan2.text = String.format(" ($totalItems ulasan)")
    }
}