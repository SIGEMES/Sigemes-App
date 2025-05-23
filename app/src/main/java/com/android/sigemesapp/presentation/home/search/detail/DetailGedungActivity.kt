package com.android.sigemesapp.presentation.home.search.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.sigemesapp.R
import com.android.sigemesapp.data.model.CityHall
import com.android.sigemesapp.data.model.Review
import com.android.sigemesapp.databinding.ActivityDetailGedungBinding
import com.android.sigemesapp.presentation.MainViewModel
import com.android.sigemesapp.presentation.home.search.adapter.FacilityAdapter
import com.android.sigemesapp.presentation.home.search.adapter.PhotoAdapter
import com.android.sigemesapp.presentation.home.search.detail.about.AboutActivity
import com.android.sigemesapp.presentation.home.search.detail.review.ReviewActivity
import com.android.sigemesapp.presentation.home.search.detail.review.ReviewViewModel
import com.android.sigemesapp.presentation.home.search.rent.ReviewOrderActivity
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_CATEGORY
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_END_DATE
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_GENDER
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_PRICE_PER_NIGHT
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_PRICING_ID
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_RENT_TYPE
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_START_DATE
import com.android.sigemesapp.utils.ConstantKeys.KEY_CITYHALL_ID
import com.android.sigemesapp.utils.ConstantKeys.KEY_DURATION
import com.android.sigemesapp.utils.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailGedungActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailGedungBinding
    private val detailViewModel: DetailViewModel by viewModels()
    private val reviewViewModel: ReviewViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    private var startDate : Long = 0
    private var endDate : Long = 0
    private var startDateApi = ""
    private var endDateApi = ""
    private var receivedDuration = -1
    private var rentType = ""
    private var pricePerNight = -1
    private var pricingId = -1
    private var gender = ""
    private var cityHallId = -1
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailGedungBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        cityHallId = intent.getIntExtra(KEY_CITYHALL_ID, -1)
        startDate = intent.getLongExtra(EXTRA_START_DATE, -1)
        endDate = intent.getLongExtra(EXTRA_END_DATE, -1)
        gender = intent.getStringExtra(EXTRA_GENDER) ?: "Default Query"
        receivedDuration = mainViewModel.calculateDays(startDate, endDate)
        startDateApi = mainViewModel.formatDate(startDate)
        endDateApi = mainViewModel.formatDate(endDate)
        setupAction()
        observeCityHallData()
        setupReviewCount()
    }

    private fun observeCityHallData() {
        detailViewModel.getCityHall(cityHallId, startDateApi, endDateApi)

        detailViewModel.detailCityHall.observe(this){ result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvPhotoCityhall.visibility = View.GONE
                    binding.cardGeneralInfo.visibility = View.GONE
                    binding.cardAbout.visibility = View.GONE
                    binding.cardUlasan.visibility = View.GONE
                    binding.cardSelectEventType.visibility = View.GONE
                    binding.cardRincianHarga.visibility = View.GONE
                    binding.checkoutButton.visibility = View.GONE
                }
                is Result.Success -> {
                    val cityHall = result.data
                    setCityHallDetail(cityHall)
                    binding.progressBar.visibility = View.GONE
                    binding.rvPhotoCityhall.visibility = View.VISIBLE
                    binding.cardGeneralInfo.visibility = View.VISIBLE
                    binding.cardAbout.visibility = View.VISIBLE
                    binding.cardUlasan.visibility = View.VISIBLE
                    binding.cardSelectEventType.visibility = View.VISIBLE
                    binding.cardRincianHarga.visibility = View.VISIBLE
                    binding.checkoutButton.visibility = View.VISIBLE
                }
                is Result.Error -> {
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupAction() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.checkoutButton.setOnClickListener {
            val intent = Intent(this, ReviewOrderActivity::class.java)
            intent.putExtra(KEY_CITYHALL_ID, cityHallId)
            intent.putExtra(EXTRA_START_DATE, startDate)
            intent.putExtra(EXTRA_END_DATE, endDate)
            intent.putExtra(EXTRA_GENDER, gender)
            intent.putExtra(KEY_DURATION, receivedDuration)
            intent.putExtra(EXTRA_CATEGORY, "Gedung")
            intent.putExtra(EXTRA_RENT_TYPE, rentType)
            intent.putExtra(EXTRA_PRICE_PER_NIGHT, pricePerNight)
            intent.putExtra(EXTRA_PRICING_ID, pricingId)
            startActivity(intent)
        }

        binding.cardUlasan.setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java)
            intent.putExtra(KEY_CITYHALL_ID, cityHallId)
            startActivity(intent)
        }

        binding.cardAbout.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            intent.putExtra(KEY_CITYHALL_ID, cityHallId)
            intent.putExtra(EXTRA_START_DATE, startDate)
            intent.putExtra(EXTRA_END_DATE, endDate)
            startActivity(intent)
        }
    }

    private fun setCityHallDetail(cityHall: CityHall) {
        binding.rvPhotoCityhall.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val cityHallPhotoUrls = cityHall.media.map { it.url }
        val photoAdapter = PhotoAdapter(cityHallPhotoUrls)
        binding.rvPhotoCityhall.adapter = photoAdapter
        binding.rincianPemesanan.text = cityHall.name
        binding.buildingName.text = cityHall.name
        binding.capacity.text = detailViewModel.formatCapacity(cityHall.peopleCapacity)
        binding.luas.text = detailViewModel.formatArea(cityHall.areaM2)
        binding.durationText.text = detailViewModel.formatDuration(receivedDuration)
        binding.tentangGedungText.text = cityHall.description

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
        val defaultIndex = detailViewModel.getDefaultPricingIndex(pricing)

        fun selectOption(index: Int) {
            checkboxes.forEachIndexed { i, checkbox ->
                val isSelected = i == index
                checkbox.isChecked = isSelected
                cardViews[i].strokeColor = if (isSelected) selectedStrokeColor else defaultStrokeColor
            }

            val pricePerDay = pricing[index].pricePerDay
            pricePerNight = pricePerDay
            val totalPrice = detailViewModel.calculateTotalPrice(pricePerDay, receivedDuration)
            binding.priceRange.text = detailViewModel.formatPrice(totalPrice)
            binding.eventType.text = pricing[index].activityType
            rentType = pricing[index].activityType
            pricingId = pricing[index].id
            binding.pricePerDay.text = detailViewModel.formatPrice(pricePerDay)
            val facility = mainViewModel.extractFacilities(cityHall.pricing[index].facilities)
            binding.fasilitasText.text = detailViewModel.formatFacilityList(cityHall.pricing[index].facilities)
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

    }

    private fun setupReviewCount() {
        reviewViewModel.getCityHallReviewss(cityHallId)
        reviewViewModel.allReviewResult.observe(this) { result ->
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

    private fun setCityHallReviews(cityHallReviews: List<Review>) {
        val totalRating = reviewViewModel.calculateAverageRatingText(cityHallReviews)
        val totalItems = reviewViewModel.calculateTotalReviewText(cityHallReviews.size)
        binding.textReviewCount.text = totalRating
        binding.totalUlasan.text = totalItems
        binding.textReviewCount2.text = totalRating
        binding.totalUlasan2.text = totalItems
    }
}