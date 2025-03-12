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
import com.android.sigemesapp.R
import com.android.sigemesapp.data.source.remote.response.CityHallMediaItem
import com.android.sigemesapp.databinding.FragmentHomeBinding
import com.android.sigemesapp.presentation.auth.AuthViewModel
import com.android.sigemesapp.presentation.history.adapter.HistoryAdapter
import com.android.sigemesapp.presentation.home.search.SearchActivity
import com.android.sigemesapp.presentation.home.search.adapter.PhotoAdapter
import com.android.sigemesapp.presentation.home.search.detail.about.AboutActivity
import com.android.sigemesapp.utils.Result
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private val photoUrls: MutableList<String> = mutableListOf()
    private lateinit var photoAdapter: MenuAdapter

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
    }

    private fun setupWelcome() {
        authViewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.isLogin) {
                binding.username.text = "Halo, ${user.fullname}"
                setupAction()
                setupAboutGedung()
                setupAboutMess()
            }
        }
    }

    private fun setupAdapter() {
        binding.rvPhotoView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val adapter = MenuAdapter(photoUrls)
        binding.rvPhotoView.adapter = adapter
    }

    private fun setupAboutMess() {
        homeViewModel.getGuesthouse(1)

        homeViewModel.guesthouseResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {

                }
                is Result.Success -> {
                    result.data.guesthouseMedia.firstOrNull()?.url?.let { url ->
                        photoUrls.add(url)
                    }

                    binding.aboutMess.setOnClickListener {
                        val intent = Intent(requireActivity(), AboutActivity::class.java)
                        intent.putExtra(AboutActivity.KEY_GUESTHOUSE_ID, result.data.id)
                        startActivity(intent)
                    }
                }
                is Result.Error -> {
                    Toast.makeText(requireActivity(), "Tidak dapat mengambil data tentang Mess Pemko", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupAboutGedung() {
        val today = Calendar.getInstance().timeInMillis
        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.timeInMillis

        val ymf = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))

        val startDate = ymf.format(Date(today))
        val endDate = ymf.format(Date(tomorrow))

        homeViewModel.getCityHall(1, startDate, endDate)

        homeViewModel.cityHall.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                }
                is Result.Success -> {
                    result.data.media.firstOrNull()?.url?.let { url ->
                        photoUrls.add(url)
                        Log.e("photoUrls", "photo2 $photoUrls")

                    }
                    setupAdapter()
                    binding.aboutGedung.setOnClickListener {
                        val intent = Intent(requireActivity(), AboutActivity::class.java)
                        intent.putExtra(AboutActivity.KEY_CITYHALL_ID, result.data.id)
                        startActivity(intent)
                    }
                }
                is Result.Error -> {
                    Toast.makeText(requireActivity(), "Tidak dapat mengambil data tentang Gedung Adam Malik", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupAction() {
        binding.cardPesanMess.setOnClickListener {
            val intent = Intent(requireActivity(), SearchActivity::class.java)
            intent.putExtra(SearchActivity.EXTRA_STRING, "Mess")
            startActivity(intent)
        }

        binding.cardPesanGedung.setOnClickListener {
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


