package com.android.sigemesapp.presentation.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.sigemesapp.data.source.remote.response.RentsDataItem
import com.android.sigemesapp.data.source.remote.response.RoomItem
import com.android.sigemesapp.databinding.FragmentAccountBinding
import com.android.sigemesapp.databinding.FragmentHistoryBinding
import com.android.sigemesapp.presentation.auth.AuthViewModel
import com.android.sigemesapp.presentation.history.adapter.HistoryAdapter
import com.android.sigemesapp.presentation.home.adapter.RoomAdapter
import com.android.sigemesapp.presentation.home.search.rent.RentViewModel
import com.android.sigemesapp.utils.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val historyList = mutableListOf<RentsDataItem>()
    private val rentViewModel: RentViewModel by viewModels()

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
                        historyList.addAll(result.data)
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
        val adapter = HistoryAdapter(data)
        binding.rvHistoryCard.adapter = adapter

        adapter.setOnItemClickCallback(object : HistoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: RentsDataItem) {
                sendSelectedHistory(data)
            }
        })
    }

    private fun sendSelectedHistory(data: RentsDataItem) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}