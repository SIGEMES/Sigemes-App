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
import com.android.sigemesapp.R
import com.android.sigemesapp.databinding.FragmentHomeBinding
import com.android.sigemesapp.presentation.auth.AuthViewModel
import com.android.sigemesapp.presentation.home.search.SearchActivity
import com.android.sigemesapp.presentation.home.search.detail.about.AboutActivity
import com.android.sigemesapp.utils.Result
import com.bumptech.glide.Glide
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
            if (user.isLogin){
                binding.username.text = "Halo, ${user.fullname}"
                Glide.with(binding.profilePic.context)
                    .load(user.profile_picture)
                    .error(R.drawable.ic_android_black_24dp)
                    .into(binding.profilePic)
                setupAction()
                setupAboutGedung()
                setupAboutMess()
            }
        }
    }

    private fun setupAboutMess() {
        homeViewModel.getGuesthouse(1)

        homeViewModel.guesthouseResult.observe(viewLifecycleOwner){ result ->
            when(result){
                is Result.Loading -> {
                    binding.cardAboutMessPemko.visibility = View.GONE
                }

                is Result.Success -> {
                    binding.cardAboutMessPemko.visibility = View.VISIBLE
                    binding.tentangMessText.text = result.data.description
                    binding.cardAboutMessPemko.setOnClickListener {
                        val intent = Intent(requireActivity(), AboutActivity::class.java)
                        intent.putExtra(AboutActivity.KEY_GUESTHOUSE_ID, result.data.id)
                        startActivity(intent)
                    }
                }

                is Result.Error -> {
                    binding.cardAboutMessPemko.visibility = View.GONE
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

        homeViewModel.cityHall.observe(viewLifecycleOwner){ result ->
            when(result){
                is Result.Loading -> {
                    binding.cardAboutGedung.visibility = View.GONE
                }

                is Result.Success -> {
                    binding.cardAboutGedung.visibility = View.VISIBLE
                    binding.tentangGedungText.text = result.data.description
                    binding.cardAboutGedung.setOnClickListener {
                        val intent = Intent(requireActivity(), AboutActivity::class.java)
                        intent.putExtra(AboutActivity.KEY_CITYHALL_ID, result.data.id)
                        startActivity(intent)
                    }
                }

                is Result.Error -> {
                    binding.cardAboutGedung.visibility = View.GONE
                    Toast.makeText(requireActivity(), "Tidak dapat mengambil data tentang gedung adam malik", Toast.LENGTH_SHORT).show()
                }
            }
        }
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

