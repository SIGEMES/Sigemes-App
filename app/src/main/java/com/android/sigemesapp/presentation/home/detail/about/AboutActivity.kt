package com.android.sigemesapp.presentation.home.detail.about

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.sigemesapp.data.source.remote.response.DetailRoom
import com.android.sigemesapp.data.source.remote.response.GuesthouseData
import com.android.sigemesapp.databinding.ActivityAboutBinding
import com.android.sigemesapp.presentation.home.detail.DetailMessActivity
import com.android.sigemesapp.presentation.home.detail.DetailMessActivity.Companion
import com.android.sigemesapp.presentation.home.detail.DetailViewModel
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.extractFacilities
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding
    private val detailViewModel: DetailViewModel by viewModels()

    companion object {
        const val KEY_ROOM_ID = "key_room_id"
        const val KEY_GUESTHOUSE_ID = "key_guesthouse_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupAction()

        val roomId = intent.getIntExtra(DetailMessActivity.KEY_ROOM_ID, -1)
        val guesthouseId = intent.getIntExtra(DetailMessActivity.KEY_GUESTHOUSE_ID, -1)

        observeRoomData(guesthouseId, roomId)
        observeGuesthouseData(guesthouseId)


    }

    private fun setupAction() {

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.btnTentang.setOnClickListener {
            scrollToSection(binding.cardAboutSection)
            binding.activeLine1.visibility = View.VISIBLE
            binding.activeLine2.visibility = View.GONE
            binding.activeLine3.visibility = View.GONE
        }

        binding.btnFasilitas.setOnClickListener {
            scrollToSection(binding.cardFacilitiesSection)
            binding.activeLine1.visibility = View.GONE
            binding.activeLine2.visibility = View.VISIBLE
            binding.activeLine3.visibility = View.GONE
        }

        binding.btnKebijakan.setOnClickListener {
            scrollToSection(binding.cardKebijakanSection)
            binding.activeLine1.visibility = View.GONE
            binding.activeLine2.visibility = View.GONE
            binding.activeLine3.visibility = View.VISIBLE
        }
    }

    private fun scrollToSection(section: MaterialCardView) {
        binding.scrollView.post {
            binding.scrollView.smoothScrollTo(0, section.top)
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

    private fun setGuesthouseDetail(guesthouse: GuesthouseData) {
        binding.lokasiDesc.text = guesthouse.address
        binding.aboutDesc.text = guesthouse.description

        val facility = extractFacilities(guesthouse.facilities)
        binding.fasilitasUmumDesc.text = String.format("- " + facility.joinToString("\n- "))
    }

    private fun observeRoomData(guesthouseId: Int, roomId: Int,) {
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

    private fun setRoomDetail(room: DetailRoom) {
        val facility = extractFacilities(room.facilities)
        binding.fasilitasKamarDesc.text = String.format("- " + facility.joinToString("\n- "))
    }
}