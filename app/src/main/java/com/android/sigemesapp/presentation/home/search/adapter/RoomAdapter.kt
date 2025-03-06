package com.android.sigemesapp.presentation.home.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.sigemesapp.data.source.remote.response.RoomItem
import com.android.sigemesapp.databinding.ItemRoomBinding
import com.android.sigemesapp.utils.Result
import com.android.sigemesapp.utils.extractFacilities
import java.text.NumberFormat
import java.util.Locale

class RoomAdapter(private val listRoom: List<RoomItem>) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null

    class RoomViewHolder(private val binding: ItemRoomBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(room: RoomItem, callback: OnItemClickCallback?) {
            binding.roomType.text = room.name

            var minPrice = Int.MAX_VALUE
            var maxPrice = Int.MIN_VALUE

            for (pricing in room.pricing) {
                if (pricing.isActive) {
                    if (pricing.pricePerDay < minPrice) minPrice = pricing.pricePerDay
                    if (pricing.pricePerDay > maxPrice) maxPrice = pricing.pricePerDay
                }
            }

            binding.priceRange.text = String.format(
                "Rp %s - Rp %s",
                NumberFormat.getNumberInstance(Locale("id", "ID")).format(minPrice),
                NumberFormat.getNumberInstance(Locale("id", "ID")).format(maxPrice)
            )

            binding.rvPhoto.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)

            val photoUrls = room.media.map { it.url }

            val photoAdapter = PhotoAdapter(photoUrls)

            binding.rvPhoto.adapter = photoAdapter

            binding.facilities.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)

            val facilityList = extractFacilities(room.facilities)

            val facilityAdapter = FacilityAdapter(facilityList)
            binding.facilities.adapter = facilityAdapter

            binding.root.setOnClickListener {
                callback?.onItemClicked(room)
            }

        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemRoomBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return RoomViewHolder(binding)
    }

    override fun getItemCount(): Int = listRoom.size

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = listRoom[position]
        holder.bind(room, onItemClickCallback)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: RoomItem)
    }

    fun setOnItemClickCallback(callback: OnItemClickCallback) {
        onItemClickCallback = callback
    }
}
