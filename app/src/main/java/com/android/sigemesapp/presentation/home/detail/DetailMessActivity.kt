package com.android.sigemesapp.presentation.home.detail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.sigemesapp.data.source.remote.response.DetailRoom
import com.android.sigemesapp.data.source.remote.response.GuesthouseData
import com.android.sigemesapp.databinding.ActivityDetailMessBinding
import com.android.sigemesapp.presentation.home.adapter.FacilityAdapter
import com.android.sigemesapp.presentation.home.adapter.PhotoAdapter
import com.android.sigemesapp.presentation.home.detail.adapter.PhotoRoomAdapter
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.extractFacilities
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailMessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailMessBinding
    private val detailViewModel: DetailViewModel by viewModels()

    companion object {
        const val KEY_ROOM_ID = "key_room_id"
        const val KEY_GUESTHOUSE_ID = "key_guesthouse_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val roomId = intent.getIntExtra(KEY_ROOM_ID, -1)
        val guesthouseId = intent.getIntExtra(KEY_GUESTHOUSE_ID, -1)

        binding.backButton.setOnClickListener {
            finish()
        }

        observeRoomData(guesthouseId, roomId)
        observeGuesthouseData(guesthouseId)
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
        detailViewModel.getDetailGuesthouseRoom(guesthouseId, roomId)

        detailViewModel.detailRoom.observe(this){ result ->
            when (result) {
                is Result.Loading -> {

                }
                is Result.Success -> {
                    val room = result.data
                    setRoomDetail(room)
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

    }

    private fun setRoomDetail(room: DetailRoom) {
        binding.rvPhoto.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val roomPhotoUrls = room.media.map { it.url }
        val roomPhotoAdapter = PhotoRoomAdapter(roomPhotoUrls)
        binding.rvPhoto.adapter = roomPhotoAdapter
    }
}