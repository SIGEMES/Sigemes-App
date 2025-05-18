package com.android.sigemesapp.presentation.home.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.sigemesapp.R
import com.android.sigemesapp.data.model.CityHall
import com.android.sigemesapp.data.model.RoomItem
import com.android.sigemesapp.databinding.ActivitySearchResultBinding
import com.android.sigemesapp.presentation.MainViewModel
import com.android.sigemesapp.presentation.home.search.adapter.FacilityAdapter
import com.android.sigemesapp.presentation.home.search.adapter.PhotoAdapter
import com.android.sigemesapp.presentation.home.search.adapter.RoomAdapter
import com.android.sigemesapp.presentation.home.search.detail.DetailGedungActivity
import com.android.sigemesapp.presentation.home.search.detail.DetailMessActivity
import com.android.sigemesapp.utils.ConstantKeys
import com.android.sigemesapp.utils.Result
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class SearchResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchResultBinding
    private val searchViewModel: SearchViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    private val roomList = mutableListOf<RoomItem>()
    private var category = ""
    private var startDate : Long = 0
    private var endDate : Long = 0
    private var startDateApi = ""
    private var endDateApi = ""
    private var gender = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        category = intent.getStringExtra(ConstantKeys.EXTRA_CATEGORY) ?: "Default Query"
        startDate = intent.getLongExtra(ConstantKeys.EXTRA_START_DATE, -1)
        endDate = intent.getLongExtra(ConstantKeys.EXTRA_END_DATE, -1)
        gender = intent.getStringExtra(ConstantKeys.EXTRA_GENDER) ?: "Default Query"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val ymf = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))
        startDateApi = ymf.format(Date(startDate))
        endDateApi = ymf.format(Date(endDate))
        setupViewVisibility()
    }

    private fun setupViewVisibility() {
        supportActionBar?.title = mainViewModel.getDurationText(category, startDate, endDate)
        if (category == "Mess") {
            binding.availableGedungCard.visibility = View.GONE
            binding.notAvailableGedungCard.visibility = View.GONE
            setListAvailableRoom()
        } else {
            binding.rvRoomCard.visibility = View.GONE
            setupCityHallCard()
        }
    }

    private fun setListAvailableRoom() {
        val guesthouseIds = 1
        searchViewModel.getRooms(guesthouseIds, startDateApi, endDateApi, gender)
        searchViewModel.allRooms.observe(this){ result ->
            when(result){
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvRoomCard.visibility = View.GONE
                    binding.availableText.visibility = View.GONE
                }

                is Result.Success -> {
                    if (result.data.isNotEmpty()) {
                        roomList.addAll(result.data)
                        binding.availableText.visibility = View.VISIBLE
                        binding.rvRoomCard.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                        setupRoomData(result.data)
                        Log.e("RoomCheck", "Hello im heere")
                    } else {
                        binding.rvRoomCard.visibility = View.GONE
                        binding.availableText.visibility = View.VISIBLE
                        binding.availableText.text = getString(R.string.no_rooms_available)
                    }
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.availableText.visibility = View.VISIBLE
                    binding.availableText.text = getString(R.string.failed_to_fetch_data)
                }
            }
        }
    }

    private fun setupRoomData(data: List<RoomItem>) {
        val adapter = RoomAdapter(data)
        binding.rvRoomCard.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvRoomCard.adapter = adapter


        adapter.setOnItemClickCallback(object : RoomAdapter.OnItemClickCallback {
            override fun onItemClicked(data: RoomItem) {
                sendSelectedRoom(data)
            }
        })
    }

    private fun sendSelectedRoom(data: RoomItem) {
        val intent = Intent(this, DetailMessActivity::class.java)
        intent.putExtra(ConstantKeys.KEY_ROOM_ID, data.id)
        intent.putExtra(ConstantKeys.KEY_GUESTHOUSE_ID, data.guesthouseId)
        intent.putExtra(ConstantKeys.EXTRA_START_DATE, startDate)
        intent.putExtra(ConstantKeys.EXTRA_END_DATE, endDate)
        intent.putExtra(ConstantKeys.EXTRA_GENDER, gender)
        startActivity(intent)
    }

    private fun setupCityHallCard() {
        searchViewModel.getCityHall(1, startDateApi, endDateApi)

        searchViewModel.cityHall.observe(this){ result ->
            when(result){
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.availableText.visibility = View.GONE
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    setupCardVisibility(result.data)
                }

                is Result.Error -> {
                    binding.availableGedungCard.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun setupCardVisibility(data: CityHall) {
        val status = data.status
        if(status == "tersedia"){
            binding.availableText.text = getString(R.string.cityhall_available)
            binding.availableText.visibility = View.VISIBLE
            binding.availableGedungCard.visibility = View.VISIBLE
            binding.notAvailableGedungCard.visibility = View.GONE
            setAvailableCityHallData(data)
        } else {
            binding.availableText.text = getString(R.string.no_cityhall_available)
            binding.availableText.visibility = View.VISIBLE
            binding.availableGedungCard.visibility = View.GONE
            binding.notAvailableGedungCard.visibility = View.VISIBLE
            setNotAvailableCityHallData(data)
        }
    }

    private fun setAvailableCityHallData(data: CityHall) {
        binding.gedungName.text = data.name

        val (minPrice, maxPrice) = mainViewModel.getMinMaxPriceCityHall(data.pricing)

        binding.priceRange.text = mainViewModel.formatPriceRange(minPrice, maxPrice)

        binding.rvPhoto.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)

        val photoUrls = data.media.map { it.url }

        val photoAdapter = PhotoAdapter(photoUrls)

        binding.rvPhoto.adapter = photoAdapter

        binding.facilities.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)

        val facilityList = mainViewModel.extractFacilities(data.pricing.first().facilities)

        val facilityAdapter = FacilityAdapter(facilityList)
        binding.facilities.adapter = facilityAdapter

        binding.availableGedungCard.setOnClickListener {
            val intent = Intent(this, DetailGedungActivity::class.java)
            intent.putExtra(ConstantKeys.KEY_CITYHALL_ID, 1)
            intent.putExtra(ConstantKeys.EXTRA_START_DATE, startDate)
            intent.putExtra(ConstantKeys.EXTRA_END_DATE, endDate)
            intent.putExtra(ConstantKeys.EXTRA_GENDER, gender)
            startActivity(intent)
        }
    }

    private fun setNotAvailableCityHallData(data: CityHall) {
        binding.gedungNameGray.text = data.name

        val (minPrice, maxPrice) = mainViewModel.getMinMaxPriceCityHall(data.pricing)

        binding.priceRange.text = mainViewModel.formatPriceRange(minPrice, maxPrice)

        binding.rvPhotoGray.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)

        val photoUrls = data.media.map { it.url }

        val photoAdapter = PhotoAdapter(photoUrls)

        binding.rvPhotoGray.adapter = photoAdapter

        binding.facilitiesGray.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)

        val facilityList = mainViewModel.extractFacilities(data.pricing.first().facilities)

        val facilityAdapter = FacilityAdapter(facilityList)
        binding.facilitiesGray.adapter = facilityAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}