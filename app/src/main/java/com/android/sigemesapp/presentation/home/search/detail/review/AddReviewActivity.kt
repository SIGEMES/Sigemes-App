package com.android.sigemesapp.presentation.home.search.detail.review

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.sigemesapp.R
import com.android.sigemesapp.data.source.remote.ReviewMedia
import com.android.sigemesapp.data.source.remote.response.CityHallReviews
import com.android.sigemesapp.data.source.remote.response.GuesthouseRoomReviews
import com.android.sigemesapp.databinding.ActivityAddReviewBinding
import com.android.sigemesapp.presentation.home.search.detail.adapter.AddPhotoReviewAdapter
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.dialog.FailedDialog
import com.android.sigemesapp.utils.dialog.LoadingDialog
import com.android.sigemesapp.utils.dialog.SuccessDialog
import com.android.sigemesapp.utils.downloadImageAndGetUri
import com.android.sigemesapp.utils.reduceFileImage
import com.android.sigemesapp.utils.uriToFile
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.File

@AndroidEntryPoint
class AddReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddReviewBinding
    private val reviewViewModel: ReviewViewModel by viewModels()
    private lateinit var adapter: AddPhotoReviewAdapter
    private var rentId = -1
    private var category = ""
    private var action = ""
    private var reviewId = -1
    private val successDialog = SuccessDialog(this)
    private val loadingDialog = LoadingDialog(this)
    private val failedDialog = FailedDialog(this)

    companion object {
        const val KEY_RENT_ID = "key_rent_id"
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_ACTION = "extra_action"
    }

    private val pickImages = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        if (uris.isNotEmpty()) {
            reviewViewModel.addPhotos(uris)
            if(action == "update") {
                reviewViewModel.addNewPhotos(uris)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rentId = intent.getIntExtra(KEY_RENT_ID, -1)
        category = intent.getStringExtra(EXTRA_CATEGORY) ?: "Default Category"
        action = intent.getStringExtra(EXTRA_ACTION) ?: "Default Category"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (action == "add"){
            supportActionBar?.title = "Beri Ulasan"
            setupStars()
            setupRecyclerView()
            setupSendButton()
            setupPhotoSelection()
        } else if (action == "update"){
            supportActionBar?.title = "Edit Ulasan"
            if(category == "Mess"){
                observeMessComment()
            } else {
                observeCommentGedung()
            }
        }

    }

    private fun observeCommentGedung() {
        reviewViewModel.getCityHallReviewById(rentId)
        reviewViewModel.cityHallRentReviewById.observe(this) { result ->
            when(result){
                is Result.Loading -> {

                }
                is Result.Success -> {
                    setupCommentGedung(result.data.data)
                }
                is Result.Error -> {

                }
            }
        }
    }

    private fun observeMessComment() {
        reviewViewModel.getGuesthouseReviewById(rentId)
        reviewViewModel.guesthouseRentReviewById.observe(this) { result ->
            when (result) {
                is Result.Loading -> {

                }

                is Result.Success -> {
                    setupCommentMess(result.data.data)
                }

                is Result.Error -> {

                }
            }
        }
    }

    private fun setupCommentGedung(data: CityHallReviews?) {
        if (data != null) {
            reviewId = data.id
        }
        data?.rating?.let { reviewViewModel.setRating(it) }
        setupStars()

        data?.comment?.let { reviewViewModel.setComment(it) }
        binding.edReview.findViewById<TextInputEditText>(R.id.in_review)?.setText(reviewViewModel.comment.value)

        val photoUrls = data?.reviewMedia?.map { it.url }.orEmpty()

        val mediaList = data?.reviewMedia?.map { media ->
            ReviewMedia(
                id = media.id,
                url = media.url,
                uri = null
            )
        }.orEmpty()
        reviewViewModel.addDownloadedMedia(mediaList)

        if (reviewViewModel.downloadedPhotoUris.isEmpty()) {
            lifecycleScope.launch {
                val updatedMediaList = photoUrls.mapNotNull { url ->
                    val downloadedUri = withContext(Dispatchers.IO) {
                        downloadImageAndGetUri(this@AddReviewActivity, url)
                    }

                    reviewViewModel.downloadedMedia.find { it.url == url }?.apply {
                        uri = downloadedUri.toString()
                    }

                    downloadedUri
                }

                reviewViewModel.addPhotos(updatedMediaList)
            }
        }

        setupRecyclerView()
        setupPhotoSelection()

        binding.cancelButton.visibility = View.VISIBLE
        binding.cancelButton.setOnClickListener {
            finish()
        }
        setupSendButton()
    }

    private fun setupCommentMess(data: GuesthouseRoomReviews?) {
        if (data != null) {
            reviewId = data.id
        }
        data?.rating?.let { reviewViewModel.setRating(it) }
        setupStars()

        data?.comment?.let { reviewViewModel.setComment(it) }
        binding.edReview.findViewById<TextInputEditText>(R.id.in_review)?.setText(reviewViewModel.comment.value)

        val photoUrls = data?.reviewMedia?.map { it.url }.orEmpty()

        val mediaList = data?.reviewMedia?.map { media ->
            ReviewMedia(
                id = media.id,
                url = media.url,
                uri = null
            )
        }.orEmpty()
        reviewViewModel.addDownloadedMedia(mediaList)

        if (reviewViewModel.downloadedPhotoUris.isEmpty()) {
            lifecycleScope.launch {
                val updatedMediaList = photoUrls.mapNotNull { url ->
                    val downloadedUri = withContext(Dispatchers.IO) {
                        downloadImageAndGetUri(this@AddReviewActivity, url)
                    }

                    reviewViewModel.downloadedMedia.find { it.url == url }?.apply {
                        uri = downloadedUri.toString()
                    }

                    downloadedUri
                }

                reviewViewModel.addPhotos(updatedMediaList)
            }
        }

        setupRecyclerView()
        setupPhotoSelection()

        binding.cancelButton.visibility = View.VISIBLE
        binding.cancelButton.setOnClickListener {
            finish()
        }
        setupSendButton()
    }


    private fun setupStars() {
        val stars = listOf(binding.star1, binding.star2, binding.star3, binding.star4, binding.star5)

        val initialRating = reviewViewModel.rating.value ?: 0
        updateStars(stars, initialRating)

        stars.forEachIndexed { index, star ->
            star.setOnClickListener {
                val clickedRating = index + 1
                val currentRating = reviewViewModel.rating.value ?: 0

                val newRating = if (clickedRating == currentRating) {
                    currentRating - 1
                } else {
                    clickedRating
                }

                reviewViewModel.setRating(newRating)
            }
        }

        reviewViewModel.rating.observe(this) { rating ->
            updateStars(stars, rating)
        }
    }

    private fun updateStars(stars: List<ImageView>, rating: Int) {
        stars.forEachIndexed { index, star ->
            if (index < rating) {
                star.setImageResource(R.drawable.star)
            } else {
                star.setImageResource(R.drawable.star_border)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = AddPhotoReviewAdapter(
            photos = reviewViewModel.photos.value ?: emptyList(),
            onDeleteClick = { uri ->
                reviewViewModel.removePhoto(uri)
            }
        )
        binding.rvPhotoReview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvPhotoReview.adapter = adapter


        reviewViewModel.photos.observe(this) { photos ->
            adapter.updatePhotos(photos)
            updatePhotoVisibility()
            binding.textAddPhoto2.text = String.format("Tambah foto (${reviewViewModel.photos.value?.size}/5)")
        }
    }

    private fun updatePhotoVisibility() {
        val hasPhotos = reviewViewModel.photos.value?.isNotEmpty() == true
        binding.cardAddPhoto.visibility = if (hasPhotos) View.GONE else View.VISIBLE
        binding.rvPhotoReview.visibility = if (hasPhotos) View.VISIBLE else View.GONE
        if(reviewViewModel.photos.value?.size!! >= 5){
            binding.cardAddPhoto2.visibility = View.GONE
        } else {
            binding.cardAddPhoto2.visibility = if (hasPhotos) View.VISIBLE else View.GONE
        }

    }


    private fun setupSendButton() {
        binding.submitButton.setOnClickListener {
            val reviewText = binding.inReview.text.toString().trim()
            reviewViewModel.setComment(reviewText)

            if (reviewText.isEmpty()) {
                Toast.makeText(this, "Deskripsi tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                if (category == "Mess") {
                    sendGuesthouseReview()
                } else {
                    sendCityHallReview()
                }
            }
        }
    }

    private fun sendGuesthouseReview() {
        val photos = when (action) {
            "add" -> reviewViewModel.photos.value.orEmpty()
            else -> reviewViewModel.newPhotos.value.orEmpty()
        }

        lifecycleScope.launch {
            try {
                val files = if (photos.isNotEmpty()) {
                    withContext(Dispatchers.IO) {
                        photos.map { uri ->
                            uriToFile(uri, this@AddReviewActivity).reduceFileImage()
                        }
                    }
                } else {
                    emptyList()
                }

                uploadGuesthouseReview(rentId, reviewViewModel.rating.value ?: 0, reviewViewModel.comment.value ?: "", files)

            } catch (e: Exception) {
                Toast.makeText(this@AddReviewActivity, "Gagal mengirim ulasan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun uploadGuesthouseReview(rentId: Int, rating: Int, comment: String, files: List<File>) {
        if(action == "add"){
            reviewViewModel.addGuesthouseReview(rentId, rating, comment, files)
        } else {
            reviewViewModel.updateGuesthouseReview(rentId, reviewId, rating, comment, files, reviewViewModel.deletedMediaObjects)
        }

        reviewViewModel.guesthouseReviewResult.observe(this){ result ->
            when(result){
                is Result.Loading -> {
                    loadingDialog.startLoadingDialog()
                }

                is Result.Success -> {
                    loadingDialog.dismissDialog()
                    successDialog.startSuccessDialog("Ulasan Berhasil Diunggah! Terima kasih.")
                    navigateBack()
                }

                is Result.Error -> {
                    loadingDialog.dismissDialog()
                    failedDialog.startFailedDialog("Ulasan Gagal Diunggah!")
                    Log.e("Upload", "Error processing profile update: ${result.message}")
                }
            }
        }
    }

    private fun sendCityHallReview() {
        val photos = when (action) {
            "add" -> reviewViewModel.photos.value.orEmpty()
            else -> reviewViewModel.newPhotos.value.orEmpty()
        }

        lifecycleScope.launch {
            try {
                val files = if (photos.isNotEmpty()) {
                    withContext(Dispatchers.IO) {
                        photos.map { uri ->
                            uriToFile(uri, this@AddReviewActivity).reduceFileImage()
                        }
                    }
                } else {
                    emptyList()
                }

                uploadCityHallReview(rentId, reviewViewModel.rating.value ?: 0, reviewViewModel.comment.value ?: "", files)

            } catch (e: Exception) {
                Toast.makeText(this@AddReviewActivity, "Gagal mengirim ulasan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadCityHallReview(rentId: Int, rating: Int, comment: String, files: List<File>) {
        if(action == "add"){
            reviewViewModel.addCityHallReview(rentId, rating, comment, files)
        } else {
            reviewViewModel.updateCityHallReview(rentId, reviewId, rating, comment, files, reviewViewModel.deletedMediaObjects)
        }

        reviewViewModel.cityhallReviewResult.observe(this){ result ->
            when(result){
                is Result.Loading -> {
                    loadingDialog.startLoadingDialog()
                }

                is Result.Success -> {
                    loadingDialog.dismissDialog()
                    successDialog.startSuccessDialog("Ulasan Berhasil Diunggah! Terima kasih.")
                    navigateBack()
                }

                is Result.Error -> {
                    loadingDialog.dismissDialog()
                    failedDialog.startFailedDialog("Ulasan Gagal Diunggah!")
                    Log.e("Upload", "Error processing profile update: ${result.message}")
                }
            }
        }
    }

    private fun setupPhotoSelection() {
        binding.cardAddPhoto.setOnClickListener {
            pickImages.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.cardAddPhoto2.setOnClickListener {
            pickImages.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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