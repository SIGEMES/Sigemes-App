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
import com.android.sigemesapp.data.model.Rents
import com.android.sigemesapp.databinding.FragmentHistoryBinding
import com.android.sigemesapp.presentation.history.adapter.HistoryAdapter
import com.android.sigemesapp.presentation.history.detail.ContinuePaymentActivity
import com.android.sigemesapp.presentation.history.detail.DetailHistoryActivity
import com.android.sigemesapp.presentation.home.search.rent.RentViewModel
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_CATEGORY
import com.android.sigemesapp.utils.ConstantKeys.KEY_RENT_ID
import com.android.sigemesapp.utils.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val rentViewModel: RentViewModel by viewModels()
    private lateinit var adapter: HistoryAdapter

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

    private fun setupHistory(data: MutableList<Rents>) {
        if (!::adapter.isInitialized) {
            binding.rvHistoryCard.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = HistoryAdapter(data)
            binding.rvHistoryCard.adapter = adapter

            adapter.setOnItemClickCallback(object : HistoryAdapter.OnItemClickCallback {
                override fun onItemClicked(data: Rents) {
                    rentViewModel.onHistorySelected(data)
                    sendSelectedHistory()
                }
            })
        } else {
            adapter.updateData(data)
        }
    }

    private fun sendSelectedHistory() {
        rentViewModel.navigateTo.observe(viewLifecycleOwner) { (status, rentId, category) ->
            when (status) {
                "expired" -> Toast.makeText(requireContext(), "Pesanan kamu sudah kadaluwarsa", Toast.LENGTH_SHORT).show()
                "failed" -> Toast.makeText(requireContext(), "Tidak dapat melanjutkan transaksi", Toast.LENGTH_SHORT).show()
                "continue_payment" -> {
                    val intent = Intent(requireContext(), ContinuePaymentActivity::class.java).apply {
                        putExtra(KEY_RENT_ID, rentId)
                        putExtra(EXTRA_CATEGORY, category)
                    }
                    startActivityForResult(intent, 1)
                }
                "detail" -> {
                    val intent = Intent(requireContext(), DetailHistoryActivity::class.java).apply {
                        putExtra(KEY_RENT_ID, rentId)
                        putExtra(EXTRA_CATEGORY, category)
                    }
                    startActivityForResult(intent, 1)
                }
            }
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