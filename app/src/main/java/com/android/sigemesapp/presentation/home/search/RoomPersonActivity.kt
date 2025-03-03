//package com.android.sigemesapp.presentation.home.search
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.MenuItem
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.android.sigemesapp.R
//import com.android.sigemesapp.databinding.ActivityRoomPersonBinding
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class RoomPersonActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityRoomPersonBinding
//    private val searchViewModel: SearchViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityRoomPersonBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.title = "Tambahkan Tamu & Kamar"
//
//        val roomCount = intent.getIntExtra(SearchActivity.EXTRA_ROOM_COUNT, 0)
//        val guestCount = intent.getIntExtra(SearchActivity.EXTRA_GUEST_COUNT, 1)
//
//        searchViewModel.setRoomCount(roomCount)
//        searchViewModel.setGuestCount(guestCount)
//
//        setupAction()
//    }
//
//    private fun setupAction() {
//        searchViewModel.countRoom.observe(this) { count ->
//            binding.roomCount.text = count.toString()
//        }
//
//        searchViewModel.countGuest.observe(this) { count ->
//            binding.guestCount.text = count.toString()
//        }
//
//        binding.addButton.setOnClickListener { searchViewModel.incrementRoom() }
//        binding.removeButton.setOnClickListener { searchViewModel.decrementRoom() }
//
//        binding.addButton2.setOnClickListener { searchViewModel.incrementGuest() }
//        binding.removeButton2.setOnClickListener { searchViewModel.decrementGuest() }
//
//        binding.saveButton.setOnClickListener {
//            val roomCount = searchViewModel.countRoom.value ?: 0
//            val guestCount = searchViewModel.countGuest.value ?: 1
//
//            val intent = Intent(this, SearchActivity::class.java).apply {
//                putExtra(SearchActivity.EXTRA_ROOM_COUNT, roomCount)
//                putExtra(SearchActivity.EXTRA_GUEST_COUNT, guestCount)
//            }
//            setResult(RESULT_OK, intent)
//            finish()
//        }
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            android.R.id.home -> {
//                onBackPressedDispatcher.onBackPressed()
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
//}