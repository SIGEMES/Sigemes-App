package com.android.sigemesapp.presentation.home.search.detail.review

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.sigemesapp.data.source.remote.response.CityHallReviews
import com.android.sigemesapp.data.source.remote.response.GuesthouseRoomReviews
import com.android.sigemesapp.databinding.ActivityReviewBinding
import com.android.sigemesapp.presentation.home.search.detail.DetailViewModel
import com.android.sigemesapp.presentation.home.search.detail.adapter.ReviewCityHallAdapter
import com.android.sigemesapp.presentation.home.search.detail.adapter.ReviewGuesthouseRoomsAdapter
import com.android.sigemesapp.utils.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewBinding
    private val detailViewModel: DetailViewModel by viewModels()
    private val reviewViewModel: ReviewViewModel by viewModels()

    companion object {
        const val KEY_ROOM_ID = "key_room_id"
        const val KEY_GUESTHOUSE_ID = "key_guesthouse_id"
        const val KEY_CITYHALL_ID = "key_cityhall_id"
    }

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
            when(result){
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.textUlasanPelanggan.visibility = View.GONE
                    binding.rvReviews.visibility = View.GONE
                    binding.review.visibility = View.GONE
                }
                is Result.Success -> {
                    val guesthouseRoomReviews = result.data
                    setGuesthouseRoomReviews(guesthouseRoomReviews)
                    binding.progressBar.visibility = View.GONE
                    binding.textUlasanPelanggan.visibility = View.VISIBLE
                    binding.rvReviews.visibility = View.VISIBLE
                    binding.review.visibility = View.VISIBLE
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun setGuesthouseRoomReviews(guesthouseRoomReviews: List<GuesthouseRoomReviews>) {
        if(guesthouseRoomReviews.isNotEmpty()){
            binding.rvReviews.layoutManager = LinearLayoutManager(this)
            val adapter = ReviewGuesthouseRoomsAdapter(guesthouseRoomReviews)
            binding.rvReviews.adapter = adapter

            val totalRating = guesthouseRoomReviews.sumOf { it.rating }
            val totalItems = guesthouseRoomReviews.size

            val averageRating = if (totalItems > 0) totalRating.toDouble() / totalItems else 0

            binding.textReviewCount.text = String.format("$averageRating / 5")
            binding.totalUlasan.text = String.format(" ($totalItems ulasan)")
        }else{
            binding.rvReviews.visibility = View.GONE
            binding.review.visibility = View.GONE
            binding.textUlasanPelanggan.text = "Mohon maaf. Belum ada ulasan mengenai kamar ini."
        }

    }

    private fun observeCityHallReviews(cityHallId: Int) {
        reviewViewModel.getCityHallReviews(cityHallId)
        reviewViewModel.cityHallReviews.observe(this) { result ->
            when(result){
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.textUlasanPelanggan.visibility = View.GONE
                    binding.rvReviews.visibility = View.GONE
                    binding.review.visibility = View.GONE
                }
                is Result.Success -> {
                    val cityHallReviews = result.data
                    setCityHallReviews(cityHallReviews)
                    binding.progressBar.visibility = View.GONE
                    binding.textUlasanPelanggan.visibility = View.VISIBLE
                    binding.rvReviews.visibility = View.VISIBLE
                    binding.review.visibility = View.VISIBLE
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun setCityHallReviews(cityHallReviews: List<CityHallReviews>) {
        if(cityHallReviews.isNotEmpty()){
            binding.rvReviews.layoutManager = LinearLayoutManager(this)
            val adapter = ReviewCityHallAdapter(cityHallReviews)
            binding.rvReviews.adapter = adapter

            val totalRating = cityHallReviews.sumOf { it.rating }
            val totalItems = cityHallReviews.size

            val averageRating = if (totalItems > 0) totalRating.toDouble() / totalItems else 0

            binding.textReviewCount.text = String.format("$averageRating / 5")
            binding.totalUlasan.text = String.format(" ($totalItems ulasan)")
        } else {
            binding.review.visibility = View.GONE
            binding.textUlasanPelanggan.text = "Mohon maaf. Belum ada ulasan mengenai Gedung ini."
            binding.rvReviews.visibility = View.GONE
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
}
