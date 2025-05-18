package com.android.sigemesapp.presentation.account

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.android.sigemesapp.databinding.ActivityTutorialBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TutorialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTutorialBinding
    private lateinit var youTubePlayerView: YouTubePlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Pusat Bantuan"

        youTubePlayerView = binding.youtubePlayerView
        lifecycle.addObserver(youTubePlayerView)

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                val videoId = "1cer1sqJqaY"
                youTubePlayer.loadVideo(videoId, 0f)
            }
        })
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


    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(youTubePlayerView)
    }
}