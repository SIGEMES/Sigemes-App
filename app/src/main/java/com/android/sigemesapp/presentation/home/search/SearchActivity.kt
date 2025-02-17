package com.android.sigemesapp.presentation.home.search

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.sigemesapp.R
import com.android.sigemesapp.databinding.ActivitySearchBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val searchViewModel: SearchViewModel by viewModels()

    companion object {
        const val EXTRA_STRING = "extra_string"
        const val EXTRA_ROOM_COUNT = "extra_room_count"
        const val EXTRA_GUEST_COUNT = "extra_guest_count"
    }

    // Register for activity result to receive data from RoomPersonActivity
    private val roomPersonLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val roomCount = result.data?.getIntExtra(EXTRA_ROOM_COUNT, 0) ?: 0
            val guestCount = result.data?.getIntExtra(EXTRA_GUEST_COUNT, 1) ?: 1

            // Update LiveData with the new values
            searchViewModel.setRoomCount(roomCount)
            searchViewModel.setGuestCount(guestCount)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val string = intent.getStringExtra(EXTRA_STRING) ?: "Default Query"

        setup(string)
    }

    private fun setup(string: String) {
        if (string == "Mess") {
            binding.title.text = getText(R.string.text_mess)
            binding.roomPerson.visibility = View.VISIBLE
        } else if (string == "Gedung") {
            binding.title.text = getText(R.string.text_gedung)
            binding.roomPersonPicker.visibility = View.GONE
        }

        // Observe LiveData for room and guest counts
        searchViewModel.countRoom.observe(this) { roomCount ->
            searchViewModel.countGuest.observe(this) { guestCount ->
                val selected = "$roomCount Kamar, $guestCount Tamu"
                binding.roomPersonPicker.findViewById<TextInputEditText>(R.id.room_person).setText(selected)
            }
        }

        // Open date picker when the date field is clicked
        binding.mtrlPickerTextInputDate.findViewById<TextInputEditText>(
            R.id.date_picker_actions
        ).setOnClickListener {
            datePickerDialog()
        }

        // Open RoomPersonActivity when the room/person field is clicked
        binding.roomPersonPicker.findViewById<TextInputEditText>(
            R.id.room_person
        ).setOnClickListener {
            val roomCount = searchViewModel.countRoom.value ?: 0
            val guestCount = searchViewModel.countGuest.value ?: 1

            val intent = Intent(this, RoomPersonActivity::class.java).apply {
                putExtra(EXTRA_ROOM_COUNT, roomCount)
                putExtra(EXTRA_GUEST_COUNT, guestCount)
            }
            roomPersonLauncher.launch(intent)
        }

        // Handle back button click
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun datePickerDialog() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setTitleText("Pilih tanggal menyewa")

        val datePicker = builder.build()
        datePicker.addOnPositiveButtonClickListener { selection ->
            val startDate = selection.first
            val endDate = selection.second

            val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale("id", "ID"))
            val startDateString = sdf.format(Date(startDate))
            val endDateString = sdf.format(Date(endDate))

            val selectedDateRange = "$startDateString - $endDateString"

            binding.mtrlPickerTextInputDate.findViewById<TextInputEditText>(R.id.date_picker_actions).setText(selectedDateRange)
        }

        datePicker.show(supportFragmentManager, "DATE_PICKER")
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