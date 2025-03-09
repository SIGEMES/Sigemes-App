package com.android.sigemesapp.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog.Builder
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.android.sigemesapp.BuildConfig
import com.android.sigemesapp.R
import com.android.sigemesapp.databinding.ItemHistoryBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

private const val MAXIMAL_SIZE = 1000000
private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())


fun showAlertDialog(
    context: Context,
    title: String,
    message: String? = null,
    positiveButtonText: String,
    negativeButtonText: String,
    onPositive: (() -> Unit)? = null,
    onNegative: (() -> Unit)? = null
) {
    Builder(context).apply {
        setTitle(title)
        setMessage(message)
        setPositiveButton(positiveButtonText) { dialog, _ ->
            onPositive?.invoke()
            dialog.dismiss()
        }
        setNegativeButton(negativeButtonText) { dialog, _ ->
            onNegative?.invoke()
            dialog.dismiss()
        }
        create()
        show()
    }
}

fun getFacilityIcon(facilityName: String): Int {
    val name = facilityName.lowercase(Locale.ROOT)
    return when {
        name.contains("single") -> R.drawable.single_bed
        name.contains("personal") -> R.drawable.single_bed
        name.contains("twin") -> R.drawable.single_bed
        name.contains("king") -> R.drawable.king_bed
        name.contains("ac") -> R.drawable.ac
        name.contains("tv") -> R.drawable.tv
        name.contains("parkir") -> R.drawable.parking
        name.contains("sofa") -> R.drawable.sofa
        name.contains("umum") -> R.drawable.wc
        name.contains("mandi") -> R.drawable.shower
        name.contains("wifi") -> R.drawable.wifi
        name.contains("duduk") -> R.drawable.seat
        name.contains("rapat") -> R.drawable.rapat
        name.contains("gedung") -> R.drawable.group
        name.contains("rias") -> R.drawable.desk
        name.contains("lemari") -> R.drawable.shelves

        else -> 0
    }
}

fun extractFacilities(facilities: String): List<String> {
    return facilities.split(";").map { it.trim() }
}

fun getImageUri(context: Context): Uri {
    var uri: Uri? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
        }
        uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }
    return uri ?: getImageUriForPreQ(context)
}

private fun getImageUriForPreQ(context: Context): Uri {
    val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(filesDir, "/MyCamera/$timeStamp.jpg")
    if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()
    return FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.fileprovider",
        imageFile
    )
}

fun createCustomTempFile(context: Context): File {
    val filesDir = context.externalCacheDir
    return File.createTempFile(timeStamp, ".jpg", filesDir)
}

fun uriToFile(imageUri: Uri, context: Context): File {
    val myFile = createCustomTempFile(context)
    val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
    val outputStream = FileOutputStream(myFile)
    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
    outputStream.close()
    inputStream.close()
    return myFile
}

fun File.reduceFileImage(): File {
    val file = this
    val bitmap = BitmapFactory.decodeFile(file.path).getRotatedBitmap(file)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAXIMAL_SIZE)
    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}

fun Bitmap.getRotatedBitmap(file: File): Bitmap {
    val orientation = ExifInterface(file).getAttributeInt(
        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
    )
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
        ExifInterface.ORIENTATION_NORMAL -> this
        else -> this
    }
}

fun rotateImage(source: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(
        source, 0, 0, source.width, source.height, matrix, true
    )

}

fun calculateNights(startDate: Long, endDate: Long): Int {
    val diffMillis = endDate - startDate
    return (diffMillis / (1000 * 60 * 60 * 24)).toInt()
}

fun calculateDays(startDate: Long, endDate: Long): Int {
    val diffMillis = endDate - startDate
    val days = (diffMillis / (1000 * 60 * 60 * 24)).toInt()
    return days + 1
}

fun formatDate(inputDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))
    val outputFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale("id", "ID"))

    val date = inputFormat.parse(inputDate) ?: return ""
    return outputFormat.format(date)
}

fun formatDateUTC(inputDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")

    val outputFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale("id", "ID"))
    outputFormat.timeZone = TimeZone.getDefault()

    val date = inputFormat.parse(inputDate)
    return date?.let { outputFormat.format(it) } ?: "Tanggal tidak valid"
}

fun convertTimestampToFormattedDate(timestamp: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")

    val outputFormat = SimpleDateFormat("EEE, dd MMM yyyy, hh:mm a", Locale("id", "ID"))
    outputFormat.timeZone = TimeZone.getDefault()

    val date = inputFormat.parse(timestamp)
    return date?.let { outputFormat.format(it) } ?: "Tanggal tidak valid"
}

fun convertToDateRange(timestamp1: String, timestamp2: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    val outputFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale("id", "ID")).apply {
        timeZone = TimeZone.getDefault()
    }

    val startDate = inputFormat.parse(timestamp1)
    val endDate = inputFormat.parse(timestamp2)

    return if (startDate != null && endDate != null) {
        "${outputFormat.format(startDate)} - ${outputFormat.format(endDate)}"
    } else {
        "Tanggal tidak valid"
    }
}

fun setRentStatusColorAndText(binding: ItemHistoryBinding, status: String) {
    val statusText = when (status.lowercase()) {
        "pending" -> "Menunggu Pembayaran"
        "dikonfirmasi" -> "Pembayaran Berhasil"
        "selesai" -> "Selesai"
        "dibatalkan" -> "Pesanan Dibatalkan"
        "expired" -> "Pesanan sudah kadaluwarsa"
        else -> status
    }
    binding.status.text = statusText

    val colorRes = when (status.lowercase()) {
        "pending" -> R.color.orange
        "dikonfirmasi" -> R.color.darkBlue
        "dibatalkan" -> R.color.red
        "selesai" -> R.color.onGreen
        "expired" -> R.color.red
        else -> null
    }

    val color = if (colorRes != null) {
        ContextCompat.getColor(binding.root.context, colorRes)
    } else {
        Color.parseColor("#808080")
    }
    binding.status.setTextColor(color)
}

fun calculateNightsUTC(startDateStr: String, endDateStr: String): Int {
    val startDate = parseDate(startDateStr)
    val endDate = parseDate(endDateStr)
    val diffMillis = endDate - startDate
    return (diffMillis / (1000 * 60 * 60 * 24)).toInt()
}

fun calculateDaysUTC(startDateStr: String, endDateStr: String): Int {
    val startDate = parseDate(startDateStr)
    val endDate = parseDate(endDateStr)
    val diffMillis = endDate - startDate
    val days = (diffMillis / (1000 * 60 * 60 * 24)).toInt()
    return days + 1
}

private fun parseDate(dateStr: String): Long {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone("UTC")
    return format.parse(dateStr)?.time ?: 0L
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

fun isoToTimestamp(isoString: String): Long {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date = dateFormat.parse(isoString)
    return date?.time ?: 0L
}



