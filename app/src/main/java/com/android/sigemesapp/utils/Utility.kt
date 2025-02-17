package com.android.sigemesapp.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog.Builder
import com.android.sigemesapp.R
import java.util.Locale

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
        else -> 0
    }
}

fun extractFacilities(facilities: String): List<String> {
    return facilities.split(";").map { it.trim() }
}

