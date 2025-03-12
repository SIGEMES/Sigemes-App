package com.android.sigemesapp.presentation.history

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.sigemesapp.data.source.remote.response.RentsDataItem
import com.android.sigemesapp.databinding.FragmentHistoryBinding
import com.android.sigemesapp.presentation.history.adapter.HistoryAdapter
import com.android.sigemesapp.presentation.history.detail.ContinuePaymentActivity
import com.android.sigemesapp.presentation.history.detail.DetailHistoryActivity
import com.android.sigemesapp.presentation.home.search.rent.RentViewModel
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.isoToTimestamp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val rentViewModel: RentViewModel by viewModels()
    private lateinit var adapter: HistoryAdapter

    companion object {
        const val KEY_RENT_ID = "key_rent_id"
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_BEFORE = "extra_before"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        setupHistoryData()
    }

    private fun setupHistoryData() {
        rentViewModel.getAllRents()
        rentViewModel.allRentsResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvHistoryCard.visibility = View.GONE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (result.data.isNotEmpty()) {
                        setupHistory(result.data.toMutableList())
                        binding.rvHistoryCard.visibility = View.VISIBLE
                    } else {
                        binding.rvHistoryCard.visibility = View.GONE
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupHistory(data: MutableList<RentsDataItem>) {
        if (!::adapter.isInitialized) {
            binding.rvHistoryCard.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = HistoryAdapter(data)
            binding.rvHistoryCard.adapter = adapter

            adapter.setOnItemClickCallback(object : HistoryAdapter.OnItemClickCallback {
                override fun onItemClicked(data: RentsDataItem) {
                    sendSelectedHistory(data)
                }
            })
        } else {
            adapter.updateData(data)
        }
    }

    private fun sendSelectedHistory(data: RentsDataItem) {
        val createdAtTimestamp = isoToTimestamp(data.createdAt)
        val paymentTriggeredAtTimestamp = data.payment.paymentTriggeredAt?.let { isoToTimestamp(it) }

        val isExpired = data.rentStatus == "pending" &&
                ((System.currentTimeMillis() > createdAtTimestamp + 5 * 60 * 1000) ||
                        (paymentTriggeredAtTimestamp != null && System.currentTimeMillis() > paymentTriggeredAtTimestamp + 24 * 60 * 60 * 1000))

        if (isExpired) {
            Toast.makeText(requireContext(), "Pesana kamu sudah kadaluwarsa", Toast.LENGTH_SHORT).show()
            return
        }

        val category = if (data.cityHallPricing == null) "Mess" else "Gedung"

        val intent = when (data.rentStatus) {
            "pending" -> Intent(requireActivity(), ContinuePaymentActivity::class.java)
            "dikonfirmasi", "selesai" -> Intent(requireActivity(), DetailHistoryActivity::class.java)
            else -> null
        }

        if (intent != null) {
            intent.apply {
                putExtra(KEY_RENT_ID, data.id)
                putExtra(EXTRA_CATEGORY, category)
                startActivityForResult(this, 1)
            }
        } else {
            Toast.makeText(requireContext(), "Tidak dapat melanjutkan transaksi", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            setupHistoryData()
        }
    }

    override fun onResume() {
        super.onResume()
        setupHistoryData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}