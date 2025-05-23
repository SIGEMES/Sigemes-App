package com.android.sigemesapp.presentation.home.search.rent.payment

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.sigemesapp.BuildConfig
import com.android.sigemesapp.MainActivity
import com.android.sigemesapp.R
import com.android.sigemesapp.data.model.Rent
import com.android.sigemesapp.databinding.ActivityPaymentBinding
import com.android.sigemesapp.presentation.MainViewModel
import com.android.sigemesapp.presentation.history.detail.DetailHistoryActivity
import com.android.sigemesapp.presentation.home.search.rent.RentViewModel
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_CATEGORY
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_END_DATE
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_FLOW
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_GENDER
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_PRICING_ID
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_SLOT
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_START_DATE
import com.android.sigemesapp.utils.ConstantKeys.KEY_DURATION
import com.android.sigemesapp.utils.ConstantKeys.KEY_RENT_ID
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.calculateDaysUTC
import com.android.sigemesapp.utils.calculateNightsUTC
import com.android.sigemesapp.utils.dialog.DetailDialog
import com.android.sigemesapp.utils.dialog.ExitDialog
import com.android.sigemesapp.utils.dialog.FailedDialog
import com.android.sigemesapp.utils.dialog.LoadingDialog
import com.android.sigemesapp.utils.dialog.SuccessDialog
import com.android.sigemesapp.utils.formatDateUTC
import com.android.sigemesapp.utils.isoToTimestamp
import com.midtrans.sdk.corekit.models.snap.TransactionResult.STATUS_FAILED
import com.midtrans.sdk.corekit.models.snap.TransactionResult.STATUS_INVALID
import com.midtrans.sdk.corekit.models.snap.TransactionResult.STATUS_PENDING
import com.midtrans.sdk.corekit.models.snap.TransactionResult.STATUS_SUCCESS
import com.midtrans.sdk.uikit.api.model.CustomColorTheme
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.external.UiKitApi
import com.midtrans.sdk.uikit.internal.util.UiKitConstants
import com.midtrans.sdk.uikit.internal.util.UiKitConstants.STATUS_CANCELED
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private val rentViewModel: RentViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    private var startDate = ""
    private var endDate = ""
    private var gender = ""
    private var slot = -1
    private var pricingId = -1
    private var category = ""
    private var receivedDurations = -1
    private var isExpanded: Boolean = false
    private var rentId = -1
    private var flow = ""
    private lateinit var rentDetail: Rent
    private val successDialog by lazy { SuccessDialog(this) }
    private val failedDialog by lazy { FailedDialog(this) }
    private val loadingDialog by lazy { LoadingDialog(this) }
    private var countDownTimer: CountDownTimer? = null

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { data ->
                    val transactionResult = data.getParcelableExtra<TransactionResult>(UiKitConstants.KEY_TRANSACTION_RESULT)
                    handleTransactionResult(transactionResult)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        })

        supportActionBar?.title = "Bayar Sekarang"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        gender = intent.getStringExtra(EXTRA_GENDER) ?: "Default Query"
        startDate = intent.getStringExtra(EXTRA_START_DATE) ?: "Default Query"
        endDate = intent.getStringExtra(EXTRA_END_DATE) ?: "Default Query"
        slot = intent.getIntExtra(EXTRA_SLOT, -1)
        pricingId = intent.getIntExtra(EXTRA_PRICING_ID, -1)
        category = intent.getStringExtra(EXTRA_CATEGORY) ?: "Default Query"
        receivedDurations = intent.getIntExtra(KEY_DURATION, -1)
        rentId = intent.getIntExtra(KEY_RENT_ID, -1)
        flow = intent.getStringExtra(EXTRA_FLOW) ?: "Default Query"

        if(rentId == -1){
//                flow = "booking"
            createRent()
        }else{
//                flow = "history_pending"
            observeRent(rentId)
        }
        setupAction()
    }

    private fun initializeMidtransSDK() {
        UiKitApi.Builder()
            .withContext(this.applicationContext)
            .withMerchantUrl(BuildConfig.BASE_URL)
            .withMerchantClientKey(BuildConfig.CLIENT_KEY)
            .enableLog(true)
            .withColorTheme(CustomColorTheme("#89CFF0", "#3B6790", "#89CFF0"))
            .build()
    }

    private fun createRent() {
        if(category == "Gedung"){
            slot = 1
            rentViewModel.createRent(pricingId, slot, startDate, endDate, gender, category)
        } else {
            rentViewModel.createRent(pricingId, slot, startDate, endDate, gender, category)
        }
        rentViewModel.createRentResult.observe(this){ result ->
            when (result) {
                is Result.Loading -> {
                    loadingDialog.startLoadingDialog()
                    onLoad()
                }
                is Result.Success -> {
                    val createdRent = result.data.data
                    val rentType = rentViewModel.checkRentType(result.data.data)
                    setupPaymentData(createdRent, rentType)
                    loadingDialog.dismissDialog()
                    successDialog.startSuccessDialog(getString(R.string.rent_success))
                    lifecycleScope.launch {
                        delay(2000)
                        successDialog.dismissDialog()
                        onSuccess()
                    }
                }
                is Result.Error -> {
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startMidtransPayment(snapToken: String) {
        UiKitApi.getDefaultInstance().startPaymentUiFlow(
            activity = this,
            launcher = launcher,
            snapToken = snapToken
        )
    }

    private fun observeRent(rentId: Int) {
        rentViewModel.getRent(rentId)
        rentViewModel.rentResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    onLoad()
                }
                is Result.Success -> {
                    val rentType = rentViewModel.checkRentType(result.data)
                    setupPaymentData(result.data, rentType)
                    onSuccess()
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onSuccess() {
        binding.cardRincianPesanan1.visibility = View.VISIBLE
        binding.cardRincianHarga.visibility = View.VISIBLE
        binding.cardPaymentMethod.visibility = View.VISIBLE
        binding.cardCountdown.visibility = View.VISIBLE
        binding.countOne.visibility = View.VISIBLE
        binding.bayarText.visibility = View.VISIBLE
        binding.activeLine.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.checkStatusButton.visibility = View.VISIBLE
    }

    private fun onLoad() {
        binding.cardRincianPesanan1.visibility = View.GONE
        binding.cardRincianHarga.visibility = View.GONE
        binding.cardPaymentMethod.visibility = View.GONE
        binding.cardCountdown.visibility = View.GONE
        binding.countOne.visibility = View.GONE
        binding.bayarText.visibility = View.GONE
        binding.activeLine.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.checkStatusButton.visibility = View.GONE
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
    }

    private fun setupPaymentData(rent: Rent, rentType: String) {
        rentDetail = rent
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

        binding.paymentMethodCard.setOnClickListener {
            initializeMidtransSDK()
            val snapToken = rent.payment.paymentGatewayToken
            startMidtransPayment(snapToken)
        }

        countDownTimer?.cancel()
        val payInTimeStamp = rent.payment.paymentTriggeredAt?.let { isoToTimestamp(it) }
        val createdAtTimestamp = isoToTimestamp(rent.createdAt)
        setCountDown(payInTimeStamp, createdAtTimestamp, rent.rentStatus)

        rentId = rent.id
        binding.checkStatusButton.setOnClickListener {
            checkStatus()
        }

        binding.cardRincianPesanan1.setOnClickListener {
            val detailDialog = DetailDialog.newInstance(category, rent = rentDetail)
            detailDialog.show(supportFragmentManager, "DetailDialog")
        }
    }

    private fun setCountDown(payInTimeStamp: Long?, createdAt: Long, rentStatus: String) {
        if (payInTimeStamp != null) {
            if (rentStatus == "pending" &&
                (System.currentTimeMillis() < payInTimeStamp + 24 * 60 * 60 * 1000 )
            ) {
                binding.pilihMetodePembayaranTitle.text = "Bayar dalam"
                val countdownTime = (payInTimeStamp + 24 * 60 * 60 * 1000 ) - System.currentTimeMillis()

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
        } else {
            binding.pilihMetodePembayaranTitle.text = "Pilih Bank pembayaran"
            val countdownTime = (createdAt + 5 * 60 * 1000) - System.currentTimeMillis()

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

    private fun handleTransactionResult(transactionResult: TransactionResult?) {
        if (transactionResult != null) {
            when (transactionResult.status) {
                STATUS_SUCCESS -> {
                    Toast.makeText(
                        this,
                        "Transaction Finished. ID: " + transactionResult.transactionId,
                        Toast.LENGTH_LONG
                    ).show()
                }

                STATUS_PENDING -> {
                    setPaymentMethod(transactionResult.paymentType)
                    checkStatus()
                }

                STATUS_FAILED -> {
                    Toast.makeText(
                        this,
                        "Transaction Failed. ID: " + transactionResult.transactionId,
                        Toast.LENGTH_LONG
                    ).show()
                }

                STATUS_CANCELED -> {
                    Toast.makeText(this, "Transaction Cancelled", Toast.LENGTH_LONG).show()
                }

                STATUS_INVALID -> {
                    Toast.makeText(
                        this,
                        "Transaction Invalid. ID: " + transactionResult.transactionId,
                        Toast.LENGTH_LONG
                    ).show()
                }

                else -> {
                    Toast.makeText(
                        this,
                        "Transaction ID: " + transactionResult.transactionId + ". Message: " + transactionResult.status,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkStatus() {
            rentViewModel.getRent(rentId)
            rentViewModel.rentResult.observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        loadingDialog.startLoadingDialog()
                    }
                    is Result.Success -> {
                        val status = result.data.payment.status
                        loadingDialog.dismissDialog()
                        if(status == "dibayar"){
                            successDialog.startSuccessDialog(getString(R.string.payment_success))
                            lifecycleScope.launch {
                                delay(2000)
                                successDialog.dismissDialog()
                                navigateToDetail()
                            }
                        } else if (status == "pending"){
                            countDownTimer?.cancel()
                            val payInTimeStamp = result.data.payment.paymentTriggeredAt?.let { isoToTimestamp(it) }
                            val createdAtTimestamp = isoToTimestamp(result.data.createdAt)
                            setCountDown(payInTimeStamp, createdAtTimestamp, result.data.rentStatus)
                        }

                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun navigateToDetail() {
        val intent = Intent(this, DetailHistoryActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(KEY_RENT_ID, rentId)
            putExtra(EXTRA_CATEGORY, category)
            putExtra(EXTRA_FLOW, flow)
        }
        startActivity(intent)
        finish()
    }


    private fun setPaymentMethod(paymentType: String) {
        val payment = when {
            paymentType.contains("bni", ignoreCase = true) -> "BNI Virtual Account"
            paymentType.contains("bca", ignoreCase = true) -> "BCA Virtual Account"
            paymentType.contains("mandiri", ignoreCase = true) -> "Mandiri Bill Payment"
            paymentType.contains("bri", ignoreCase = true) -> "BRI Virtual Account"
            paymentType.contains("permata", ignoreCase = true) -> "Permata Virtual Account"
            paymentType.contains("cimb", ignoreCase = true) -> "Cimb Niaga Virtual Account"
            else -> "Bank Lainnya"
        }
        binding.paymentMethodPicked.text = payment
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                showExitDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showExitDialog() {
        val exitDialog = ExitDialog(this)
        exitDialog.startExitDialog(
            onExit = { onBackPressed() },
            onCheckDetails = {
                val detailDialog = DetailDialog.newInstance(category, rent = rentDetail)
                detailDialog.show(supportFragmentManager, "DetailDialog")
            }
        )
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

//    private fun navigateBack() {
//        finish()
//    }


    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

}