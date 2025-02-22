package com.android.sigemesapp.presentation.home.search

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.sigemesapp.R
import com.android.sigemesapp.data.source.remote.response.CityHallData
import com.android.sigemesapp.data.source.remote.response.GuesthouseResponse
import com.android.sigemesapp.data.source.remote.response.RoomItem
import com.android.sigemesapp.databinding.ActivitySearchBinding
import com.android.sigemesapp.presentation.home.HomeViewModel
import com.android.sigemesapp.presentation.home.adapter.FacilityAdapter
import com.android.sigemesapp.presentation.home.adapter.PhotoAdapter
import com.android.sigemesapp.presentation.home.adapter.RoomAdapter
import com.android.sigemesapp.presentation.home.detail.DetailGedungActivity
import com.android.sigemesapp.presentation.home.detail.DetailMessActivity
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.dialog.FailedDialog
import com.android.sigemesapp.utils.extractFacilities
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val searchViewModel: SearchViewModel by viewModels()
    private val failedDialog = FailedDialog(this)
    private val homeViewModel: HomeViewModel by viewModels()
    private val roomList = mutableListOf<RoomItem>()

    companion object {
        const val EXTRA_STRING = "extra_string"
        const val EXTRA_ROOM_COUNT = "extra_room_count"
        const val EXTRA_GUEST_COUNT = "extra_guest_count"
    }

    private val roomPersonLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val roomCount = result.data?.getIntExtra(EXTRA_ROOM_COUNT, 0) ?: 0
            val guestCount = result.data?.getIntExtra(EXTRA_GUEST_COUNT, 1) ?: 1

            searchViewModel.setRoomCount(roomCount)
            searchViewModel.setGuestCount(guestCount)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Cari"

        val category = intent.getStringExtra(EXTRA_STRING) ?: "Default Query"

        setupSearchCard(category)
    }

    private fun setupSearchCard(category: String) {
        val today = Calendar.getInstance().timeInMillis
        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.timeInMillis

        val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale("id", "ID"))

        val defaultStartDate = if (category == "Gedung") tomorrow else today
        val defaultEndDate = tomorrow

        val startDateString = sdf.format(Date(defaultStartDate))
        val endDateString = sdf.format(Date(defaultEndDate))
        val selectedDateRange = "$startDateString - $endDateString"

        binding.mtrlPickerTextInputDate.findViewById<TextInputEditText>(R.id.date_picker_actions)
            .setText(selectedDateRange)

        binding.title.text = if (category == "Mess") getText(R.string.text_mess) else getText(R.string.text_gedung)

        setupViewVisibility(category)
        observeViewModel()
        setupClickListeners(defaultStartDate, defaultEndDate)
    }

    private fun setupViewVisibility(category: String) {
        if (category == "Mess") {
            binding.roomPerson.visibility = View.VISIBLE
            binding.pickRoomPerson.visibility = View.VISIBLE
            binding.malamOrDays.text = "malam"
            binding.availableRoom.visibility = View.VISIBLE
            binding.availableGedung.visibility = View.GONE
            binding.availableGedungCard.visibility = View.GONE
            binding.notAvailableGedung.visibility = View.GONE
            binding.notAvailableGedungCard.visibility = View.GONE
            setupRecyclerView()
            setupRoomCard()
        } else {
            binding.roomPersonPicker.visibility = View.GONE
            binding.pickRoomPerson.visibility = View.GONE
            binding.malamOrDays.text = "hari"
            binding.pickRangeDate.text = "Pilih rentang tanggal mulai dan selesai penyewaan"
            binding.availableRoom.visibility = View.GONE
            binding.filterButton.visibility = View.GONE
            binding.rvRoomCard.visibility = View.GONE
            setupCityHallCard()
        }
    }

    private fun observeViewModel() {
        searchViewModel.countRoom.observe(this) { roomCount ->
            searchViewModel.countGuest.observe(this) { guestCount ->
                val selected = "$roomCount Kamar, $guestCount Tamu"
                binding.roomPersonPicker.findViewById<TextInputEditText>(R.id.room_person).setText(selected)
            }
        }
    }

    private fun setupClickListeners(defaultStartDate: Long, defaultEndDate: Long) {
        binding.mtrlPickerTextInputDate.findViewById<TextInputEditText>(R.id.date_picker_actions)
            .setOnClickListener { datePickerDialog(defaultStartDate, defaultEndDate) }

        binding.roomPersonPicker.findViewById<TextInputEditText>(R.id.room_person)
            .setOnClickListener {
                val roomCount = searchViewModel.countRoom.value ?: 0
                val guestCount = searchViewModel.countGuest.value ?: 1

                val intent = Intent(this, RoomPersonActivity::class.java).apply {
                    putExtra(EXTRA_ROOM_COUNT, roomCount)
                    putExtra(EXTRA_GUEST_COUNT, guestCount)
                }
                roomPersonLauncher.launch(intent)
            }
    }

    private fun datePickerDialog(defaultStartDate: Long, defaultEndDate: Long) {
        val calendarToday = Calendar.getInstance()
        val today = calendarToday.timeInMillis
        val normalizedToday = normalizeToStartOfDay(today)

        val calendarYesterday = Calendar.getInstance()
        calendarYesterday.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = calendarYesterday.timeInMillis
        val normalizedYesterday = normalizeToStartOfDay(yesterday)

        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setSelection(Pair(defaultStartDate, defaultEndDate))
        builder.setTitleText("Pilih tanggal menyewa")
        val datePicker = builder.build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val startDate = selection.first
            val endDate = selection.second

            val normalizedStartDate = normalizeToStartOfDay(startDate)
            val normalizedEndDate = normalizeToStartOfDay(endDate)

            if (binding.title.text == getString(R.string.text_gedung)) {
                if (normalizedStartDate == normalizedToday) {
                    failedDialog.startFailedDialog("Tidak dapat memesan gedung hari ini. Gedung dapat dipesan mulai esok hari")
                    return@addOnPositiveButtonClickListener
                } else if (normalizedStartDate < normalizedToday) {
                    failedDialog.startFailedDialog("Tidak dapat memesan tanggal yang sudah lewat")
                    return@addOnPositiveButtonClickListener
                }
            }

            if (binding.title.text == getString(R.string.text_mess)) {
                if (normalizedStartDate <= normalizedYesterday) {
                    failedDialog.startFailedDialog("Tidak bisa memesan untuk tanggal yang sudah lewat")
                    return@addOnPositiveButtonClickListener
                }

                if (normalizedStartDate == normalizedEndDate) {
                    failedDialog.startFailedDialog("Pemesanan Mess harus minimal 1 malam")
                    return@addOnPositiveButtonClickListener
                }
            }

            val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale("id", "ID"))
            val startDateString = sdf.format(Date(startDate))
            val endDateString = sdf.format(Date(endDate))

            val selectedDateRange = "$startDateString - $endDateString"
            binding.mtrlPickerTextInputDate.findViewById<TextInputEditText>(R.id.date_picker_actions).setText(selectedDateRange)

            val duration = if (binding.title.text == getString(R.string.text_mess)) {
                calculateNights(startDate, endDate)
            } else {
                calculateDays(startDate, endDate)
            }

            binding.malamCount.text = String.format("$duration")
        }

        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }


    //Setting up list room card
    private fun setupRecyclerView() {
        binding.rvRoomCard.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun setupRoomCard() {
        homeViewModel.getGuesthouses()

        homeViewModel.allGuesthousesResult.observe(this){ result ->
            when(result){
                is Result.Loading -> {

                }

                is Result.Success -> {
                    if (result.data.isNotEmpty()) {
                        binding.rvRoomCard.visibility = View.VISIBLE
                        setListAvailableRoom(result.data)
                    } else {
                        binding.rvRoomCard.visibility = View.GONE
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
            homeViewModel.getRooms(id)
            homeViewModel.allRooms.observe(this){ result ->
                when(result){
                    is Result.Loading -> {
                        binding.rvRoomCard.visibility = View.GONE
                        binding.progressBar.visibility = View.VISIBLE
                        binding.filterButton.visibility = View.GONE
                    }

                    is Result.Success -> {
                        if (result.data.isNotEmpty()) {
                            binding.rvRoomCard.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                            binding.filterButton.visibility = View.VISIBLE
                            roomList.addAll(result.data)
                            setupRoomData(result.data)
                        }
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.filterButton.visibility = View.GONE
                        Toast.makeText(this, "Tidak dapat mengambil data kamar yang tersedia", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    private fun setupRoomData(data: List<RoomItem>) {
        val adapter = RoomAdapter(data)
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
        startActivity(intent)
    }

    //Setting up gedung card
    private fun setupCityHallCard() {
        homeViewModel.getCityHall(1)

        homeViewModel.cityHall.observe(this){ result ->
            when(result){
                is Result.Loading -> {
                    binding.availableGedungCard.visibility = View.GONE
                    binding.notAvailableGedung.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.availableGedungCard.visibility = View.VISIBLE
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
            binding.availableGedung.visibility = View.VISIBLE
            binding.availableGedungCard.visibility = View.VISIBLE
            binding.notAvailableGedung.visibility = View.GONE
            binding.notAvailableGedungCard.visibility = View.GONE
            setAvailableCityHallData(data)
        } else {
            binding.availableGedung.visibility = View.GONE
            binding.availableGedungCard.visibility = View.GONE
            binding.notAvailableGedung.visibility = View.VISIBLE
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

    private fun calculateNights(startDate: Long, endDate: Long): Int {
        val diffMillis = endDate - startDate
        return (diffMillis / (1000 * 60 * 60 * 24)).toInt()
    }

    private fun calculateDays(startDate: Long, endDate: Long): Int {
        val diffMillis = endDate - startDate
        val days = (diffMillis / (1000 * 60 * 60 * 24)).toInt()
        return days + 1
    }

    private fun normalizeToStartOfDay(timeInMillis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
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