package com.android.sigemesapp.utils.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.sigemesapp.R
import com.android.sigemesapp.data.source.remote.response.CityHallRent
import com.android.sigemesapp.data.source.remote.response.GuesthouseRentData
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

        if (category == "Mess") {
            binding.checkInTime.visibility = View.VISIBLE
            binding.checkOutTime.visibility = View.VISIBLE
            binding.roomTitle.visibility = View.VISIBLE
            binding.roomType.visibility = View.VISIBLE
            val rentData = arguments?.getParcelable<GuesthouseRentData>("EXTRA_GUESTHOUSE")
            rentData?.let {
                binding.cardKebijakanMess.visibility = View.VISIBLE
                binding.itemTitle.text = rentData.guesthouseRoomPricing.guesthouseRoom.guesthouse.name
                binding.checkInDate.text = formatDateUTC(rentData.startDate)
                binding.checkOutDate.text = formatDateUTC(rentData.endDate)
                binding.roomType.text = String.format(rentData.guesthouseRoomPricing.guesthouseRoom.name)
                binding.renterType.text = rentData.guesthouseRoomPricing.retributionType
                binding.userName.text = rentData.renter.fullname
                binding.userEmail.text = rentData.renter.email
                binding.userPhone.text = rentData.renter.phoneNumber
                binding.userGender.text = if(rentData.renter.gender == "laki_laki") "Laki-Laki" else "Perempuan"
                binding.durationCount.text = String.format("${calculateNightsUTC(rentData.startDate, rentData.endDate) } malam")
            }
        } else if (category == "Gedung") {
            val rentData = arguments?.getParcelable<CityHallRent>("EXTRA_CITYHALL")
            binding.checkInTime.visibility = View.GONE
            binding.checkOutTime.visibility = View.GONE
            binding.roomTitle.visibility = View.GONE
            binding.roomType.visibility = View.GONE
            rentData?.let {
                binding.cardKebijakanGedung.visibility = View.VISIBLE
                binding.itemTitle.text = rentData.cityHallPricing.cityHall.name
                binding.checkInDate.text = formatDateUTC(rentData.startDate)
                binding.checkOutDate.text = formatDateUTC(rentData.endDate)
                binding.renterType.text = String.format("${rentData.cityHallPricing.activityType}")
                binding.renterTypeTitle.text = "Kategori Acara: "
                binding.checkInTitle.text = "Mulai sewa"
                binding.checkOutTitle.text = "Selesai sewa"
                binding.moonIcon.setImageResource(R.drawable.day)
                binding.userName.text = rentData.renter.fullname
                binding.userEmail.text = rentData.renter.email
                binding.userPhone.text = rentData.renter.phoneNumber
                binding.userGender.text = if(rentData.renter.gender == "laki_laki") "Laki-Laki" else "Perempuan"
                binding.durationCount.text = String.format("${calculateDaysUTC(rentData.startDate, rentData.endDate) } hari")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(category: String, guesthouseRentData: GuesthouseRentData? = null, cityhallRentData: CityHallRent? = null): DetailDialog {
            val args = Bundle().apply {
                putString("EXTRA_CATEGORY", category)
                guesthouseRentData?.let { putParcelable("EXTRA_GUESTHOUSE", it) }
                cityhallRentData?.let { putParcelable("EXTRA_CITYHALL", it) }
            }
            return DetailDialog().apply {
                arguments = args
            }
        }
    }
}
