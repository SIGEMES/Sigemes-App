package com.android.sigemesapp.presentation.home.search.rent.payment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.sigemesapp.BuildConfig
import com.android.sigemesapp.R
import com.android.sigemesapp.data.source.remote.response.CityHallRent
import com.android.sigemesapp.databinding.ActivityPaymentBinding
import com.android.sigemesapp.presentation.home.search.rent.RentViewModel
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.formatDate
import com.android.sigemesapp.utils.dialog.LoadingDialog
import com.android.sigemesapp.utils.dialog.SuccessDialog
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Bayar Sekarang"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        gender = intent.getStringExtra(EXTRA_GENDER) ?: "Default Query"
        startDate = intent.getStringExtra(EXTRA_START_DATE) ?: "Default Query"
        endDate = intent.getStringExtra(EXTRA_END_DATE) ?: "Default Query"
        slot = intent.getIntExtra(EXTRA_SLOT, -1)
        pricingId = intent.getIntExtra(EXTRA_PRICING_ID, -1)
        category = intent.getStringExtra(EXTRA_CATEGORY) ?: "Default Query"
        receivedDurations = intent.getIntExtra(KEY_DURATION, -1)
        Log.e("Durations2", "durations $receivedDurations")

        initializeMidtransSDK()

        if (category == "Mess") {
            createMessRent()
        } else {
            createCityhallRent()
            Log.e("CreateCityHallReques", "$category")
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
        TODO("Not yet implemented")
    }

    private fun createCityhallRent() {
        rentViewModel.createCityHallRent(pricingId, 1, startDate, endDate, gender)
        rentViewModel.cityHallRentResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    loadingDialog.startLoadingDialog()
                    binding.cardRincianPesanan1.visibility = View.GONE
                    binding.cardRincianHarga.visibility = View.GONE
                    binding.payButton.visibility = View.GONE
                }
                is Result.Success -> {
                    val cityHallRent = result.data.data
                    setupPaymentData(cityHallRent)
                    loadingDialog.dismissDialog()
                    successDialog.startSuccessDialog(getString(R.string.rent_success))
                    lifecycleScope.launch {
                        delay(2000)
                        successDialog.dismissDialog()
                        binding.cardRincianPesanan1.visibility = View.VISIBLE
                        binding.cardRincianHarga.visibility = View.VISIBLE
                        binding.payButton.visibility = View.VISIBLE
                    }
                }
                is Result.Error -> {
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupPaymentData(cityHallRent: CityHallRent) {
        binding.priceItemName.text = String.format("${cityHallRent.cityHallPricing.cityHall.name}, Acara ${cityHallRent.cityHallPricing.activityType}, $receivedDurations hari")
        binding.checkInDate.text = formatDate(startDate)
        binding.checkOutDate.text = formatDate(endDate)
        binding.itemTitle.text = cityHallRent.cityHallPricing.cityHall.name
        binding.total.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(cityHallRent.payment.amount))
        binding.adminFee.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(5000))
        binding.priceRange.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(cityHallRent.payment.amount))

        binding.payButton.setOnClickListener {
            val snapToken = cityHallRent.payment.paymentGatewayToken
            startMidtransPayment(snapToken)
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