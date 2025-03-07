package com.android.sigemesapp.presentation.home.search.rent.payment

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.sigemesapp.BuildConfig
import com.android.sigemesapp.R
import com.android.sigemesapp.data.source.remote.response.CityHallRent
import com.android.sigemesapp.data.source.remote.response.GuesthouseRentData
import com.android.sigemesapp.databinding.ActivityPaymentBinding
import com.android.sigemesapp.presentation.home.search.rent.RentViewModel
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.calculateNightsUTC
import com.android.sigemesapp.utils.dialog.DetailDialog
import com.android.sigemesapp.utils.dialog.ExitDialog
import com.android.sigemesapp.utils.dialog.LoadingDialog
import com.android.sigemesapp.utils.dialog.SuccessDialog
import com.android.sigemesapp.utils.formatDateUTC
import com.midtrans.sdk.uikit.api.model.CustomColorTheme
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.external.UiKitApi
import com.midtrans.sdk.uikit.internal.util.UiKitConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@AndroidEntryPoint
class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private val rentViewModel: RentViewModel by viewModels()
    private var startDate = ""
    private var endDate = ""
    private var gender = ""
    private var slot = -1
    private var pricingId = -1
    private var category = ""
    private var receivedDurations = -1
    private var isExpanded: Boolean = false
    private var rentId = -1
    private var before = ""
    private lateinit var guesthouseRentData: GuesthouseRentData
    private lateinit var cityHallRentData: CityHallRent
    private val successDialog = SuccessDialog(this)
    private val loadingDialog = LoadingDialog(this)

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { data ->
                    val transactionResult = data.getParcelableExtra<TransactionResult>(UiKitConstants.KEY_TRANSACTION_RESULT)
                    handleTransactionResult(transactionResult)
                }
            }
        }

    companion object {
        const val EXTRA_PRICING_ID = "extra_pricing_id"
        const val EXTRA_SLOT = "extra_slot"
        const val EXTRA_START_DATE = "extra_start_date"
        const val EXTRA_END_DATE = "extra_end_date"
        const val EXTRA_GENDER = "extra_gender"
        const val EXTRA_CATEGORY = "extra_category"
        const val KEY_DURATION = "key_duration"
        const val KEY_RENT_ID = "key_rent_id"
        const val EXTRA_STRING = "extra_string"
        const val EXTRA_BEFORE = "extra_before"
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
        before = intent.getStringExtra(EXTRA_BEFORE) ?: "Default Query"
        Log.e("Durations2", "durations $receivedDurations")

        initializeMidtransSDK()

        if (category == "Mess") {
            if(rentId == -1){
                createMessRent()
            }else{
                observeGuesthouseRent(rentId)
            }
        } else {
            if(rentId == -1){
                createCityhallRent()
            }else{
                observeCityHallRent(rentId)
            }
        }

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

    private fun initializeMidtransSDK() {
        UiKitApi.Builder()
            .withContext(this.applicationContext)
            .withMerchantUrl(BuildConfig.BASE_URL)
            .withMerchantClientKey(BuildConfig.CLIENT_KEY)
            .enableLog(true)
            .withColorTheme(CustomColorTheme("#89CFF0", "#3B6790", "#89CFF0"))
            .build()
    }

    private fun createMessRent() {
        rentViewModel.createGuesthouseRent(pricingId, 1, startDate, endDate, gender)
        rentViewModel.guesthouseRentResult.observe(this){ result ->
            when (result) {
                is Result.Loading -> {
                    loadingDialog.startLoadingDialog()
                    onLoad()
                }
                is Result.Success -> {
                    val guesthouse = result.data.data
                    setupGuesthousePaymentData(guesthouse)
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

    private fun createCityhallRent() {
        rentViewModel.createCityHallRent(pricingId, 1, startDate, endDate, gender)
        rentViewModel.cityHallRentResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    loadingDialog.startLoadingDialog()
                    onLoad()
                }
                is Result.Success -> {
                    val cityHallRent = result.data.data
                    setupCityHallPaymentData(cityHallRent)
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

    private fun handleTransactionResult(transactionResult: TransactionResult?) {
        when (transactionResult?.status) {
            UiKitConstants.STATUS_SUCCESS -> {
                Toast.makeText(this, "Pembayaran berhasil! ID: ${transactionResult.transactionId}", Toast.LENGTH_LONG).show()
            }
            UiKitConstants.STATUS_PENDING -> {
                val paymentType = transactionResult.paymentType
                binding.paymentMethodPicked.text = paymentType
                Toast.makeText(this, "Pembayaran pending. ID: ${transactionResult.transactionId}", Toast.LENGTH_LONG).show()
            }
            UiKitConstants.STATUS_FAILED -> {
                Toast.makeText(this, "Pembayaran gagal. ID: ${transactionResult.transactionId}", Toast.LENGTH_LONG).show()
            }
            UiKitConstants.STATUS_CANCELED -> {
                Toast.makeText(this, "Pembayaran dibatalkan", Toast.LENGTH_LONG).show()
            }
            UiKitConstants.STATUS_INVALID -> {
                Toast.makeText(this, "Pembayaran tidak valid. ID: ${transactionResult.transactionId}", Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(this, "Status pembayaran tidak diketahui", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun observeGuesthouseRent(rentId: Int) {
        rentViewModel.getDetailGuesthouseRent(rentId)
        rentViewModel.guesthouseDetailRent.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    onLoad()
                }
                is Result.Success -> {
                    val guesthouseRent = result.data
                    setupGuesthousePaymentData(guesthouseRent)
                    onSuccess()

                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun observeCityHallRent(rentId: Int) {
        rentViewModel.getDetailCityHallRent(rentId)
        rentViewModel.cityhallDetailRent.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    onLoad()
                }
                is Result.Success -> {
                    val cityHallRent = result.data
                    setupCityHallPaymentData(cityHallRent)
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
        binding.payButton.visibility = View.VISIBLE
        binding.cardPaymentMethod.visibility = View.VISIBLE
        binding.cardCountdown.visibility = View.VISIBLE
        binding.countOne.visibility = View.VISIBLE
        binding.bayarText.visibility = View.VISIBLE
        binding.activeLine.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    private fun onLoad() {
        binding.cardRincianPesanan1.visibility = View.GONE
        binding.cardRincianHarga.visibility = View.GONE
        binding.payButton.visibility = View.GONE
        binding.cardPaymentMethod.visibility = View.GONE
        binding.cardCountdown.visibility = View.GONE
        binding.countOne.visibility = View.GONE
        binding.bayarText.visibility = View.GONE
        binding.activeLine.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun setupCityHallPaymentData(cityHallRent: CityHallRent) {
        cityHallRentData = cityHallRent
        binding.priceItemName.text = String.format("${cityHallRent.cityHallPricing.cityHall.name}, Acara ${cityHallRent.cityHallPricing.activityType}, ${calculateNightsUTC(cityHallRent.startDate, cityHallRent.endDate)} hari")
        binding.checkInDate.text = formatDateUTC(cityHallRent.startDate)
        binding.checkOutDate.text = formatDateUTC(cityHallRent.endDate)
        binding.itemTitle.text = cityHallRent.cityHallPricing.cityHall.name
        binding.total.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(cityHallRent.payment.amount + 5550))
        binding.priceItem.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(cityHallRent.payment.amount))
        binding.adminFee.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(5000))
        binding.ppn.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(550))
        binding.priceRange.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(cityHallRent.payment.amount + 5550))

        binding.payButton.setOnClickListener {
            val snapToken = cityHallRent.payment.paymentGatewayToken
            startMidtransPayment(snapToken)
        }

        binding.cardRincianPesanan1.setOnClickListener {
            val detailDialog = DetailDialog.newInstance("Gedung", cityhallRentData = cityHallRent)
            detailDialog.show(supportFragmentManager, "DetailDialog")
        }
    }

    private fun setupGuesthousePaymentData(guesthouseRent: GuesthouseRentData) {
        guesthouseRentData = guesthouseRent
        binding.itemTitle.text = guesthouseRent.guesthouseRoomPricing.guesthouseRoom.guesthouse.name
        binding.checkInDate.text = formatDateUTC(guesthouseRent.startDate)
        binding.checkOutDate.text = formatDateUTC(guesthouseRent.endDate)
        binding.priceItemName.text = String.format("Kamar ${guesthouseRent.guesthouseRoomPricing.guesthouseRoom.name}, ${guesthouseRent.guesthouseRoomPricing.retributionType}, ${calculateNightsUTC(guesthouseRent.startDate, guesthouseRent.endDate)} malam")
        binding.total.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(guesthouseRent.payment.amount + 5550))
        binding.priceItem.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(guesthouseRent.payment.amount))
        binding.adminFee.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(5000))
        binding.ppn.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(550))
        binding.priceRange.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(guesthouseRent.payment.amount + 5550))

        binding.payButton.setOnClickListener {
            val snapToken = guesthouseRent.payment.paymentGatewayToken
            startMidtransPayment(snapToken)
        }

        binding.cardRincianPesanan1.setOnClickListener {
            val detailDialog = DetailDialog.newInstance("Mess", guesthouseRentData = guesthouseRent)
            detailDialog.show(supportFragmentManager, "DetailDialog")
        }
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
            onExit = { navigateBack() },
            onCheckDetails = {
                if(category == "Mess"){
                    val detailDialog = DetailDialog.newInstance("Mess", guesthouseRentData = guesthouseRentData)
                    detailDialog.show(supportFragmentManager, "DetailDialog")
                } else{
                    val detailDialog = DetailDialog.newInstance("Gedung", cityhallRentData = cityHallRentData)
                    detailDialog.show(supportFragmentManager, "DetailDialog")
                }

            }
        )
    }

    private fun navigateBack() {
        if(before == "continue"){
            finish()
        } else {
//            if(category == "Mess"){
//                val intent = Intent(this, SearchActivity::class.java).apply {
//                    putExtra(EXTRA_STRING, "Mess")
//                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                }
//                startActivity(intent)
//
//            }else{
//                val intent = Intent(this, SearchActivity::class.java).apply {
//                    putExtra(EXTRA_STRING, "Gedung")
//                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                }
//                startActivity(intent)
//
//            }
            finish()
        }

    }

}