package com.android.sigemesapp.presentation.history

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        setupHistoryData()
    }

    private fun setupHistoryData() {
        rentViewModel.getAllRents()
        rentViewModel.allRentsResult.observe(viewLifecycleOwner){ result ->
            when(result){
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvHistoryCard.visibility = View.GONE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if(result.data.isNotEmpty()){
                        setupHistory(result.data)
                        binding.rvHistoryCard.visibility = View.VISIBLE
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupHistory(data: List<RentsDataItem>) {
        binding.rvHistoryCard.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = HistoryAdapter(data)
        binding.rvHistoryCard.adapter = adapter

        adapter.setOnItemClickCallback(object : HistoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: RentsDataItem) {
                sendSelectedHistory(data)
            }
        })
    }

    private fun sendSelectedHistory(data: RentsDataItem) {
        var category = ""
        if(data.cityHallPricing == null){
            category = "Mess"
        } else {
            category = "Gedung"
        }
        if(data.rentStatus == "pending"){
            val intent = Intent(requireActivity(), ContinuePaymentActivity::class.java)
            intent.putExtra(KEY_RENT_ID, data.id)
            intent.putExtra(EXTRA_CATEGORY, category)
            startActivity(intent)
        } else if (data.rentStatus == "dikonfirmasi" || data.rentStatus == "selesai"){
            val intent = Intent(requireActivity(), DetailHistoryActivity::class.java)
            intent.putExtra(KEY_RENT_ID, data.id)
            intent.putExtra(EXTRA_CATEGORY, category)
            startActivity(intent)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
}