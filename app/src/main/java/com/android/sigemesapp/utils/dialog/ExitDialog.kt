package com.android.sigemesapp.utils.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.android.sigemesapp.databinding.ExitDialogBinding

class ExitDialog(private val activity: Activity) {

    private var dialog: Dialog? = null
    private var _binding: ExitDialogBinding? = null
    private val binding get() = _binding!!

    fun startExitDialog(onExit: () -> Unit, onCheckDetails: () -> Unit) {
        if (dialog == null) {
            dialog = Dialog(activity).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                _binding = ExitDialogBinding.inflate(layoutInflater)
                setContentView(binding.root)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setCancelable(true)

                binding.closeButton.setOnClickListener {
                    dismissDialog()
                }

                binding.keluarHalaman.setOnClickListener {
                    onExit()
                    dismissDialog()
                }

                binding.cekDetail.setOnClickListener {
                    onCheckDetails()
                    dismissDialog()
                }
            }
        }

        dialog?.show()
    }

    fun dismissDialog() {
        dialog?.dismiss()
        _binding = null
    }
}
