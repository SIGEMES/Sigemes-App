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
import com.android.sigemesapp.data.source.remote.response.DetailRoom
import com.android.sigemesapp.data.source.remote.response.GuesthouseData
import com.android.sigemesapp.databinding.ActivityDetailMessBinding
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
class DetailMessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailMessBinding
    private val detailViewModel: DetailViewModel by viewModels()

    companion object {
        const val KEY_ROOM_ID = "key_room_id"
        const val KEY_GUESTHOUSE_ID = "key_guesthouse_id"
        const val KEY_DURATION = "key_duration"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val roomId = intent.getIntExtra(KEY_ROOM_ID, -1)
        val guesthouseId = intent.getIntExtra(KEY_GUESTHOUSE_ID, -1)
        val receivedDuration = intent.getIntExtra(KEY_DURATION, 1)

        setupAction()

        observeRoomData(guesthouseId, roomId, receivedDuration)
        observeGuesthouseData(guesthouseId)
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

    private fun observeRoomData(guesthouseId: Int, roomId: Int, receivedDuration: Int) {
        detailViewModel.getDetailGuesthouseRoom(guesthouseId, roomId)

        detailViewModel.detailRoom.observe(this){ result ->
            when (result) {
                is Result.Loading -> {

                }
                is Result.Success -> {
                    val room = result.data
                    setRoomDetail(room, receivedDuration)
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

    private fun setRoomDetail(room: DetailRoom, receivedDuration: Int) {
        binding.rvPhoto.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val roomPhotoUrls = room.media.map { it.url }
        val roomPhotoAdapter = PhotoRoomAdapter(roomPhotoUrls)
        binding.rvPhoto.adapter = roomPhotoAdapter

        binding.roomType.text = room.name
        binding.totalSlotCount.text = String.format(room.totalSlot.toString())
        binding.totalAvailableSlotCount.text = String.format(room.availableSlot.toString())
        binding.rincianRoom.text = room.name

        binding.cardUlasan.setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java)
            intent.putExtra(ReviewActivity.KEY_ROOM_NAME, room.name)
            Log.e("Check", "roomNameMess ${room.name}")
            startActivity(intent)
        }

        binding.cardAbout.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            intent.putExtra(AboutActivity.KEY_ROOM_ID, room.id)
            intent.putExtra(AboutActivity.KEY_GUESTHOUSE_ID, room.guesthouseId)
            startActivity(intent)
        }

        val facility = extractFacilities(room.facilities)
        binding.fasiliasKamarText.text = String.format("- " + facility.joinToString("\n- "))

        val durationInNights = receivedDuration ?: 1
        binding.durationText.text = String.format("$durationInNights malam")

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

        val defaultIndex = pricing.indexOfFirst { it.retributionType == "Umum" }.takeIf { it != -1 } ?: 0

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

            binding.renterType.text = pricing[index].retributionType
        }

        for (i in pricing.indices) {
            checkboxes[i].text = pricing[i].retributionType
            checkboxes[i].isChecked = i == defaultIndex
            cardViews[i].strokeColor = if (i == defaultIndex) selectedStrokeColor else defaultStrokeColor

            cardViews[i].setOnClickListener { selectOption(i) }
            checkboxes[i].setOnClickListener { selectOption(i) }
        }

        selectOption(defaultIndex)
    }
}