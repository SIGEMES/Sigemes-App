package com.android.sigemesapp.utils.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.widget.TextView
import com.android.sigemesapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FailedDialog(private val activity: Activity) {

    private var dialog: Dialog? = null

    fun startFailedDialog(message: String) {
        if (dialog == null) {
            dialog = Dialog(activity).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(R.layout.failed_dialog)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setCancelable(true)
            }
        }

        dialog?.findViewById<TextView>(R.id.message)?.text = message

        if (dialog?.isShowing == false) {
            dialog?.show()
        }
    }

    fun dismissDialog() {
        dialog?.dismiss()
    }
}
