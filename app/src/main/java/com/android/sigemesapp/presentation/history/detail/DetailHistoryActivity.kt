package com.android.sigemesapp.presentation.history.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.sigemesapp.MainActivity
import com.android.sigemesapp.R
import com.android.sigemesapp.data.source.remote.response.CityHallRent
import com.android.sigemesapp.data.source.remote.response.CityHallReviews
import com.android.sigemesapp.data.source.remote.response.GetReviewCityHallByIdResponse
import com.android.sigemesapp.data.source.remote.response.GuesthouseRentData
import com.android.sigemesapp.data.source.remote.response.GuesthouseRoomReviews
import com.android.sigemesapp.databinding.ActivityDetailHistoryBinding
import com.android.sigemesapp.presentation.home.search.detail.adapter.PhotoRoomAdapter
import com.android.sigemesapp.presentation.home.search.detail.review.AddReviewActivity
import com.android.sigemesapp.presentation.home.search.detail.review.ReviewViewModel
import com.android.sigemesapp.presentation.home.search.rent.RentViewModel
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.calculateDaysUTC
import com.android.sigemesapp.utils.calculateNightsUTC
import com.android.sigemesapp.utils.calculateTimeDifference
import com.android.sigemesapp.utils.dialog.DetailDialog
import com.android.sigemesapp.utils.formatDateUTC
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Locale


