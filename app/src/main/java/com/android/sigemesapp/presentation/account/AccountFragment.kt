package com.android.sigemesapp.presentation.account

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.android.sigemesapp.R
import com.android.sigemesapp.databinding.FragmentAccountBinding
import com.android.sigemesapp.presentation.auth.AuthViewModel
import com.android.sigemesapp.utils.showAlertDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAction()
    }

    private fun setupAction() {
        binding.logoutButton.setOnClickListener{
            showAlertDialog(
                requireContext(),
                title = "Apakah Anda Yakin?",
                message = "Apakah Anda Ingin Keluar?",
                positiveButtonText = "Ya",
                negativeButtonText = "Batal",
                onPositive = { authViewModel.logout() }
            )
        }
    }
}