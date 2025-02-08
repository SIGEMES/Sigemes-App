package com.android.sigemesapp.utils.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.android.sigemesapp.R

class LoadingDialog(private val activity: Activity) {

    private var dialog: Dialog? = null

    fun startLoadingDialog() {
        if (dialog == null) {
            dialog = Dialog(activity).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(R.layout.loading)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setCancelable(false)
            }
        }

        dialog?.show()
    }

    fun dismissDialog() {
        dialog?.dismiss()
    }
}