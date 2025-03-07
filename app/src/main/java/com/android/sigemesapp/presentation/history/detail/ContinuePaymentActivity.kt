package com.android.sigemesapp.presentation.history.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.sigemesapp.R
import com.android.sigemesapp.data.source.remote.response.CityHallRent
import com.android.sigemesapp.data.source.remote.response.GuesthouseRentData
import com.android.sigemesapp.databinding.ActivityContinuePaymentBinding
import com.android.sigemesapp.presentation.home.search.rent.RentViewModel
import com.android.sigemesapp.presentation.home.search.rent.payment.PaymentActivity
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.calculateDaysUTC
import com.android.sigemesapp.utils.calculateNightsUTC
import com.android.sigemesapp.utils.dialog.DetailDialog
import com.android.sigemesapp.utils.formatDateUTC
import com.android.sigemesapp.utils.showAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class ContinuePaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContinuePaymentBinding
    private val rentViewModel: RentViewModel by viewModels()
    private var isExpanded: Boolean = false
    private var category = ""
    private var rentId = -1
    private var before = ""

    companion object {
        const val KEY_RENT_ID = "key_rent_id"
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_BEFORE = "extra_before"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContinuePaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rentId = intent.getIntExtra(KEY_RENT_ID, -1)
        category = intent.getStringExtra(EXTRA_CATEGORY) ?: "Default Query"
        before = intent.getStringExtra(EXTRA_BEFORE) ?: "Default Query"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Lanjutkan Pembayaran"
        if(category == "Mess"){
            observeGuesthouseRentData()
        }else if (category == "Gedung"){
            observeCityhallRentData()
        }
        setupAction()
        
    }

    private fun observeCityhallRentData() {
        rentViewModel.getDetailCityHallRent(rentId)
        rentViewModel.cityhallDetailRent.observe(this){ result ->
            when (result) {
                is Result.Loading -> {
                    onLoad()
                }
                is Result.Success -> {
                    val cityHall = result.data
                    setCityHallDetail(cityHall)
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

    private fun observeGuesthouseRentData() {
        rentViewModel.getDetailGuesthouseRent(rentId)
        rentViewModel.guesthouseDetailRent.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    onLoad()
                }
                is Result.Success -> {
                    val guesthouseRent = result.data
                    setGuesthouseDetail(guesthouseRent)
                    onSuccess()
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setGuesthouseDetail(guesthouseRent: GuesthouseRentData) {
        binding.itemTitle.text = guesthouseRent.guesthouseRoomPricing.guesthouseRoom.guesthouse.name
        binding.checkInDate.text = String.format(formatDateUTC(guesthouseRent.startDate))
        binding.checkOutDate.text = String.format(formatDateUTC(guesthouseRent.endDate))
        binding.priceRange.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(guesthouseRent.payment.amount + 5550))
        binding.total.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(guesthouseRent.payment.amount + 5550))
        binding.priceItem.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(guesthouseRent.payment.amount))
        binding.adminFee.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(5000))
        binding.ppn.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(550))
        binding.priceItemName.text = String.format("Kamar ${guesthouseRent.guesthouseRoomPricing.guesthouseRoom.name}, ${guesthouseRent.guesthouseRoomPricing.retributionType}, ${calculateNightsUTC(guesthouseRent.startDate, guesthouseRent.endDate)} malam")
        binding.cardRincianPesanan1.setOnClickListener {
            val detailDialog = DetailDialog.newInstance("Mess", guesthouseRentData  = guesthouseRent)
            detailDialog.show(supportFragmentManager, "DetailDialog")
        }
    }

    private fun setCityHallDetail(cityHall: CityHallRent) {
        binding.itemTitle.text = cityHall.cityHallPricing.cityHall.name
        binding.checkInDate.text = String.format(formatDateUTC(cityHall.startDate))
        binding.checkOutDate.text = String.format(formatDateUTC(cityHall.endDate))
        binding.priceRange.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(cityHall.payment.amount + 5550))
        binding.total.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(cityHall.payment.amount + 5550))
        binding.priceItem.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(cityHall.payment.amount))
        binding.adminFee.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(5000))
        binding.ppn.text = String.format("Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(550))
        binding.priceItemName.text = String.format("Kamar ${cityHall.cityHallPricing.cityHall.name}, Acara ${cityHall.cityHallPricing.activityType}, ${calculateDaysUTC(cityHall.startDate, cityHall.endDate)} hari")
        binding.cardRincianPesanan1.setOnClickListener {
            val detailDialog = DetailDialog.newInstance("Gedung", cityhallRentData = cityHall)
            detailDialog.show(supportFragmentManager, "DetailDialog")
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
                message = "Apakah Anda Ingin Membatalkan Pesanan?",
                positiveButtonText = "Ya, Batalkan",
                negativeButtonText = "Kembali",
                onPositive = { navigateBack(rentId) }
            )
        }

        binding.continueButton.setOnClickListener {
            before = "continue"
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra(KEY_RENT_ID, rentId)
            intent.putExtra(EXTRA_CATEGORY, category)
            intent.putExtra(EXTRA_BEFORE, before)
            startActivity(intent)
        }
    }

    private fun navigateBack(id: Int) {
        if(category == "Mess"){
            rentViewModel.cancelGuesthouseRent(id)
        } else {
            rentViewModel.cancelCityHallRent(id)
        }

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

}