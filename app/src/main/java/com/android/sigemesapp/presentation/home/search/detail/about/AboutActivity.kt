package com.android.sigemesapp.presentation.home.search.detail.about

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.sigemesapp.data.model.CityHall
import com.android.sigemesapp.data.model.Guesthouse
import com.android.sigemesapp.data.model.RoomItem
import com.android.sigemesapp.databinding.ActivityAboutBinding
import com.android.sigemesapp.presentation.MainViewModel
import com.android.sigemesapp.presentation.home.search.detail.DetailViewModel
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_END_DATE
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_GENDER
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_START_DATE
import com.android.sigemesapp.utils.ConstantKeys.KEY_CITYHALL_ID
import com.android.sigemesapp.utils.ConstantKeys.KEY_GUESTHOUSE_ID
import com.android.sigemesapp.utils.ConstantKeys.KEY_ROOM_ID
import com.android.sigemesapp.utils.Result
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding
    private val detailViewModel: DetailViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    private var roomId = -1
    private var guesthouseId = -1
    private var cityHallId = -1
    private var gender = ""
    private var startDateApi = ""
    private var endDateApi = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        roomId = intent.getIntExtra(KEY_ROOM_ID, -1)
        guesthouseId = intent.getIntExtra(KEY_GUESTHOUSE_ID, -1)
        cityHallId = intent.getIntExtra(KEY_CITYHALL_ID, -1)
        gender = intent.getStringExtra(EXTRA_GENDER) ?: "Default Query"
        val startDate = intent.getLongExtra(EXTRA_START_DATE, -1)
        val endDate = intent.getLongExtra(EXTRA_END_DATE, -1)

        setupAction()

        startDateApi = mainViewModel.formatDate(startDate)
        endDateApi = mainViewModel.formatDate(endDate)

        if(cityHallId == -1){
            if(roomId == 1){
                observeRoomData()
            }else{
                setupVisibilityRoom()
            }
            observeGuesthouseData()
        } else{
            observeCityHallData()
        }
    }

    private fun setupVisibilityRoom() {
        binding.fasilitasKamarTitle.visibility = View.GONE
        binding.fasilitasKamarDesc.visibility = View.GONE
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
            if (cityHallId == -1){
                scrollToSection(binding.cardKebijakanMess)
            }else{
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

    private fun observeGuesthouseData() {
        detailViewModel.getDetailGuesthouse(guesthouseId)

        detailViewModel.guesthouseResult.observe(this){ result ->
            when (result) {
                is Result.Loading -> {
                    binding.cardAboutSection.visibility = View.GONE
                    binding.cardFacilitiesSection.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.cardAboutSection.visibility = View.VISIBLE
                    binding.cardFacilitiesSection.visibility = View.VISIBLE
                    val guesthouse = result.data
                    setGuesthouseDetail(guesthouse)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun setGuesthouseDetail(guesthouse: Guesthouse) {
        binding.lokasiDesc.text = guesthouse.address
        binding.aboutDesc.text = guesthouse.description
        binding.name.text = guesthouse.name
        binding.cardKebijakanMess.visibility = View.VISIBLE

        val facility = detailViewModel.formatFacilityList(guesthouse.facilities)
        binding.fasilitasUmumDesc.text = facility
    }

    private fun observeRoomData() {
        detailViewModel.getDetailGuesthouseRoom(guesthouseId, roomId, startDateApi, endDateApi, gender)

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

    private fun setRoomDetail(room: RoomItem) {
        binding.fasilitasKamarDesc.visibility = View.VISIBLE
        binding.fasilitasKamarTitle.visibility = View.VISIBLE

        val facility = detailViewModel.formatFacilityList(room.facilities)
        binding.fasilitasKamarDesc.text = facility
    }


    private fun observeCityHallData() {
        detailViewModel.getCityHall(cityHallId, startDateApi, endDateApi)

        detailViewModel.detailCityHall.observe(this){ result ->
            when (result) {
                is Result.Loading -> {
                    binding.cardAboutSection.visibility = View.GONE
                    binding.cardFacilitiesSection.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.cardAboutSection.visibility = View.VISIBLE
                    binding.cardFacilitiesSection.visibility = View.VISIBLE
                    val cityHall = result.data
                    setCityHallDetail(cityHall)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun setCityHallDetail(cityHall: CityHall) {
        binding.lokasiDesc.text = cityHall.address
        binding.aboutDesc.text = cityHall.description
        binding.name.text = cityHall.name
        binding.cardKebijakanGedung.visibility = View.VISIBLE
        val facility = detailViewModel.formatCityHallFacilities(cityHall)
        binding.fasilitasUmumDesc.text = facility
        binding.fasilitasKamarTitle.visibility = View.GONE
        binding.fasilitasKamarDesc.visibility = View.GONE
    }


}