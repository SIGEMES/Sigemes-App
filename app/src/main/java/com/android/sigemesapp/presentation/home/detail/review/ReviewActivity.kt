package com.android.sigemesapp.presentation.home.detail.review

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.android.sigemesapp.databinding.ActivityReviewBinding

class ReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewBinding

    companion object {
        const val KEY_ROOM_NAME = "key_room_name"
        const val KEY_CITYHALL_NAME = "key_cityHall_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val roomName = intent.getStringExtra(KEY_ROOM_NAME)
        val cityHallName = intent.getStringExtra(KEY_CITYHALL_NAME)
        supportActionBar?.title = roomName ?: cityHallName
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
