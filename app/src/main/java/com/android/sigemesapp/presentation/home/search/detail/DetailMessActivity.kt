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
import com.android.sigemesapp.data.model.Guesthouse
import com.android.sigemesapp.data.model.Review
import com.android.sigemesapp.data.model.RoomItem
import com.android.sigemesapp.databinding.ActivityDetailMessBinding
import com.android.sigemesapp.presentation.MainViewModel
import com.android.sigemesapp.presentation.home.search.adapter.FacilityAdapter
import com.android.sigemesapp.presentation.home.search.adapter.PhotoAdapter
import com.android.sigemesapp.presentation.home.search.detail.about.AboutActivity
import com.android.sigemesapp.presentation.home.search.detail.adapter.PhotoRoomAdapter
import com.android.sigemesapp.presentation.home.search.detail.review.ReviewActivity
import com.android.sigemesapp.presentation.home.search.detail.review.ReviewViewModel
import com.android.sigemesapp.presentation.home.search.rent.ReviewOrderActivity
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_CATEGORY
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_END_DATE
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_GENDER
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_ITEM_NAME
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_PRICE_PER_NIGHT
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_PRICING_ID
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_RENT_TYPE
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_SLOT
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_START_DATE
import com.android.sigemesapp.utils.ConstantKeys.KEY_DURATION
import com.android.sigemesapp.utils.ConstantKeys.KEY_GUESTHOUSE_ID
import com.android.sigemesapp.utils.ConstantKeys.KEY_ROOM_ID
import com.android.sigemesapp.utils.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailMessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailMessBinding
    private val detailViewModel: DetailViewModel by viewModels()
    private val reviewViewModel: ReviewViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
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
    private var slot = -1
    private var roomId = -1
    private var guesthouseId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        roomId = intent.getIntExtra(KEY_ROOM_ID, -1)
        guesthouseId = intent.getIntExtra(KEY_GUESTHOUSE_ID, -1)
        gender = intent.getStringExtra(EXTRA_GENDER) ?: "Default Query"
        startDate = intent.getLongExtra(EXTRA_START_DATE, -1)
        endDate = intent.getLongExtra(EXTRA_END_DATE, -1)
        receivedDuration = mainViewModel.calculateNights(startDate, endDate)
        startDateApi = mainViewModel.formatDate(startDate)
        endDateApi = mainViewModel.formatDate(endDate)
        observeGuesthouseData()
        setupReviewCount()
        observeRoomData()
        setupAction()

    }

    private fun setupAction() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.checkoutButton.setOnClickListener {
            val intent = Intent(this, ReviewOrderActivity::class.java)
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
            intent.putExtra(EXTRA_SLOT, slot)
            startActivity(intent)
        }

        binding.cardUlasan.setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java)
            intent.putExtra(KEY_ROOM_ID, roomId)
            intent.putExtra(KEY_GUESTHOUSE_ID, guesthouseId)
            startActivity(intent)
        }

        binding.cardAbout.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            intent.putExtra(KEY_ROOM_ID, roomId)
            intent.putExtra(KEY_GUESTHOUSE_ID, guesthouseId)
            intent.putExtra(EXTRA_START_DATE, startDate)
            intent.putExtra(EXTRA_END_DATE, endDate)
            intent.putExtra(EXTRA_GENDER, gender)
            startActivity(intent)
        }
    }

    private fun observeGuesthouseData() {
        detailViewModel.getDetailGuesthouse(guesthouseId)

        detailViewModel.guesthouseResult.observe(this){ result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvPhotoMess.visibility = View.GONE
                    binding.cardUlasan.visibility = View.GONE
                    binding.generalInfoCard.visibility = View.GONE
                    binding.cardAbout.visibility = View.GONE
                }
                is Result.Success -> {
                    val guesthouse = result.data
                    setGuesthouseDetail(guesthouse)
                    binding.progressBar.visibility = View.GONE
                    binding.rvPhotoMess.visibility = View.VISIBLE
                    binding.cardUlasan.visibility = View.VISIBLE
                    binding.generalInfoCard.visibility = View.VISIBLE
                    binding.cardAbout.visibility = View.VISIBLE
                }
                is Result.Error -> {
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeRoomData() {
        detailViewModel.getDetailGuesthouseRoom(guesthouseId, roomId, startDateApi, endDateApi, gender)

        detailViewModel.detailRoom.observe(this){ result ->
            when (result) {
                is Result.Loading -> {
                    binding.cardDetailRoom.visibility = View.GONE
                }
                is Result.Success -> {
                    val room = result.data
                    setRoomDetail(room)
                    binding.cardDetailRoom.visibility = View.VISIBLE
                }
                is Result.Error -> {
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun setGuesthouseDetail(guesthouse: Guesthouse) {
        binding.rvPhotoMess.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val photoUrls = guesthouse.guesthouseMedia.map { it.url }
        val photoAdapter = PhotoAdapter(photoUrls)
        binding.rvPhotoMess.adapter = photoAdapter
        binding.lodgingNameText.text = guesthouse.name
        binding.facilities.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val facilityList = mainViewModel.extractFacilities(guesthouse.facilities)
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

    private fun setRoomDetail(room: RoomItem) {
        binding.rvPhoto.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val roomPhotoUrls = room.media.map { it.url }
        val roomPhotoAdapter = PhotoRoomAdapter(this, roomPhotoUrls)
        binding.rvPhoto.adapter = roomPhotoAdapter
        binding.roomType.text = room.name
        itemName = room.name
        binding.totalSlotCount.text = detailViewModel.formatString(room.totalSlot)
        binding.totalAvailableSlotCount.text = detailViewModel.formatString(room.availableSlot)
        binding.rincianRoom.text = room.name
        binding.fasiliasKamarText.text = detailViewModel.formatFacilityList(room.facilities)
        binding.durationText.text = detailViewModel.formatDuration(receivedDuration)

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
        val defaultIndex = detailViewModel.getDefaultPricingRoomIndex(pricing)

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
            binding.renterType.text = pricing[index].retributionType
            rentType = pricing[index].retributionType
            slot = detailViewModel.calculateSlot(rentType, room.totalSlot)
            binding.pricePerNight.text = detailViewModel.formatPrice(pricePerDay)
            pricingId = pricing[index].id
        }

        for (i in pricing.indices) {
            checkboxes[i].text = pricing[i].retributionType
            checkboxes[i].isChecked = i == defaultIndex
            cardViews[i].strokeColor = if (i == defaultIndex) selectedStrokeColor else defaultStrokeColor

            if (room.availableSlot != room.totalSlot && (pricing[i].retributionType.contains("kamar", ignoreCase = true) || pricing[i].retributionType.contains("pejabat", ignoreCase = true))) {
                checkboxes[i].isEnabled = false
                binding.roomsRentNotAvailable.visibility = View.VISIBLE
                if(room.name.contains("vip", ignoreCase = true)){
                    binding.roomsRentNotAvailable2.visibility = View.VISIBLE
                }
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
    }

    private fun setupReviewCount() {
        reviewViewModel.getGuesthouseRoomsReviewss(guesthouseId, roomId)
        reviewViewModel.allReviewResult.observe(this) { result ->
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

    private fun setGuesthouseRoomReviews(guesthouseRoomReviews: List<Review>) {
        val totalRating = reviewViewModel.calculateAverageRatingText(guesthouseRoomReviews)
        val totalItems = reviewViewModel.calculateTotalReviewText(guesthouseRoomReviews.size)
        binding.textReviewCount.text = totalRating
        binding.totalUlasan.text = totalItems
        binding.textReviewCount2.text = totalRating
        binding.totalUlasan2.text = totalItems
    }

}