package com.android.sigemesapp.presentation.account.edit

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.sigemesapp.R
import com.android.sigemesapp.data.source.remote.response.RenterData
import com.android.sigemesapp.databinding.ActivityEditProfileBinding
import com.android.sigemesapp.presentation.auth.AuthViewModel
import com.android.sigemesapp.presentation.customview.CustomFullnameValid
import com.android.sigemesapp.presentation.customview.CustomPhoneNumberValid
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.dialog.FailedDialog
import com.android.sigemesapp.utils.dialog.LoadingDialog
import com.android.sigemesapp.utils.dialog.SuccessDialog
import com.android.sigemesapp.utils.reduceFileImage
import com.android.sigemesapp.utils.uriToFile
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private val authViewModel: AuthViewModel by viewModels()
    private var isPhotoChanged = false
    private var isNameChanged = false
    private var isGenderChanged = false
    private var isPhoneNumberChanged = false
    private lateinit var genderSpinner: Spinner
    private val successDialog = SuccessDialog(this)
    private val loadingDialog = LoadingDialog(this)
    private val failedDialog = FailedDialog(this)

    companion object {
        const val KEY_USER_ID = "key_user_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Ubah Data Akun"

        val userId = intent.getIntExtra(KEY_USER_ID, -1)

        genderSpinner = binding.spinnerCrops
        observeUserData()
    }

    private fun observeUserData() {
        authViewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                authViewModel.getRenterData(user.id)

                authViewModel.renterData.observe(this) { result ->
                    when (result) {
                        is Result.Loading -> {

                        }

                        is Result.Success -> {
                            setupData(result.data)
                        }

                        is Result.Error -> {

                        }
                    }

                }
            }
        }
    }

    private fun setupData(data: RenterData) {
        val fullname = authViewModel.fullname ?: data.fullname
        val photoUri = authViewModel.currentImageUri ?: Uri.parse(data.profilePicture)
        val gender = authViewModel.gender ?: data.gender
        val phoneNumber = authViewModel.phoneNumber ?: data.phoneNumber

        checkChanges(data.id, data.fullname, data.phoneNumber, data.gender)

        binding.userEmail.text = data.email

        binding.edUpdateFullnameLayout.findViewById<CustomFullnameValid>(R.id.ed_update_fullname)
            .setText(fullname)
        binding.edUpdatePhoneNumberLayout.findViewById<CustomPhoneNumberValid>(R.id.ed_update_phone_number)
            .setText(phoneNumber)
        Glide.with(binding.profilePicture.context)
            .load(photoUri)
            .error(R.drawable.ic_android_black_24dp)
            .into(binding.profilePicture)

        val genderOptions = resources.getStringArray(R.array.gender_options).toList()

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCrops.adapter = spinnerAdapter

        genderOptions.find { it.equals(gender, ignoreCase = true) }?.let {
            binding.spinnerCrops.setSelection(spinnerAdapter.getPosition(it))
        }

    }

    private fun updateSaveButtonVisibility() {
        binding.saveButton.visibility =
            if (isNameChanged || isPhotoChanged || isGenderChanged || isPhoneNumberChanged) View.VISIBLE else View.GONE
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            isPhotoChanged = true
            authViewModel.currentImageUri = uri
            showImage()
            updateSaveButtonVisibility()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        authViewModel.currentImageUri?.let {
            binding.profilePicture.setImageURI(it)
        }
    }

    private fun checkChanges(
        id: Int,
        initialFullname: String,
        initalPhoneNumber: String,
        initialGender: String
    ) {
       binding.edUpdateFullname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                isNameChanged = s.toString().trim() != initialFullname
                authViewModel.fullname = s.toString().trim()
                updateSaveButtonVisibility()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.edUpdatePhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                isPhoneNumberChanged = s.toString().trim() != initalPhoneNumber
                authViewModel.phoneNumber = s.toString().trim()
                updateSaveButtonVisibility()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                val genderValue = when (selectedItem) {
                    "Perempuan" -> "perempuan"
                    "Laki-Laki" -> "laki_laki"
                    else -> ""
                }

                if (genderValue.isNotEmpty() && !genderValue.equals(initialGender, ignoreCase = true)) {
                    isGenderChanged = true
                    authViewModel.gender = genderValue
                    Log.e("CheckGenderUpdate", "Gender Updated to: $genderValue")
                    updateSaveButtonVisibility()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.editProfilePictureButton.setOnClickListener {
            startGallery()
        }

        binding.saveButton.setOnClickListener {
            lifecycleScope.launch {
                val imageFile = if (isPhotoChanged) {
                    try {
                        authViewModel.currentImageUri?.let { uri ->
                            withContext(Dispatchers.IO) {
                                uriToFile(uri, this@EditProfileActivity).reduceFileImage()
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@EditProfileActivity,
                            "Terjadi kesalahan saat memproses gambar",
                            Toast.LENGTH_SHORT
                        ).show()
                        null
                    }
                } else null

                updateProfile(id, imageFile, initialFullname, initialGender, initalPhoneNumber)
            }
        }


    }

    private fun updateProfile(
        id: Int,
        imageFile: File?,
        initialFullname: String,
        initialGender: String,
        initalPhoneNumber: String
    ) {
        val fullname = authViewModel.fullname ?: initialFullname
        val gender = authViewModel.gender ?: initialGender
        val phoneNumber = authViewModel.phoneNumber ?: initalPhoneNumber
        if(fullname != null && gender != null && phoneNumber != null){
            authViewModel.updateRenterData(id, fullname, phoneNumber, gender, imageFile)
            authViewModel.updatedDataResult.observe(this){ result ->
                when (result) {
                    is Result.Loading -> {
                        loadingDialog.startLoadingDialog()
                    }

                    is Result.Success -> {
                        loadingDialog.dismissDialog()
                        successDialog.startSuccessDialog("Perubahan Berhasil Disimpan!")
                        navigateBack()
                    }

                    is Result.Error -> {
                        loadingDialog.dismissDialog()
                        failedDialog.startFailedDialog("Perubahan Gagal Disimpan!")
                        Log.e("Upload", "Error processing profile update: ${result.message}")
                    }
                }

            }
        }
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

    private fun navigateBack() {
        lifecycleScope.launch {
            delay(2000)
            successDialog.dismissDialog()
            finish()
        }
    }
}