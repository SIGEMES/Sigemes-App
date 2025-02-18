package com.android.sigemesapp.presentation.home.detail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.sigemesapp.R
import com.android.sigemesapp.databinding.ActivityDetailGedungBinding
import com.android.sigemesapp.databinding.ActivityDetailMessBinding
import com.android.sigemesapp.presentation.home.detail.DetailMessActivity.Companion

class DetailGedungActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailGedungBinding
    private val detailViewModel: DetailViewModel by viewModels()

    companion object {
        const val KEY_CITYHALL_ID = "key_room_id"
        const val KEY_DURATION = "key_duration"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailGedungBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val cityHallId = intent.getIntExtra(KEY_CITYHALL_ID, -1)
        val receivedDuration = intent.getIntExtra(com.android.sigemesapp.presentation.home.detail.DetailMessActivity.KEY_DURATION, 1)

        observeCityHallData(cityHallId, receivedDuration)
    }

    private fun observeCityHallData(cityHallId: Int, receivedDuration: Int) {

    }
}