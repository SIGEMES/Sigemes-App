package com.android.sigemesapp.presentation.home.search.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.sigemesapp.R
import com.android.sigemesapp.data.source.remote.response.DetailRoom
import com.android.sigemesapp.data.source.remote.response.GuesthouseData
import com.android.sigemesapp.data.source.remote.response.GuesthouseRoomReviews
import com.android.sigemesapp.databinding.ActivityDetailMessBinding
import com.android.sigemesapp.presentation.home.search.adapter.FacilityAdapter
import com.android.sigemesapp.presentation.home.search.adapter.PhotoAdapter
import com.android.sigemesapp.presentation.home.search.detail.DetailGedungActivity.Companion.EXTRA_PRICING_ID
import com.android.sigemesapp.presentation.home.search.detail.about.AboutActivity
import com.android.sigemesapp.presentation.home.search.detail.adapter.PhotoRoomAdapter
import com.android.sigemesapp.presentation.home.search.detail.review.ReviewActivity
import com.android.sigemesapp.presentation.home.search.rent.FillDataActivity
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.calculateNights
import com.android.sigemesapp.utils.extractFacilities
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class DetailMessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailMessBinding
    private val detailViewModel: DetailViewModel by viewModels()
    private var startDate : Long = 0
    private var endDate : Long = 0
    private var startDateApi = ""
    private var endDateApi = ""
    private var gender = ""
    private var receivedDuration = -1
    private var rentType = ""
    private var pricePerNight = -1
    private var itemName = ""
    private var pricingId = -1

    companion object {
        const val KEY_ROOM_ID = "key_room_id"
        const val KEY_GUESTHOUSE_ID = "key_guesthouse_id"
        const val EXTRA_START_DATE = "extra_start_date"
        const val EXTRA_END_DATE = "extra_end_date"
        const val EXTRA_GENDER = "extra_gender"
        const val KEY_DURATION = "key_duration"
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_RENT_TYPE = "extra_rent_type"
        const val EXTRA_PRICE_PER_NIGHT = "extra_price_per_night"
        const val EXTRA_ITEM_NAME = "extra_item_name"
        const val EXTRA_PRICING_ID = "extra_pricing_id"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val roomId = intent.getIntExtra(KEY_ROOM_ID, -1)
        val guesthouseId = intent.getIntExtra(KEY_GUESTHOUSE_ID, -1)
        gender = intent.getStringExtra(EXTRA_GENDER) ?: "Default Query"
        startDate = intent.getLongExtra(EXTRA_START_DATE, -1)
        endDate = intent.getLongExtra(EXTRA_END_DATE, -1)

        receivedDuration = calculateNights(startDate, endDate)

        val ymf = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))

        startDateApi = ymf.format(Date(startDate))
        endDateApi = ymf.format(Date(endDate))

        setupReviewCount(guesthouseId, roomId)
        observeGuesthouseData(guesthouseId)
        observeRoomData(guesthouseId, roomId)
        setupAction()

    }

    private fun setupAction() {
        binding.backButton.setOnClickListener {
            finish()
        }

    }

    private fun observeGuesthouseData(guesthouseId: Int) {
        detailViewModel.getDetailGuesthouse(guesthouseId)

        detailViewModel.guesthouseResult.observe(this){ result ->
            when (result) {
                is Result.Loading -> {

                }
                is Result.Success -> {
                    val guesthouse = result.data
                    setGuesthouseDetail(guesthouse)
                }
                is Result.Error -> {
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun observeRoomData(guesthouseId: Int, roomId: Int) {
        detailViewModel.getDetailGuesthouseRoom(guesthouseId, roomId, startDateApi, endDateApi, gender)

        detailViewModel.detailRoom.observe(this){ result ->
            when (result) {
                is Result.Loading -> {

                }
                is Result.Success -> {
                    val room = result.data
                    setRoomDetail(room, guesthouseId, roomId)
                }
                is Result.Error -> {
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun setGuesthouseDetail(guesthouse: GuesthouseData) {
        binding.rvPhotoMess.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val photoUrls = guesthouse.guesthouseMedia.map { it.url }
        val photoAdapter = PhotoAdapter(photoUrls)
        binding.rvPhotoMess.adapter = photoAdapter

        binding.lodgingNameText.text = guesthouse.name
        binding.facilities.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val facilityList = extractFacilities(guesthouse.facilities)
        val facilityAdapter = FacilityAdapter(facilityList)
        binding.facilities.adapter = facilityAdapter

        binding.aboutDesc.text = guesthouse.description

        binding.rincianGuesthouse.text = String.format(guesthouse.name + ",")

        binding.maps.setOnClickListener {
            val latitude = guesthouse.latitude
            val longitude = guesthouse.longitude
            val gmmIntentUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
            val intent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            startActivity(intent)
        }

    }

    private fun setRoomDetail(room: DetailRoom, guesthouseId: Int, roomId: Int) {
        binding.rvPhoto.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val roomPhotoUrls = room.media.map { it.url }
        val roomPhotoAdapter = PhotoRoomAdapter(this, roomPhotoUrls)
        binding.rvPhoto.adapter = roomPhotoAdapter

        binding.roomType.text = room.name
        itemName = room.name
        binding.totalSlotCount.text = String.format(room.totalSlot.toString())
        binding.totalAvailableSlotCount.text = String.format(room.availableSlot.toString())
        binding.rincianRoom.text = room.name

        binding.cardUlasan.setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java)
            intent.putExtra(KEY_ROOM_ID, roomId)
            intent.putExtra(KEY_GUESTHOUSE_ID, guesthouseId)
            startActivity(intent)
        }

        binding.cardAbout.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            intent.putExtra(AboutActivity.KEY_ROOM_ID, room.id)
            intent.putExtra(AboutActivity.KEY_GUESTHOUSE_ID, room.guesthouseId)
            intent.putExtra(AboutActivity.EXTRA_START_DATE, startDate)
            intent.putExtra(AboutActivity.EXTRA_END_DATE, endDate)
            intent.putExtra(AboutActivity.EXTRA_GENDER, gender)
            Log.e("CreateCityHallReques", "check gender from mess, $gender")
            startActivity(intent)
        }

        val facility = extractFacilities(room.facilities)
        binding.fasiliasKamarText.text = String.format("- " + facility.joinToString("\n- "))

        binding.durationText.text = String.format("$receivedDuration malam")

        val checkboxes = listOf(
            binding.checkOption1,
            binding.checkOption2,
            binding.checkOption3,
            binding.checkOption4
        )

        val cardViews = listOf(
            binding.renterOption1,
            binding.renterOption2,
            binding.renterOption3,
            binding.renterOption4
        )

        val pricing = room.pricing

        val defaultStrokeColor = ContextCompat.getColor(this, R.color.onGray)
        val selectedStrokeColor = ContextCompat.getColor(this, R.color.babyblue)
        val disabledColor = ContextCompat.getColor(this, R.color.onGray)

        val defaultIndex = pricing.indexOfFirst { it.retributionType == "Umum" }.takeIf { it != -1 } ?: 0
        rentType = "Umum"

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

            binding.renterType.text = pricing[index].retributionType
            rentType = pricing[index].retributionType

            binding.pricePerNight.text = String.format("Rp %s",
                NumberFormat.getNumberInstance(Locale("id", "ID")).format(pricePerDay))

            pricingId = pricing[index].id
        }

        for (i in pricing.indices) {
            checkboxes[i].text = pricing[i].retributionType
            checkboxes[i].isChecked = i == defaultIndex
            cardViews[i].strokeColor = if (i == defaultIndex) selectedStrokeColor else defaultStrokeColor

            if (room.availableSlot != room.totalSlot && pricing[i].retributionType == "Khusus Booking 1 Kamar") {
                checkboxes[i].isEnabled = false
                binding.roomsRentNotAvailable.visibility = View.VISIBLE
                checkboxes[i].setTextColor(disabledColor)
                cardViews[i].strokeColor = disabledColor
            } else {
                checkboxes[i].isEnabled = true
                checkboxes[i].setTextColor(ContextCompat.getColor(this, R.color.black))
                cardViews[i].setOnClickListener { selectOption(i) }
                checkboxes[i].setOnClickListener { selectOption(i) }
            }
        }

        selectOption(defaultIndex)

        binding.checkoutButton.setOnClickListener {
            val intent = Intent(this, FillDataActivity::class.java)
            intent.putExtra(KEY_ROOM_ID, roomId)
            intent.putExtra(KEY_GUESTHOUSE_ID, guesthouseId)
            intent.putExtra(EXTRA_START_DATE, startDate)
            intent.putExtra(EXTRA_END_DATE, endDate)
            intent.putExtra(EXTRA_GENDER, gender)
            intent.putExtra(KEY_DURATION, receivedDuration)
            intent.putExtra(EXTRA_CATEGORY, "Mess")
            intent.putExtra(EXTRA_RENT_TYPE, rentType)
            intent.putExtra(EXTRA_PRICE_PER_NIGHT, pricePerNight)
            intent.putExtra(EXTRA_ITEM_NAME, itemName)
            intent.putExtra(EXTRA_PRICING_ID, pricingId)
            startActivity(intent)
        }
    }

    private fun setupReviewCount(guesthouseId: Int, roomId: Int) {
        detailViewModel.getGuesthouseRoomsReviews(guesthouseId, roomId)
        detailViewModel.guesthouseRoomReviews.observe(this) { result ->
            when(result){
                is Result.Loading -> {

                }
                is Result.Success -> {
                    val guesthouseRoomReviews = result.data
                    setGuesthouseRoomReviews(guesthouseRoomReviews)

                }
                is Result.Error -> {

                }
            }
        }
    }

    private fun setGuesthouseRoomReviews(guesthouseRoomReviews: List<GuesthouseRoomReviews>) {
        val totalRating = guesthouseRoomReviews.sumOf { it.rating }
        val totalItems = guesthouseRoomReviews.size

        val averageRating = if (totalItems > 0) totalRating.toDouble() / totalItems else 0

        binding.textReviewCount.text = String.format("$averageRating")
        binding.totalUlasan.text = String.format(" ($totalItems ulasan)")
        binding.textReviewCount2.text = String.format("$averageRating")
        binding.totalUlasan2.text = String.format(" ($totalItems ulasan)")
    }

}