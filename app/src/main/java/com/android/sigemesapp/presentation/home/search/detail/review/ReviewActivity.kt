package com.android.sigemesapp.presentation.home.search.detail.review

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.sigemesapp.data.model.CityHallReviews
import com.android.sigemesapp.data.model.GuesthouseRoomReviews
import com.android.sigemesapp.databinding.ActivityReviewBinding
import com.android.sigemesapp.presentation.home.search.detail.adapter.ReviewCityHallAdapter
import com.android.sigemesapp.presentation.home.search.detail.adapter.ReviewGuesthouseRoomsAdapter
import com.android.sigemesapp.utils.ConstantKeys.KEY_CITYHALL_ID
import com.android.sigemesapp.utils.ConstantKeys.KEY_GUESTHOUSE_ID
import com.android.sigemesapp.utils.ConstantKeys.KEY_ROOM_ID
import com.android.sigemesapp.utils.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewBinding
    private val reviewViewModel: ReviewViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val cityHallId = intent.getIntExtra(KEY_CITYHALL_ID, -1)
        val guesthouseId = intent.getIntExtra(KEY_GUESTHOUSE_ID, -1)
        val roomId = intent.getIntExtra(KEY_ROOM_ID, -1)

        if(cityHallId == -1){
            observeRoomReviews(guesthouseId, roomId)
            supportActionBar?.title = "Ulasan Kamar Mess"

        }else{
            observeCityHallReviews(cityHallId)
            supportActionBar?.title = "Ulasan Gedung Adam Malik"
        }

    }

    private fun observeRoomReviews(guesthouseId: Int, roomId: Int) {
        reviewViewModel.getGuesthouseRoomsReviews(guesthouseId, roomId)
        reviewViewModel.guesthouseRoomReviews.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    setLoadingVisibility(true)
                }
                is Result.Success -> {
                    setLoadingVisibility(false)
                    val data = result.data
                    if (data.isNotEmpty()) {
                        setupGuesthouseRoomReviews(data)
                    } else {
                        setEmptyReviewMessage("Mohon maaf. Belum ada ulasan mengenai kamar ini.")
                    }
                }
                is Result.Error -> {
                    setLoadingVisibility(false)
                }
            }
        }
    }

    private fun observeCityHallReviews(cityHallId: Int) {
        reviewViewModel.getCityHallReviews(cityHallId)
        reviewViewModel.cityHallReviews.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    setLoadingVisibility(true)
                }
                is Result.Success -> {
                    setLoadingVisibility(false)
                    val data = result.data
                    if (data.isNotEmpty()) {
                        setupCityHallReviews(data)
                    } else {
                        setEmptyReviewMessage("Mohon maaf. Belum ada ulasan mengenai Gedung ini.")
                    }
                }
                is Result.Error -> {
                    setLoadingVisibility(false)
                }
            }
        }
    }

    private fun setupGuesthouseRoomReviews(reviews: List<GuesthouseRoomReviews>) {
        binding.rvReviews.layoutManager = LinearLayoutManager(this)
        binding.rvReviews.adapter = ReviewGuesthouseRoomsAdapter(reviews)
        binding.textReviewCount.text = reviewViewModel.calculateAverageRatingText(reviews)
        binding.totalUlasan.text = reviewViewModel.calculateTotalReviewText(reviews.size)
        showReviewsSection()
    }

    private fun setupCityHallReviews(reviews: List<CityHallReviews>) {
        binding.rvReviews.layoutManager = LinearLayoutManager(this)
        binding.rvReviews.adapter = ReviewCityHallAdapter(reviews)
        binding.textReviewCount.text = reviewViewModel.calculateAverageRatingText(reviews)
        binding.totalUlasan.text = reviewViewModel.calculateTotalReviewText(reviews.size)
        showReviewsSection()
    }

    private fun setLoadingVisibility(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.textUlasanPelanggan.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.rvReviews.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.review.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showReviewsSection() {
        binding.review.visibility = View.VISIBLE
        binding.rvReviews.visibility = View.VISIBLE
        binding.textUlasanPelanggan.visibility = View.VISIBLE
    }

    private fun setEmptyReviewMessage(message: String) {
        binding.review.visibility = View.GONE
        binding.rvReviews.visibility = View.GONE
        binding.textUlasanPelanggan.text = message
        binding.textUlasanPelanggan.visibility = View.VISIBLE
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
}
