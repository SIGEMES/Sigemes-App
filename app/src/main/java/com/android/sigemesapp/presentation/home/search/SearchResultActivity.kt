package com.android.sigemesapp.presentation.home.search

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.sigemesapp.R
import com.android.sigemesapp.data.source.remote.response.CityHallData
import com.android.sigemesapp.data.source.remote.response.GuesthouseResponse
import com.android.sigemesapp.data.source.remote.response.RoomItem
import com.android.sigemesapp.databinding.ActivitySearchResultBinding
import com.android.sigemesapp.presentation.home.search.adapter.FacilityAdapter
import com.android.sigemesapp.presentation.home.search.adapter.PhotoAdapter
import com.android.sigemesapp.presentation.home.search.adapter.RoomAdapter
import com.android.sigemesapp.presentation.home.search.detail.DetailGedungActivity
import com.android.sigemesapp.presentation.home.search.detail.DetailMessActivity
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.calculateDays
import com.android.sigemesapp.utils.calculateNights
import com.android.sigemesapp.utils.extractFacilities
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class SearchResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchResultBinding
    private val searchViewModel: SearchViewModel by viewModels()
    private val roomList = mutableListOf<RoomItem>()
    private var category = ""
    private var startDate : Long = 0
    private var endDate : Long = 0
    private var startDateApi = ""
    private var endDateApi = ""
    private var gender = ""

    companion object {
        const val EXTRA_STRING = "extra_string"
        const val EXTRA_START_DATE = "extra_start_date"
        const val EXTRA_END_DATE = "extra_end_date"
        const val EXTRA_GENDER = "extra_gender"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        category = intent.getStringExtra(EXTRA_STRING) ?: "Default Query"
        startDate = intent.getLongExtra(EXTRA_START_DATE, -1)
        endDate = intent.getLongExtra(EXTRA_END_DATE, -1)
        gender = intent.getStringExtra(EXTRA_GENDER) ?: "Default Query"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val ymf = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))

        startDateApi = ymf.format(Date(startDate))
        endDateApi = ymf.format(Date(endDate))

        setupViewVisibility()

    }

    private fun setupViewVisibility() {
        val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale("id", "ID"))

        val startDateString = sdf.format(Date(startDate))
        val endDateString = sdf.format(Date(endDate))

        if (category == "Mess") {
            binding.availableGedungCard.visibility = View.GONE
            binding.notAvailableGedungCard.visibility = View.GONE
            val duration = calculateNights(startDate, endDate)
            supportActionBar?.title = "$startDateString - $endDateString, $duration malam"
            setupRoomCard()
        } else {
            binding.rvRoomCard.visibility = View.GONE
            val duration = calculateDays(startDate, endDate)
            supportActionBar?.title = "$startDateString - $endDateString, $duration hari"
            setupCityHallCard()
        }
    }

    private fun setupRoomCard() {
        binding.rvRoomCard.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        searchViewModel.getGuesthouses()

        searchViewModel.allGuesthousesResult.observe(this){ result ->
            when(result){
                is Result.Loading -> {

                }

                is Result.Success -> {
                    if (result.data.isNotEmpty()) {
                        setListAvailableRoom(result.data)
                    } else {
                        binding.rvRoomCard.visibility = View.GONE
                        binding.availableText.text = getString(R.string.no_rooms_available)
                    }
                }

                is Result.Error -> {

                }
            }
        }
    }


    private fun setListAvailableRoom(data: List<GuesthouseResponse>) {
        val guesthouseIds = data.map { it.data.id }
        for (id in guesthouseIds){
            searchViewModel.getRooms(id, startDateApi, endDateApi, gender)
            searchViewModel.allRooms.observe(this){ result ->
                when(result){
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        if (result.data.isNotEmpty()) {
                            binding.rvRoomCard.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                            roomList.addAll(result.data)
                            setupRoomData(result.data)
                        }
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, "Tidak dapat mengambil data kamar yang tersedia", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    private fun setupRoomData(data: List<RoomItem>) {
        val filteredList = data.filter { it.availableSlot > 0 }
        val adapter = RoomAdapter(filteredList)
        binding.rvRoomCard.adapter = adapter

        adapter.setOnItemClickCallback(object : RoomAdapter.OnItemClickCallback {
            override fun onItemClicked(data: RoomItem) {
                sendSelectedRoom(data)
            }
        })
    }

    //Setting setOnItemClickCallback
    private fun sendSelectedRoom(data: RoomItem) {
        val intent = Intent(this, DetailMessActivity::class.java)
        intent.putExtra(DetailMessActivity.KEY_ROOM_ID, data.id)
        intent.putExtra(DetailMessActivity.KEY_GUESTHOUSE_ID, data.guesthouseId)
        intent.putExtra(DetailMessActivity.EXTRA_START_DATE, startDate)
        intent.putExtra(DetailMessActivity.EXTRA_END_DATE, endDate)
        intent.putExtra(DetailMessActivity.EXTRA_GENDER, gender)
        startActivity(intent)
    }

    //Setting up gedung card
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
                    Toast.makeText(this, "Tidak dapat mengambil data gedung adam malik yang tersedia", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupCardVisibility(data: CityHallData) {
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

    private fun setAvailableCityHallData(data: CityHallData) {
        binding.gedungName.text = data.name

        var minPrice = Int.MAX_VALUE
        var maxPrice = Int.MIN_VALUE

        for (pricing in data.pricing) {
            if (pricing.pricePerDay < minPrice) minPrice = pricing.pricePerDay
            if (pricing.pricePerDay > maxPrice) maxPrice = pricing.pricePerDay
        }

        binding.priceRange.text = String.format(
            "Rp %s - Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(minPrice),
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(maxPrice)
        )

        binding.rvPhoto.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)

        val photoUrls = data.media.map { it.url }

        val photoAdapter = PhotoAdapter(photoUrls)

        binding.rvPhoto.adapter = photoAdapter

        binding.facilities.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)

        val facilityList = extractFacilities(data.pricing.first().facilities)

        val facilityAdapter = FacilityAdapter(facilityList)
        binding.facilities.adapter = facilityAdapter

        binding.availableGedungCard.setOnClickListener {
            val intent = Intent(this, DetailGedungActivity::class.java)
            intent.putExtra(DetailGedungActivity.KEY_CITYHALL_ID, 1)
            intent.putExtra(DetailGedungActivity.EXTRA_START_DATE, startDate)
            intent.putExtra(DetailGedungActivity.EXTRA_END_DATE, endDate)
            intent.putExtra(DetailMessActivity.EXTRA_GENDER, gender)
            startActivity(intent)
        }
    }

    private fun setNotAvailableCityHallData(data: CityHallData) {
        binding.gedungNameGray.text = data.name

        var minPrice = Int.MAX_VALUE
        var maxPrice = Int.MIN_VALUE

        for (pricing in data.pricing) {
            if (pricing.pricePerDay < minPrice) minPrice = pricing.pricePerDay
            if (pricing.pricePerDay > maxPrice) maxPrice = pricing.pricePerDay
        }

        binding.priceRangeGray.text = String.format(
            "Rp %s - Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(minPrice),
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(maxPrice)
        )

        binding.rvPhotoGray.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)

        val photoUrls = data.media.map { it.url }

        val photoAdapter = PhotoAdapter(photoUrls)

        binding.rvPhotoGray.adapter = photoAdapter

        binding.facilitiesGray.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)

        val facilityList = extractFacilities(data.pricing.first().facilities)

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