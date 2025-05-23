package com.android.sigemesapp.presentation.history.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.sigemesapp.MainActivity
import com.android.sigemesapp.R
import com.android.sigemesapp.data.model.Rent
import com.android.sigemesapp.data.model.Review
import com.android.sigemesapp.databinding.ActivityDetailHistoryBinding
import com.android.sigemesapp.presentation.MainViewModel
import com.android.sigemesapp.presentation.home.search.detail.adapter.PhotoRoomAdapter
import com.android.sigemesapp.presentation.home.search.detail.review.AddReviewActivity
import com.android.sigemesapp.presentation.home.search.detail.review.ReviewViewModel
import com.android.sigemesapp.presentation.home.search.rent.RentViewModel
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_ACTION
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_CATEGORY
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_FLOW
import com.android.sigemesapp.utils.ConstantKeys.KEY_RENT_ID
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.calculateDaysUTC
import com.android.sigemesapp.utils.calculateNightsUTC
import com.android.sigemesapp.utils.dialog.DetailDialog
import com.android.sigemesapp.utils.formatDateUTC
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailHistoryBinding
    private val reviewViewModel: ReviewViewModel by viewModels()
    private val rentViewModel: RentViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    private var isExpanded: Boolean = false
    private var category = ""
    private var rentId = -1
    private var flow = ""
    private var rentStatus = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        onBackPressedDispatcher.addCallback(this, backCallback)
        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rentId = intent.getIntExtra(KEY_RENT_ID, -1)
        category = intent.getStringExtra(EXTRA_CATEGORY) ?: "Default Query"
        flow = intent.getStringExtra(EXTRA_FLOW) ?: "Default Query"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        observeRentData()
        setupAction()

    }

    private fun observeRentData() {
        rentViewModel.getRent(rentId)
        rentViewModel.rentResult.observe(this){ result ->
            when (result) {
                is Result.Loading -> {
                    onLoad()
                }
                is Result.Success -> {
                    val rent = result.data
                    val rentType = rentViewModel.checkRentType(result.data)
                    setDetailRent(rent, rentType)
                    supportActionBar?.title = "No. Pesanan ${rent.payment.id}"
                    rentStatus = rent.rentStatus
                    checkReview(rentStatus)
                    onSuccess()
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setDetailRent(rent: Rent, rentType: String) {
        when (rentType) {
            "guesthouse" -> {
                val pricing = rent.guesthouseRoomPricing
                binding.itemTitle.text = pricing?.guesthouseRoom?.guesthouse?.name
                binding.priceItemName.text = String.format("Kamar ${pricing?.guesthouseRoom?.name}, ${pricing?.retributionType}, ${calculateNightsUTC(rent.startDate, rent.endDate)} malam")
            }
            "cityhall" -> {
                val pricing = rent.cityHallPricing
                binding.itemTitle.text = pricing?.cityHall?.name
                binding.priceItemName.text = String.format("${pricing?.cityHall?.name}, Acara ${pricing?.activityType}, ${calculateDaysUTC(rent.startDate, rent.endDate)} hari")
                binding.checkInTitle.text = String.format("Mulai sewa")
                binding.checkOutTitle.text = String.format("Selesai sewa")
            }
            else -> {
                Toast.makeText(this, "Error: $rentType", Toast.LENGTH_SHORT).show()
            }
        }

        binding.checkInDate.text = formatDateUTC(rent.startDate)
        binding.checkOutDate.text = formatDateUTC(rent.endDate)
        binding.totalHarga2.text = mainViewModel.formatPrice(rent.payment.amount + 5550)
        binding.dibeliPadaTanggal.text = formatDateUTC(rent.payment.paymentConfirmedAt)
        binding.totalHarga.text = mainViewModel.formatPrice(rent.payment.amount + 5550)
        binding.priceItem.text = mainViewModel.formatPrice(rent.payment.amount)
        binding.adminFee.text = mainViewModel.formatPrice(5000)
        binding.ppn.text = mainViewModel.formatPrice(550)

        binding.cardRincianPesanan1.setOnClickListener {
            val detailDialog = DetailDialog.newInstance(category, rent = rent)
            detailDialog.show(supportFragmentManager, "DetailDialog")
        }
    }

    private fun onSuccess() {
        binding.cardPembayaran.visibility = View.VISIBLE
        binding.textDetailTitle.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.cardRincianPesanan1.visibility = View.VISIBLE
    }

    private fun onLoad() {
        binding.cardPembayaran.visibility = View.GONE
        binding.textDetailTitle.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.cardRincianPesanan1.visibility = View.GONE
        binding.leaveReviewTitle.visibility = View.GONE
        binding.cardReview.visibility = View.GONE
    }

    private fun setupAction() {
        binding.expandIcon.setOnClickListener {
            if (!isExpanded) {
                isExpanded = true
                binding.expandIcon.setImageResource(R.drawable.expand_less)
                binding.divider1.visibility = View.VISIBLE
                binding.adminFeeTitle.visibility = View.VISIBLE
                binding.adminFee.visibility = View.VISIBLE
                binding.totalHarga2.visibility = View.VISIBLE
                binding.totalTitle.visibility = View.VISIBLE
                binding.priceItemName.visibility = View.VISIBLE
                binding.priceItem.visibility = View.VISIBLE
                binding.ppnTitle.visibility = View.VISIBLE
                binding.ppn.visibility = View.VISIBLE
            } else {
                isExpanded = false
                binding.expandIcon.setImageResource(R.drawable.expand_more)
                binding.divider1.visibility = View.GONE
                binding.adminFeeTitle.visibility = View.GONE
                binding.adminFee.visibility = View.GONE
                binding.totalHarga2.visibility = View.GONE
                binding.totalTitle.visibility = View.GONE
                binding.priceItemName.visibility = View.GONE
                binding.priceItem.visibility = View.GONE
                binding.ppnTitle.visibility = View.GONE
                binding.ppn.visibility = View.GONE
            }
        }

        binding.cardReview.setOnClickListener {
            val intent = Intent(this, AddReviewActivity::class.java)
            intent.putExtra(KEY_RENT_ID, rentId)
            intent.putExtra(EXTRA_CATEGORY, category)
            intent.putExtra(EXTRA_ACTION, "add")
            startActivity(intent)
        }
    }

    private fun checkReview(rentStatus: String) {
        if(rentStatus == "selesai"){
            checkAlreadyReviewed(rentId)
        } else {
            binding.cardReview.visibility = View.GONE
            binding.leaveReviewTitle.visibility = View.GONE
        }
    }

    private fun checkAlreadyReviewed(rentId: Int) {
        reviewViewModel.getReviewById(rentId)
        reviewViewModel.getReviewResult.observe(this) { result ->
            when(result){
                is Result.Loading -> {

                }
                is Result.Success -> {
                    if(result.data.data == null){
                        binding.cardReview.visibility = View.VISIBLE
                        binding.leaveReviewTitle.visibility = View.VISIBLE
                    } else {
                        binding.leaveReviewTitle.text = String.format("Ulasan Anda")
                        binding.leaveReviewTitle.visibility = View.VISIBLE
                        binding.editCommentButton.visibility = View.VISIBLE
                        setupComment(result.data.data)
                        binding.commentCard.visibility = View.VISIBLE
                        binding.editCommentButton.setOnClickListener {
                            val intent = Intent(this, AddReviewActivity::class.java)
                            intent.putExtra(KEY_RENT_ID, rentId)
                            intent.putExtra(EXTRA_CATEGORY, category)
                            intent.putExtra(EXTRA_ACTION, "update")
                            startActivity(intent)
                        }
                    }

                }
                is Result.Error -> {
                    Log.e("CheckRevieww", "${result}")
                    val message = result.message
                    if (message.contains("not found", ignoreCase = true)) {
                        binding.cardReview.visibility = View.VISIBLE
                        binding.leaveReviewTitle.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setupComment(review: Review) {
        binding.desc.text = review.comment
        binding.username.text = review.rent.renter.fullname
        Glide.with(binding.profilePic.context)
            .load(review.rent.renter.profilePicture)
            .error(R.drawable.person_24)
            .into(binding.profilePic)

        binding.rvPhotoReview.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
        val photoUrls = review.reviewMedia.map { it.url }
        if(photoUrls.isNotEmpty()){
            binding.rvPhotoReview.visibility = View.VISIBLE
            val photoAdapter = PhotoRoomAdapter(binding.root.context as Activity, photoUrls)
            binding.rvPhotoReview.adapter = photoAdapter
        } else {
            binding.rvPhotoReview.visibility = View.GONE
        }

        val createdAt = review.createdAt
        val hoursDifference = mainViewModel.calculateTimeDifference(createdAt)
        binding.hour.text = hoursDifference
        binding.rating.text = String.format("${review.rating}/5")
        if(review.reviewReply == null){
            binding.cardAdminReply.visibility = View.GONE
        }else{
            binding.cardAdminReply.visibility = View.VISIBLE
            binding.hour2.text = mainViewModel.calculateTimeDifference(review.reviewReply.createdAt)
            binding.adminReply.text = review.reviewReply.comment
        }
    }

    override fun onBackPressed() {
        when(intent.getStringExtra(EXTRA_FLOW)) {
            "booking" -> {
                // Kembali ke Main untuk alur booking
                Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }.also { startActivity(it) }
                finish()
            }
            "history_pending" -> {
                // Kembali ke Fragment History untuk alur pending
                Intent(this, MainActivity::class.java).apply {
                    putExtra("OPEN_FRAGMENT", "history")
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }.also { startActivity(it) }
                finish()
            }
            else -> super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        checkReview(rentStatus)
    }

}