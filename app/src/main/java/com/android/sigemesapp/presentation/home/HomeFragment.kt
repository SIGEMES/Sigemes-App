package com.android.sigemesapp.presentation.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.sigemesapp.R
import com.android.sigemesapp.data.source.remote.response.CityHallData
import com.android.sigemesapp.data.source.remote.response.GuesthouseResponse
import com.android.sigemesapp.data.source.remote.response.RoomItem
import com.android.sigemesapp.databinding.FragmentHomeBinding
import com.android.sigemesapp.presentation.auth.AuthViewModel
import com.android.sigemesapp.presentation.home.adapter.FacilityAdapter
import com.android.sigemesapp.presentation.home.adapter.PhotoAdapter
import com.android.sigemesapp.presentation.home.adapter.RoomAdapter
import com.android.sigemesapp.presentation.home.detail.DetailGedungActivity
import com.android.sigemesapp.presentation.home.detail.DetailMessActivity
import com.android.sigemesapp.presentation.home.search.SearchActivity
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.extractFacilities
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Locale

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
        setupCityHallCard()
    }

    private fun setupWelcome() {
        authViewModel.getSession().observe(viewLifecycleOwner) { user ->
            binding.username.text = "Halo, ${user.fullname}"
        }

    }

    private fun setupRecyclerView() {
        binding.rvRoomCard.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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
                        binding.progressBar1.visibility = View.VISIBLE
                        binding.filterButton.visibility = View.GONE
                    }

                    is Result.Success -> {
                        if (result.data.isNotEmpty()) {
                            binding.rvRoomCard.visibility = View.VISIBLE
                            binding.progressBar1.visibility = View.GONE
                            binding.filterButton.visibility = View.VISIBLE
                            roomList.addAll(result.data)
                            setupRoomData(result.data)
                        }
                    }

                    is Result.Error -> {
                        binding.progressBar1.visibility = View.GONE
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

    private fun setupCityHallCard() {
        homeViewModel.getCityHall(1)

        homeViewModel.cityHall.observe(viewLifecycleOwner){ result ->
            when(result){
                is Result.Loading -> {
                    binding.availableGedungCard.visibility = View.GONE
                    binding.notAvailableGedung.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.availableGedungCard.visibility = View.VISIBLE
                    setupCardVisibility(result.data)
                }

                is Result.Error -> {
                    binding.availableGedungCard.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Tidak dapat mengambil data gedung adam malik yang tersedia", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun setupCardVisibility(data: CityHallData) {
        val status = data.status
        if(status == "tersedia"){
            binding.availableGedung.visibility = View.VISIBLE
            binding.availableGedungCard.visibility = View.VISIBLE
            binding.notAvailableGedung.visibility = View.GONE
            binding.notAvailableGedungCard.visibility = View.GONE
            setAvailableCityHallData(data)
        } else {
            binding.availableGedung.visibility = View.GONE
            binding.availableGedungCard.visibility = View.GONE
            binding.notAvailableGedung.visibility = View.VISIBLE
            binding.notAvailableGedungCard.visibility = View.VISIBLE
            setNotAvailableCityHallData(data)
        }
    }

    private fun setAvailableCityHallData(data: CityHallData) {
        binding.gedungName.text = data.name

        var minPrice = Int.MAX_VALUE
        var maxPrice = Int.MIN_VALUE

        for (pricing in data.pricing) {
            if (pricing.pricePerDay < minPrice) minPrice = pricing.pricePerDay
            if (pricing.pricePerDay > maxPrice) maxPrice = pricing.pricePerDay
        }

        binding.priceRange.text = String.format(
            "Rp %s - Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(minPrice),
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(maxPrice)
        )

        binding.rvPhoto.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)

        val photoUrls = data.media.map { it.url }

        val photoAdapter = PhotoAdapter(photoUrls)

        binding.rvPhoto.adapter = photoAdapter

        binding.facilities.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)

        val facilityList = extractFacilities(data.pricing.first().facilities)

        val facilityAdapter = FacilityAdapter(facilityList)
        binding.facilities.adapter = facilityAdapter


    }

    private fun setNotAvailableCityHallData(data: CityHallData) {
        binding.gedungNameGray.text = data.name

        var minPrice = Int.MAX_VALUE
        var maxPrice = Int.MIN_VALUE

        for (pricing in data.pricing) {
            if (pricing.pricePerDay < minPrice) minPrice = pricing.pricePerDay
            if (pricing.pricePerDay > maxPrice) maxPrice = pricing.pricePerDay
        }

        binding.priceRangeGray.text = String.format(
            "Rp %s - Rp %s",
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(minPrice),
            NumberFormat.getNumberInstance(Locale("id", "ID")).format(maxPrice)
        )

        binding.rvPhotoGray.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)

        val photoUrls = data.media.map { it.url }

        val photoAdapter = PhotoAdapter(photoUrls)

        binding.rvPhotoGray.adapter = photoAdapter

        binding.facilitiesGray.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)

        val facilityList = extractFacilities(data.pricing.first().facilities)

        val facilityAdapter = FacilityAdapter(facilityList)
        binding.facilitiesGray.adapter = facilityAdapter
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

        binding.availableGedungCard.setOnClickListener {
            val intent = Intent(requireActivity(), DetailGedungActivity::class.java)
            intent.putExtra(DetailGedungActivity.KEY_CITYHALL_ID, 1)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

