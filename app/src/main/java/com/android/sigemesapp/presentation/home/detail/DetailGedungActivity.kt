package com.android.sigemesapp.presentation.home.detail

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
import com.android.sigemesapp.databinding.ActivityDetailGedungBinding
import com.android.sigemesapp.presentation.home.adapter.FacilityAdapter
import com.android.sigemesapp.presentation.home.adapter.PhotoAdapter
import com.android.sigemesapp.presentation.home.detail.about.AboutActivity
import com.android.sigemesapp.presentation.home.detail.adapter.PhotoRoomAdapter
import com.android.sigemesapp.presentation.home.detail.review.ReviewActivity
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.extractFacilities
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class DetailGedungActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailGedungBinding
    private val detailViewModel: DetailViewModel by viewModels()

    companion object {
        const val KEY_CITYHALL_ID = "key_cityhall_id"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailGedungBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val cityHallId = intent.getIntExtra(KEY_CITYHALL_ID, -1)
        val receivedDuration = intent.getIntExtra(DetailMessActivity.KEY_DURATION, 1)

        setupAction()
        observeCityHallData(cityHallId, receivedDuration)
    }

    private fun observeCityHallData(cityHallId: Int, receivedDuration: Int) {
        detailViewModel.getCityHall(cityHallId)

        detailViewModel.detailCityHall.observe(this){ result ->
            when (result) {
                is Result.Loading -> {

                }
                is Result.Success -> {
                    val cityHall = result.data
                    setCityHallDetail(cityHall, receivedDuration)
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
    }

    private fun setCityHallDetail(cityHall: CityHallData, receivedDuration: Int) {
        binding.rvPhotoCityhall.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val cityHallPhotoUrls = cityHall.media.map { it.url }
        val photoAdapter = PhotoAdapter(cityHallPhotoUrls)
        binding.rvPhotoCityhall.adapter = photoAdapter
        binding.rincianPemesanan.text = cityHall.name

        binding.buildingName.text = cityHall.name
        binding.capacity.text = String.format(cityHall.peopleCapacity.toString())
        binding.luas.text = String.format(cityHall.areaM2.toString())
        binding.tentangGedungText.text = cityHall.description

        val durationInDays = receivedDuration ?: 1
        binding.durationText.text = String.format("$durationInDays hari")

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
            val totalPrice = pricePerDay * receivedDuration

            binding.priceRange.text = String.format("Rp %s",
                NumberFormat.getNumberInstance(Locale("id", "ID")).format(totalPrice))

            binding.eventType.text = pricing[index].activityType

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
            intent.putExtra(ReviewActivity.KEY_CITYHALL_NAME, cityHall.name)
            startActivity(intent)
        }

        binding.cardAbout.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            intent.putExtra(AboutActivity.KEY_CITYHALL_ID, cityHall.id)
            startActivity(intent)
        }

    }
}