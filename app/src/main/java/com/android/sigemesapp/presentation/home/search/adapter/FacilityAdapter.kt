package com.android.sigemesapp.presentation.home.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.sigemesapp.databinding.ItemFacilityBinding
import com.android.sigemesapp.utils.getFacilityIcon

class FacilityAdapter(private val facilities: List<String>) : RecyclerView.Adapter<FacilityAdapter.FacilityViewHolder>() {

    class FacilityViewHolder(private val binding: ItemFacilityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(facility: String) {

            val iconRes = getFacilityIcon(facility)
            if (iconRes != 0) {
                binding.textIcon.visibility = View.VISIBLE
                binding.textIcon.text = facility
                binding.icon.setImageResource(iconRes)
            } else {
                binding.textIcon.visibility = View.GONE
                binding.icon.setImageDrawable(null)
            }

            }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacilityViewHolder {
        val binding = ItemFacilityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FacilityViewHolder(binding)
    }

    override fun getItemCount(): Int = facilities.size

    override fun onBindViewHolder(holder: FacilityViewHolder, position: Int) {
        holder.bind(facilities[position])
    }
}