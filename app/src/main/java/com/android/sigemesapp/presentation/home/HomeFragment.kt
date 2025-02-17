package com.android.sigemesapp.presentation.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.sigemesapp.data.source.remote.response.GuesthouseResponse
import com.android.sigemesapp.data.source.remote.response.RoomItem
import com.android.sigemesapp.databinding.FragmentHomeBinding
import com.android.sigemesapp.presentation.auth.AuthViewModel
import com.android.sigemesapp.presentation.home.adapter.RoomAdapter
import com.android.sigemesapp.presentation.home.detail.DetailMessActivity
import com.android.sigemesapp.presentation.home.search.SearchActivity
import com.android.sigemesapp.utils.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val roomList = mutableListOf<RoomItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        setupWelcome()
        setupAction()
        setupRecyclerView()
        setupRoomCard()
    }

    private fun setupWelcome() {
        authViewModel.getSession().observe(viewLifecycleOwner) { user ->
            binding.username.text = "Halo, ${user.fullname}"
        }

    }

    private fun setupRecyclerView() {
        binding.rvRoomCard.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupRoomCard() {
        homeViewModel.getGuesthouses()

        homeViewModel.allGuesthousesResult.observe(viewLifecycleOwner){ result ->
            when(result){
                is Result.Loading -> {

                }

                is Result.Success -> {
                    if (result.data.isNotEmpty()) {
                        binding.rvRoomCard.visibility = View.VISIBLE
                        setListAvailableRoom(result.data)
                        Log.d("SetupRoomCard", "${result.data}")
                    } else {
                        binding.rvRoomCard.visibility = View.GONE
                    }
                }

                is Result.Error -> {

                }
            }
        }
    }

    private fun setListAvailableRoom(data: List<GuesthouseResponse>) {
        val guesthouseIds = data.map { it.data.id }

        for (id in guesthouseIds){
            homeViewModel.getRooms(id)
            homeViewModel.allRooms.observe(requireActivity()){ result ->
                when(result){
                    is Result.Loading -> {
                        binding.rvRoomCard.visibility = View.GONE
                        binding.progressBar.visibility = View.VISIBLE
                        binding.filterButton.visibility = View.GONE
                    }

                    is Result.Success -> {
                        if (result.data.isNotEmpty()) {
                            binding.rvRoomCard.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                            binding.filterButton.visibility = View.VISIBLE
                            roomList.addAll(result.data)
                            setupRoomData(result.data)
                        }
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.filterButton.visibility = View.GONE
                        Toast.makeText(requireContext(), "Tidak dapat mengambil data kamar yang tersedia", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupRoomData(data: List<RoomItem>) {
        val adapter = RoomAdapter(data)
        binding.rvRoomCard.adapter = adapter

        adapter.setOnItemClickCallback(object : RoomAdapter.OnItemClickCallback {
            override fun onItemClicked(data: RoomItem) {
                sendSelectedRoom(data)
            }
        })
    }

    private fun sendSelectedRoom(data: RoomItem) {
        val intent = Intent(requireActivity(), DetailMessActivity::class.java)
        intent.putExtra(DetailMessActivity.KEY_ROOM_ID, data.id)
        intent.putExtra(DetailMessActivity.KEY_GUESTHOUSE_ID, data.guesthouseId)
        startActivity(intent)
    }

    private fun setupAction() {
        binding.cardMessMenu.setOnClickListener {
            val intent = Intent(requireActivity(), SearchActivity::class.java)
            intent.putExtra(SearchActivity.EXTRA_STRING, "Mess")
            startActivity(intent)
        }

        binding.cardBuildingMenu.setOnClickListener {
            val intent = Intent(requireActivity(), SearchActivity::class.java)
            intent.putExtra(SearchActivity.EXTRA_STRING, "Gedung")
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

