package com.android.sigemesapp.utils.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.sigemesapp.R
import com.android.sigemesapp.data.model.Rent
import com.android.sigemesapp.databinding.DetailRentDialogBinding
import com.android.sigemesapp.utils.calculateDaysUTC
import com.android.sigemesapp.utils.calculateNightsUTC
import com.android.sigemesapp.utils.formatDateUTC
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DetailDialog : BottomSheetDialogFragment() {

    private var _binding: DetailRentDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailRentDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val category = arguments?.getString("EXTRA_CATEGORY")

        binding.closeButton.setOnClickListener {
            dismiss()
        }

        val rentData = arguments?.getParcelable<Rent>("EXTRA_RENT")

        rentData?.let {
            binding.userName.text = it.renter.fullname
            binding.userEmail.text = it.renter.email
            binding.userPhone.text = it.renter.phoneNumber
            binding.userGender.text = if (it.renter.gender == "laki_laki") "Laki-Laki" else "Perempuan"
            binding.checkInDate.text = formatDateUTC(it.startDate)
            binding.checkOutDate.text = formatDateUTC(it.endDate)
        }

        if (category == "Mess") {
            binding.checkInTime.visibility = View.VISIBLE
            binding.checkOutTime.visibility = View.VISIBLE
            binding.roomTitle.visibility = View.VISIBLE
            binding.roomType.visibility = View.VISIBLE

            rentData?.let {
                binding.cardKebijakanMess.visibility = View.VISIBLE
                binding.itemTitle.text = it.guesthouseRoomPricing?.guesthouseRoom?.guesthouse?.name
                binding.roomType.text = it.guesthouseRoomPricing?.guesthouseRoom?.name
                binding.renterType.text = it.guesthouseRoomPricing?.retributionType
                binding.durationCount.text = "${calculateNightsUTC(it.startDate, it.endDate)} malam"
            }
        } else if (category == "Gedung") {
            binding.checkInTime.visibility = View.GONE
            binding.checkOutTime.visibility = View.GONE
            binding.roomTitle.visibility = View.GONE
            binding.roomType.visibility = View.GONE

            rentData?.let {
                binding.cardKebijakanGedung.visibility = View.VISIBLE
                binding.itemTitle.text = it.cityHallPricing?.cityHall?.name
                binding.renterType.text = it.cityHallPricing?.activityType
                binding.renterTypeTitle.text = "Kategori Acara: "
                binding.checkInTitle.text = "Mulai sewa"
                binding.checkOutTitle.text = "Selesai sewa"
                binding.moonIcon.setImageResource(R.drawable.day)
                binding.durationCount.text = "${calculateDaysUTC(it.startDate, it.endDate)} hari"
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(category: String, rent: Rent? = null): DetailDialog {
            val args = Bundle().apply {
                putString("EXTRA_CATEGORY", category)
                rent?.let { putParcelable("EXTRA_RENT", it) }
            }
            return DetailDialog().apply {
                arguments = args
            }
        }
    }
}