@AndroidEntryPoint
class DetailHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailHistoryBinding
    private val reviewViewModel: ReviewViewModel by viewModels()
    private val rentViewModel: RentViewModel by viewModels()
    private var isExpanded: Boolean = false
    private var category = ""
    private var rentId = -1
    private var before = ""
    private var rentStatus = ""

    companion object {
        const val KEY_RENT_ID = "key_rent_id"
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_BEFORE = "extra_before"
        const val EXTRA_ACTION = "extra_action"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        rentId = intent.getIntExtra(KEY_RENT_ID, -1)
        category = intent.getStringExtra(EXTRA_CATEGORY) ?: "Default Query"
        before = intent.getStringExtra(EXTRA_BEFORE) ?: "Default Query"
        Log.e("exttra_before", "before $before")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(category == "Mess"){
            observeGuesthouseRentData()
        }else if (category == "Gedung"){
            observeCityhallRentData()
        }
        setupAction()

    }

    private fun observeGuesthouseRentData() {
        rentViewModel.getDetailGuesthouseRent(rentId)
        rentViewModel.guesthouseDetailRent.observe(this){ result ->
            when(result){
                is Result.Loading -> {
                    onLoad()
                }
                is Result.Success -> {
                    val guesthouse = result.data
                    setGuesthouseData(guesthouse)
                    supportActionBar?.title = "No. Pesanan ${guesthouse.payment.id}"
                    onSuccess()
                    rentStatus = guesthouse.rentStatus
                    checkReview(rentStatus)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun setGuesthouseData(guesthouse: GuesthouseRentData) {
        binding.totalHarga.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(guesthouse.payment.amount + 5550))
        binding.priceItem.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(guesthouse.payment.amount ))
        binding.adminFee.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(5000))
        binding.ppn.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(550))
        binding.totalHarga2.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(guesthouse.payment.amount + 5550))
        binding.priceItemName.text = String.format("Kamar ${guesthouse.guesthouseRoomPricing.guesthouseRoom.name}, ${guesthouse.guesthouseRoomPricing.retributionType}, ${calculateNightsUTC(guesthouse.startDate, guesthouse.endDate)} malam")
        binding.dibeliPadaTanggal.text = formatDateUTC(guesthouse.payment.paymentConfirmedAt)
        binding.itemTitle.text = guesthouse.guesthouseRoomPricing.guesthouseRoom.guesthouse.name
        binding.checkInDate.text = formatDateUTC(guesthouse.startDate)
        binding.checkOutDate.text = formatDateUTC(guesthouse.endDate)

        binding.cardRincianPesanan1.setOnClickListener {
            val detailDialog = DetailDialog.newInstance("Mess", guesthouseRentData = guesthouse)
            detailDialog.show(supportFragmentManager, "DetailDialog")
        }
    }

    private fun observeCityhallRentData() {
        rentViewModel.getDetailCityHallRent(rentId)
        rentViewModel.cityhallDetailRent.observe(this){ result ->
            when(result){
                is Result.Loading -> {
                    onLoad()
                }
                is Result.Success -> {
                    val cityhall = result.data
                    setCityHallData(cityhall)
                    supportActionBar?.title = "No. Pesanan ${cityhall.payment.id}"
                    onSuccess()
                    rentStatus = cityhall.rentStatus
                    checkReview(rentStatus)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
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

    private fun setCityHallData(cityhall: CityHallRent) {
        binding.totalHarga.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(cityhall.payment.amount + 5550))
        binding.priceItem.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(cityhall.payment.amount))
        binding.adminFee.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(5000))
        binding.ppn.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(550))
        binding.totalHarga2.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(cityhall.payment.amount + 5550))
        binding.priceItemName.text = String.format("${cityhall.cityHallPricing.cityHall.name}, Acara ${cityhall.cityHallPricing.activityType}, ${calculateDaysUTC(cityhall.startDate, cityhall.endDate)} hari")
        binding.dibeliPadaTanggal.text = formatDateUTC(cityhall.payment.paymentConfirmedAt)
        binding.itemTitle.text = cityhall.cityHallPricing.cityHall.name
        binding.checkInDate.text = formatDateUTC(cityhall.startDate)
        binding.checkOutDate.text = formatDateUTC(cityhall.endDate)
        binding.checkInTitle.text = "Mulai sewa"
        binding.checkOutTitle.text = "Selesai sewa"

        binding.cardRincianPesanan1.setOnClickListener {
            val detailDialog = DetailDialog.newInstance("Gedung", cityhallRentData = cityhall)
            detailDialog.show(supportFragmentManager, "DetailDialog")
        }
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
        if(category == "Mess"){
            reviewViewModel.getGuesthouseReviewById(rentId)
            reviewViewModel.guesthouseRentReviewById.observe(this) { result ->
                when(result){
                    is Result.Loading -> {

                    }
                    is Result.Success -> {
                        if(result.data.data == null){
                            binding.cardReview.visibility = View.VISIBLE
                            binding.leaveReviewTitle.visibility = View.VISIBLE
                        } else {
                            binding.leaveReviewTitle.text = "Ulasan Anda"
                            binding.leaveReviewTitle.visibility = View.VISIBLE
                            binding.editCommentButton.visibility = View.VISIBLE
                            setupCommentGuesthouse(result.data.data)
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

                    }
                }
            }
        } else {
            reviewViewModel.getCityHallReviewById(rentId)
            reviewViewModel.cityHallRentReviewById.observe(this) { result ->
                when(result){
                    is Result.Loading -> {

                    }
                    is Result.Success -> {
                        if(result.data.data == null){
                            binding.cardReview.visibility = View.VISIBLE
                            binding.leaveReviewTitle.visibility = View.VISIBLE
                        } else {
                            binding.leaveReviewTitle.text = "Ulasan Anda"
                            binding.leaveReviewTitle.visibility = View.VISIBLE
                            binding.editCommentButton.visibility = View.VISIBLE
                            setupCommentCityHall(result.data.data)
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

                    }
                }
            }
        }
    }

    private fun setupCommentGuesthouse(review: GuesthouseRoomReviews) {
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
        val hoursDifference = calculateTimeDifference(createdAt)
        binding.hour.text = hoursDifference

        binding.rating.text = String.format("${review.rating}/5")

        if(review.reviewReply == null){
            binding.cardAdminReply.visibility = View.GONE
        }else{
            binding.cardAdminReply.visibility = View.VISIBLE
            binding.hour2.text = calculateTimeDifference(review.reviewReply.createdAt)
            binding.adminReply.text = review.reviewReply.comment
        }
    }

    private fun setupCommentCityHall(review: CityHallReviews) {
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
        val hoursDifference = calculateTimeDifference(createdAt)
        binding.hour.text = hoursDifference

        binding.rating.text = String.format("${review.rating}/5")

        if(review.reviewReply == null){
            binding.cardAdminReply.visibility = View.GONE
        }else{
            binding.cardAdminReply.visibility = View.VISIBLE
            binding.hour2.text = calculateTimeDifference(review.reviewReply.createdAt)
            binding.adminReply.text = review.reviewReply.comment
        }
    }

    override fun onBackPressed() {
        when (before) {
            "payment" -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        checkReview(rentStatus)
    }


}