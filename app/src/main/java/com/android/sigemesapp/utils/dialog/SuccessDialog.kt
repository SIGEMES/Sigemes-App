package com.android.sigemesapp.utils.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.TextView
import com.android.sigemesapp.R

class SuccessDialog(private val activity: Activity) {

    private var dialog: Dialog? = null

    fun startSuccessDialog(message: String) {
        if (dialog == null) {
            dialog = Dialog(activity).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(R.layout.success_dialog)
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
