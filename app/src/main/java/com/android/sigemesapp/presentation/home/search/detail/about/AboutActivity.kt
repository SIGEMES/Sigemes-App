package com.android.sigemesapp.presentation.home.search.detail.about

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.sigemesapp.data.source.remote.response.CityHallData
import com.android.sigemesapp.data.source.remote.response.DetailRoom
import com.android.sigemesapp.data.source.remote.response.GuesthouseData
import com.android.sigemesapp.databinding.ActivityAboutBinding
import com.android.sigemesapp.presentation.home.search.detail.DetailGedungActivity
import com.android.sigemesapp.presentation.home.search.detail.DetailGedungActivity.Companion
import com.android.sigemesapp.presentation.home.search.detail.DetailViewModel
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.calculateDays
import com.android.sigemesapp.utils.extractFacilities
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding
    private val detailViewModel: DetailViewModel by viewModels()

    companion object {
        const val KEY_ROOM_ID = "key_room_id"
        const val KEY_GUESTHOUSE_ID = "key_guesthouse_id"
        const val KEY_CITYHALL_ID = "key_cityhall_id"
        const val EXTRA_START_DATE = "extra_start_date"
        const val EXTRA_END_DATE = "extra_end_date"
        const val EXTRA_GENDER = "extra_gender"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val roomId = intent.getIntExtra(KEY_ROOM_ID, -1)
        val guesthouseId = intent.getIntExtra(KEY_GUESTHOUSE_ID, -1)
        val cityHallId = intent.getIntExtra(KEY_CITYHALL_ID, -1)
        val gender = intent.getStringExtra(EXTRA_GENDER) ?: "Default Query"
        var startDate = intent.getLongExtra(EXTRA_START_DATE, -1)
        var endDate = intent.getLongExtra(EXTRA_END_DATE, -1)

        setupAction(cityHallId)

        val ymf = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))

        val startDateApi = ymf.format(Date(startDate))
        val endDateApi = ymf.format(Date(endDate))


        if(cityHallId == -1){
            if(roomId == 1){
                observeRoomData(guesthouseId, roomId, startDateApi, endDateApi, gender)
            }else{
                setupVisibilityRoom()
            }
            observeGuesthouseData(guesthouseId)
        } else{
            observeCityHallData(cityHallId, startDateApi, endDateApi)
        }
    }

    private fun setupVisibilityRoom() {
        binding.fasilitasKamarTitle.visibility = View.GONE
        binding.fasilitasKamarDesc.visibility = View.GONE
    }

    private fun setupAction(cityHallId: Int) {
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
            if (cityHallId == -1){
                binding.cardKebijakanMess.visibility = View.VISIBLE
                scrollToSection(binding.cardKebijakanMess)
            }else{
                binding.cardKebijakanGedung.visibility = View.VISIBLE
                scrollToSection(binding.cardKebijakanGedung)
            }
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
        binding.name.text = guesthouse.name

        val facility = extractFacilities(guesthouse.facilities)
        binding.fasilitasUmumDesc.text = String.format("- " + facility.joinToString("\n- "))
    }

    private fun observeRoomData(guesthouseId: Int, roomId: Int, startDate: String, endDate: String, gender: String) {
        detailViewModel.getDetailGuesthouseRoom(guesthouseId, roomId, startDate, endDate, gender)

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
        binding.fasilitasKamarDesc.visibility = View.VISIBLE
        binding.fasilitasKamarTitle.visibility = View.VISIBLE
        val facility = extractFacilities(room.facilities)
        binding.fasilitasKamarDesc.text = String.format("- " + facility.joinToString("\n- "))
    }


    private fun observeCityHallData(cityHallId: Int, startDate: String, endDate: String) {
        detailViewModel.getCityHall(cityHallId, startDate, endDate)

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

    private fun setCityHallDetail(cityHall: CityHallData) {
        binding.lokasiDesc.text = cityHall.address
        binding.aboutDesc.text = cityHall.description
        binding.name.text = cityHall.name

        val area = "${cityHall.areaM2} m²"
        val capacity = "${cityHall.peopleCapacity} orang"
        val facilityList = extractFacilities(cityHall.pricing[1].facilities).toMutableList()

        facilityList.add(0, "Luas: $area")
        facilityList.add(1, "Kapasitas: $capacity")

        binding.fasilitasUmumDesc.text = facilityList.joinToString("\n- ", prefix = "- ")

        binding.fasilitasKamarTitle.visibility = View.GONE
        binding.fasilitasKamarDesc.visibility = View.GONE
    }
}