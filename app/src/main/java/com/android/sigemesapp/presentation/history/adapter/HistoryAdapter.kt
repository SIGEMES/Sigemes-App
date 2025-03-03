package com.android.sigemesapp.presentation.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.sigemesapp.data.source.remote.response.RentsDataItem
import com.android.sigemesapp.databinding.ItemHistoryBinding
import com.android.sigemesapp.utils.convertTimestampToFormattedDate
import com.android.sigemesapp.utils.convertToDateRange
import com.android.sigemesapp.utils.setRentStatusColorAndText

class HistoryAdapter(private val listHistory: List<RentsDataItem>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null

    class HistoryViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(history: RentsDataItem, callback: OnItemClickCallback?) {

            if(history.cityHallPricing == null){
                val dateTime = convertTimestampToFormattedDate(history.createdAt)
                binding.date.text = dateTime
                binding.titlePesanan.text = history.guesthouseRoomPricing?.guesthouseRoom?.guesthouse?.name
                binding.roomTypeTitle.text = String.format("Kamar ${history.guesthouseRoomPricing?.guesthouseRoom?.name}")
                setRentStatusColorAndText(binding, history.rentStatus)
                binding.checkInCheckOut.text = convertToDateRange(history.startDate, history.endDate)

            } else if (history.guesthouseRoomPricing == null){
                val dateTime = convertTimestampToFormattedDate(history.createdAt)
                binding.date.text = dateTime
                binding.titlePesanan.text = history.cityHallPricing.cityHall.name
                binding.roomTypeTitle.text = String.format("Acara ${history.cityHallPricing.activityType}")
                setRentStatusColorAndText(binding, history.rentStatus)
                binding.checkInCheckOut.text = convertToDateRange(history.startDate, history.endDate)
            }
            binding.root.setOnClickListener {
                callback?.onItemClicked(history)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return HistoryViewHolder(binding)
    }

    override fun getItemCount(): Int = listHistory.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = listHistory[position]
        holder.bind(history, onItemClickCallback)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: RentsDataItem)
    }

    fun setOnItemClickCallback(callback: OnItemClickCallback) {
        onItemClickCallback = callback
    }
}
