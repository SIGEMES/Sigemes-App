package com.android.sigemesapp.presentation.home.search

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import com.android.sigemesapp.R
import com.android.sigemesapp.databinding.ActivitySearchBinding
import com.android.sigemesapp.presentation.auth.AuthViewModel
import com.android.sigemesapp.presentation.home.search.rent.RentViewModel
import com.android.sigemesapp.utils.calculateDays
import com.android.sigemesapp.utils.calculateNights
import com.android.sigemesapp.utils.dialog.FailedDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
//    private val searchViewModel: SearchViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val rentViewModel: RentViewModel by viewModels()
    private val failedDialog = FailedDialog(this)
    private var category = ""
    private var startDateSend : Long = 0
    private var endDateSend : Long = 0
    private var gender = ""

    companion object {
        const val EXTRA_STRING = "extra_string"
        const val EXTRA_START_DATE = "extra_start_date"
        const val EXTRA_END_DATE = "extra_end_date"
        const val EXTRA_GENDER = "extra_gender"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Cari"
        }

        category = intent.getStringExtra(EXTRA_STRING) ?: "Default Query"

        setupSearchCard()
        observeUserData()
    }

    private fun observeUserData() {
        authViewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                gender = user.gender
            }
        }
    }


    private fun setupSearchCard() {
        binding.title.text = if (category == "Mess") getText(R.string.text_mess) else getText(R.string.text_gedung)
        binding.malamOrDays.text = if (category == "Mess") "malam" else "hari"
        val today = Calendar.getInstance().timeInMillis
        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.timeInMillis
        val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale("id", "ID"))
        val defaultStartDate = if (category == "Gedung") tomorrow else today

        startDateSend = defaultStartDate
        endDateSend = tomorrow

        binding.mtrlPickerTextInputDate.findViewById<TextInputEditText>(R.id.date_picker_actions)
            .setText("${sdf.format(Date(defaultStartDate))} - ${sdf.format(Date(tomorrow))}")

        setupClickListeners(defaultStartDate, tomorrow)
    }

    private fun setupClickListeners(defaultStartDate: Long, defaultEndDate: Long) {
        binding.mtrlPickerTextInputDate.findViewById<TextInputEditText>(R.id.date_picker_actions)
            .setOnClickListener { datePickerDialog(defaultStartDate, defaultEndDate) }

        binding.searchButton.setOnClickListener {
            val intent = Intent(this, SearchResultActivity::class.java).apply {
                putExtra(EXTRA_STRING, category)
                putExtra(EXTRA_START_DATE, startDateSend)
                putExtra(EXTRA_END_DATE, endDateSend)
                putExtra(EXTRA_GENDER, gender)
            }
            startActivity(intent)
        }
    }

    private fun datePickerDialog(defaultStartDate: Long, defaultEndDate: Long) {
        val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale("id", "ID"))
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setSelection(Pair(defaultStartDate, defaultEndDate))
        builder.setTitleText("Pilih tanggal menyewa")

        val datePicker = builder.build()

        val calendarToday = Calendar.getInstance()
        val today = normalizeToStartOfDay(calendarToday.timeInMillis)

        val calendarYesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
        val yesterday = normalizeToStartOfDay(calendarYesterday.timeInMillis)

        datePicker.addOnPositiveButtonClickListener { selection ->
            val startDate = selection.first
            val endDate = selection.second

            if (binding.title.text == getString(R.string.text_gedung)) {
                if (startDate == today) {
                    failedDialog.startFailedDialog("Tidak dapat memesan gedung hari ini. Gedung dapat dipesan mulai esok hari")
                    return@addOnPositiveButtonClickListener
                } else if (startDate < today) {
                    failedDialog.startFailedDialog("Tidak dapat memesan tanggal yang sudah lewat")
                    return@addOnPositiveButtonClickListener
                }
            }

            if (binding.title.text == getString(R.string.text_mess)) {
                if (startDate <= yesterday) {
                    failedDialog.startFailedDialog("Tidak bisa memesan untuk tanggal yang sudah lewat")
                    return@addOnPositiveButtonClickListener
                }
                if (startDate == endDate) {
                    failedDialog.startFailedDialog("Pemesanan Mess harus minimal 1 malam")
                    return@addOnPositiveButtonClickListener
                }
            }

            val startDateString = sdf.format(Date(startDate))
            val endDateString = sdf.format(Date(endDate))

            startDateSend = startDate
            endDateSend = endDate

            val selectedDateRange = "$startDateString - $endDateString"
            binding.mtrlPickerTextInputDate.findViewById<TextInputEditText>(R.id.date_picker_actions)
                .setText(selectedDateRange)

            val duration = if (binding.title.text == getString(R.string.text_mess)) {
                calculateNights(startDate, endDate)
            } else {
                calculateDays(startDate, endDate)
            }

            binding.malamCount.text = String.format("$duration")
        }

        datePicker.show(supportFragmentManager, "DATE_PICKER")
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