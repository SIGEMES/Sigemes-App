package com.android.sigemesapp.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog.Builder

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