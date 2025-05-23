package com.android.sigemesapp.presentation.history.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.sigemesapp.R
import com.android.sigemesapp.data.model.Rent
import com.android.sigemesapp.databinding.ActivityContinuePaymentBinding
import com.android.sigemesapp.presentation.MainViewModel
import com.android.sigemesapp.presentation.home.search.rent.RentViewModel
import com.android.sigemesapp.presentation.home.search.rent.payment.PaymentActivity
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_CATEGORY
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_FLOW
import com.android.sigemesapp.utils.ConstantKeys.KEY_RENT_ID
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.calculateDaysUTC
import com.android.sigemesapp.utils.calculateNightsUTC
import com.android.sigemesapp.utils.dialog.DetailDialog
import com.android.sigemesapp.utils.dialog.FailedDialog
import com.android.sigemesapp.utils.formatDateUTC
import com.android.sigemesapp.utils.isoToTimestamp
import com.android.sigemesapp.utils.showAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ContinuePaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContinuePaymentBinding
    private val rentViewModel: RentViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    private var isExpanded: Boolean = false
    private var category = ""
    private var rentId = -1
    private var flow = ""
    private val failedDialog = FailedDialog(this)
    private var countDownTimer: CountDownTimer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContinuePaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rentId = intent.getIntExtra(KEY_RENT_ID, -1)
        category = intent.getStringExtra(EXTRA_CATEGORY) ?: "Default Query"
        flow = intent.getStringExtra(EXTRA_FLOW) ?: "Default Query"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Lanjutkan Pembayaran"
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
                    onSuccess()
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onLoad() {
        binding.cardCountdown.visibility = View.GONE
        binding.cancelRent.visibility = View.GONE
        binding.cardRincianPesanan1.visibility = View.GONE
        binding.cardRincianHarga.visibility = View.GONE
        binding.cardLanjutkan.visibility = View.GONE
        binding.lainnyaText.visibility = View.GONE
        binding.activeLine.visibility = View.GONE
        binding.countOne.visibility = View.GONE
        binding.bayarText.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun onSuccess() {
        binding.cardCountdown.visibility = View.VISIBLE
        binding.cancelRent.visibility = View.VISIBLE
        binding.cardRincianPesanan1.visibility = View.VISIBLE
        binding.cardRincianHarga.visibility = View.VISIBLE
        binding.cardLanjutkan.visibility = View.VISIBLE
        binding.lainnyaText.visibility = View.VISIBLE
        binding.activeLine.visibility = View.VISIBLE
        binding.countOne.visibility = View.VISIBLE
        binding.bayarText.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
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
        binding.total.text = mainViewModel.formatPrice(rent.payment.amount + 5550)
        binding.priceItem.text = mainViewModel.formatPrice(rent.payment.amount)
        binding.adminFee.text = mainViewModel.formatPrice(5000)
        binding.ppn.text = mainViewModel.formatPrice(550)
        binding.priceRange.text = mainViewModel.formatPrice(rent.payment.amount + 5550)

        countDownTimer?.cancel()
        val payInTimeStamp = rent.payment.paymentTriggeredAt?.let { isoToTimestamp(it) }
        val createdAtTimestamp = isoToTimestamp(rent.createdAt)
        setCountDown(payInTimeStamp, createdAtTimestamp, rent.rentStatus)

        binding.cardRincianPesanan1.setOnClickListener {
            val detailDialog = DetailDialog.newInstance(category, rent = rent)
            detailDialog.show(supportFragmentManager, "DetailDialog")
        }
    }

    private fun setCountDown(payInTimeStamp: Long?, createdAtTimestamp: Long, rentStatus: String) {
        if (rentStatus == "pending" &&
            (System.currentTimeMillis() < createdAtTimestamp + 5 * 60 * 1000) &&
            payInTimeStamp == null) {

            val countdownTime = (createdAtTimestamp + 5 * 60 * 1000) - System.currentTimeMillis()

            countDownTimer = object : CountDownTimer(countdownTime, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val hours = millisUntilFinished / (60 * 60 * 1000)
                    val minutes = (millisUntilFinished % (60 * 60 * 1000)) / (60 * 1000)
                    val seconds = (millisUntilFinished % (60 * 1000)) / 1000

                    val timeLeftFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                    binding.countdown.text = timeLeftFormatted
                }

                override fun onFinish() {
                    failedDialog.startFailedDialog(getString(R.string.expired_text))
                    lifecycleScope.launch {
                        delay(2000)
                        failedDialog.dismissDialog()
                        finish()
                    }
                }
            }.start()
        } else if (payInTimeStamp != null) {
            if (rentStatus == "pending" &&
                (System.currentTimeMillis() < payInTimeStamp + 24 * 60 * 60 * 1000)
            ) {
                binding.pilihMetodePembayaranTitle.text = "Bayar dalam"
                val countdownTime = (payInTimeStamp.plus(24 * 60 * 60 * 1000)).minus(System.currentTimeMillis())

                countDownTimer = object : CountDownTimer(countdownTime, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val hours = millisUntilFinished / (60 * 60 * 1000)
                        val minutes = (millisUntilFinished % (60 * 60 * 1000)) / (60 * 1000)
                        val seconds = (millisUntilFinished % (60 * 1000)) / 1000

                        val timeLeftFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                        binding.countdown.text = timeLeftFormatted
                    }

                    override fun onFinish() {
                        failedDialog.startFailedDialog(getString(R.string.expired_text))
                        lifecycleScope.launch {
                            delay(2000)
                            failedDialog.dismissDialog()
                            finish()
                        }
                    }

                }.start()
            }
        }
    }

    private fun setupAction() {
        binding.expandIcon.setOnClickListener {
            if (!isExpanded) {
                isExpanded = true
                binding.expandIcon.setImageResource(R.drawable.expand_less)
                binding.divider2.visibility = View.VISIBLE
                binding.adminFeeTitle.visibility = View.VISIBLE
                binding.adminFee.visibility = View.VISIBLE
                binding.total.visibility = View.VISIBLE
                binding.totalTitle.visibility = View.VISIBLE
                binding.priceItemName.visibility = View.VISIBLE
                binding.priceItem.visibility = View.VISIBLE
                binding.ppnTitle.visibility = View.VISIBLE
                binding.ppn.visibility = View.VISIBLE
            } else {
                isExpanded = false
                binding.expandIcon.setImageResource(R.drawable.expand_more)
                binding.divider2.visibility = View.GONE
                binding.adminFeeTitle.visibility = View.GONE
                binding.adminFee.visibility = View.GONE
                binding.total.visibility = View.GONE
                binding.totalTitle.visibility = View.GONE
                binding.priceItemName.visibility = View.GONE
                binding.priceItem.visibility = View.GONE
                binding.ppnTitle.visibility = View.GONE
                binding.ppn.visibility = View.GONE
            }
        }

        binding.cancelRent.setOnClickListener {
            showAlertDialog(
                this,
                title = "Apakah Anda Yakin?",
                message = "Apakah Anda Ingin Membatalkan Pemesanan Ini?",
                positiveButtonText = "Ya, Batalkan",
                negativeButtonText = "Kembali",
                onPositive = { navigateBack(rentId) }
            )
        }

        binding.continueButton.setOnClickListener {
            flow = "history_pending"
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra(KEY_RENT_ID, rentId)
            intent.putExtra(EXTRA_CATEGORY, category)
            intent.putExtra(EXTRA_FLOW, flow)
            startActivity(intent)
            finish()
        }
    }

    private fun navigateBack(id: Int) {
        rentViewModel.cancelRent(id)
        setResult(Activity.RESULT_OK)
        finish()
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
        countDownTimer?.cancel()
    }

}