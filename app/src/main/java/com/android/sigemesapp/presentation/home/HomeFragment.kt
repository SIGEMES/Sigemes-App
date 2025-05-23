package com.android.sigemesapp.presentation.home

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
import com.android.sigemesapp.databinding.FragmentHomeBinding
import com.android.sigemesapp.presentation.MainViewModel
import com.android.sigemesapp.presentation.auth.AuthViewModel
import com.android.sigemesapp.presentation.home.search.SearchActivity
import com.android.sigemesapp.presentation.home.search.detail.about.AboutActivity
import com.android.sigemesapp.utils.ConstantKeys.EXTRA_CATEGORY
import com.android.sigemesapp.utils.ConstantKeys.KEY_CITYHALL_ID
import com.android.sigemesapp.utils.ConstantKeys.KEY_GUESTHOUSE_ID
import com.android.sigemesapp.utils.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

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
                binding.username.text = String.format("Halo, ${user.fullname}")
                setupAction()
                setupAboutGedung()
                setupAboutMess()
            }
        }
    }

    private fun setupAdapter() {
        binding.rvPhotoView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val adapter = MenuAdapter(homeViewModel.dashboardPhoto)
        binding.rvPhotoView.adapter = adapter
        binding.rvPhotoView.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    private fun setupAboutMess() {
        homeViewModel.getGuesthouse(1)

        homeViewModel.guesthouseResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {

                }
                is Result.Success -> {
                    result.data.guesthouseMedia.firstOrNull()?.url?.let { url ->
                        sendUrl(url)
                    }

                    binding.aboutMess.setOnClickListener {
                        val intent = Intent(requireActivity(), AboutActivity::class.java)
                        intent.putExtra(KEY_GUESTHOUSE_ID, result.data.id)
                        startActivity(intent)
                    }
                }
                is Result.Error -> {
                    Toast.makeText(requireActivity(), "Tidak dapat mengambil data tentang Mess Pemko", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun sendUrl(url: String) {
        homeViewModel.tryAddDashboardPhoto(url) {
            setupAdapter()
        }
    }

    private fun setupAboutGedung() {
        val (startDate, endDate) = mainViewModel.getTodayAndTomorrowDates()

        homeViewModel.getCityHall(1, startDate, endDate)

        homeViewModel.cityHall.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                }
                is Result.Success -> {
                    result.data.media.firstOrNull()?.url?.let { url ->
                        sendUrl(url)
                    }

                    binding.aboutGedung.setOnClickListener {
                        val intent = Intent(requireActivity(), AboutActivity::class.java)
                        intent.putExtra(KEY_CITYHALL_ID, result.data.id)
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
            intent.putExtra(EXTRA_CATEGORY, "Mess")
            startActivity(intent)
        }

        binding.cardPesanGedung.setOnClickListener {
            val intent = Intent(requireActivity(), SearchActivity::class.java)
            intent.putExtra(EXTRA_CATEGORY, "Gedung")
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.clearDashboardPhotos()
        setupWelcome()
    }
}


