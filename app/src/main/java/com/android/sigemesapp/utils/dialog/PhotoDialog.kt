package com.android.sigemesapp.utils.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.ImageView
import com.android.sigemesapp.R
import com.bumptech.glide.Glide

class PhotoDialog(private val activity: Activity) {

    private var dialog: Dialog? = null

    fun startPhotoDialog(photoUrl: String) {
        if (dialog == null) {
            dialog = Dialog(activity).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(R.layout.zoom_photo)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setCancelable(true)
            }
        }

        dialog?.findViewById<ImageView>(R.id.photo)?.let { imageView ->
            Glide.with(activity)
                .load(photoUrl)
                .error(R.drawable.mess)
                .into(imageView)
        }

        if (dialog?.isShowing == false) {
            dialog?.show()
        }
    }

    fun dismissDialog() {
        dialog?.dismiss()
    }
}
