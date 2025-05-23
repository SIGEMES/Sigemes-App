package com.android.sigemesapp.presentation

import androidx.lifecycle.ViewModel
import com.android.sigemesapp.data.model.CityHallPricings
import com.android.sigemesapp.repository.SigemesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sigemesRepository: SigemesRepository
) : ViewModel() {

    fun getTodayAndTomorrowDates(): Pair<String, String> {
        val today = Calendar.getInstance().timeInMillis
        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.timeInMillis
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))
        return formatter.format(Date(today)) to formatter.format(Date(tomorrow))
    }

    fun getDurationText(category: String, startDate: Long, endDate: Long): String {
        val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale("id", "ID"))
        val startStr = sdf.format(Date(startDate))
        val endStr = sdf.format(Date(endDate))

        return if (category == "Mess") {
            val nights = calculateNights(startDate, endDate)
            "$startStr - $endStr, $nights malam"
        } else {
            val days = calculateDays(startDate, endDate)
            "$startStr - $endStr, $days hari"
        }
    }

    fun getMinMaxPriceCityHall(pricing: List<CityHallPricings>): Pair<Int, Int> {
        val prices = pricing.map { it.pricePerDay }
        return Pair(prices.minOrNull() ?: 0, prices.maxOrNull() ?: 0)
    }

    fun formatPriceRange(min: Int, max: Int): String {
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        return "Rp ${formatter.format(min)} - Rp ${formatter.format(max)}"
    }

    fun extractFacilities(facilities: String): List<String> {
        return facilities.split(";").map { it.trim() }
    }

    fun formatDate(timestamp: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))
        return formatter.format(Date(timestamp))
    }

    fun calculateDays(startDate: Long, endDate: Long): Int {
        val diffMillis = endDate - startDate
        val days = (diffMillis / (1000 * 60 * 60 * 24)).toInt()
        return days + 1
    }

    fun calculateNights(startDate: Long, endDate: Long): Int {
        val diffMillis = endDate - startDate
        return (diffMillis / (1000 * 60 * 60 * 24)).toInt()
    }

    fun formatDate2(timestamp: Long): String {
        val formatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale("id", "ID"))
        return formatter.format(Date(timestamp))
    }

    fun calculateTotalPrice(pricePerDay: Int, duration: Int): Int {
        return pricePerDay * duration
    }

    fun formatPrice(value: Int): String {
        return "Rp " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(value)
    }

    fun formatDateUTC(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val outputFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale("id", "ID"))
        outputFormat.timeZone = TimeZone.getDefault()

        val date = inputFormat.parse(inputDate)
        return date?.let { outputFormat.format(it) } ?: "Tanggal tidak valid"
    }

    fun calculateTimeDifference(date: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        val updatedTime: Date = dateFormat.parse(date) ?: return "Unknown"

        val now = Date()

        val diffInMillis = now.time - updatedTime.time

        val diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis)
        val diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
        val diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

        // Return the appropriate time difference
        return when {
            diffInDays >= 365 -> {
                val years = diffInDays / 365
                "$years tahun"
            }
            diffInDays >= 30 -> {
                val months = diffInDays / 30
                "$months bulan"
            }
            diffInDays >= 1 -> {
                "$diffInDays hari"
            }
            diffInHours >= 1 -> {
                "$diffInHours jam"
            }
            diffInMinutes >= 1 -> {
                "$diffInMinutes menit"
            }
            else -> {
                "$diffInSeconds detik"
            }
        }
    }

}