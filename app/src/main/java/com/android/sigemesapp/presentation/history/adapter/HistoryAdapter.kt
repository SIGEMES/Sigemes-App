package com.android.sigemesapp.presentation.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.sigemesapp.R
import com.android.sigemesapp.data.source.remote.response.RentsDataItem
import com.android.sigemesapp.databinding.ItemHistoryBinding
import com.android.sigemesapp.utils.convertToDateRange
import com.android.sigemesapp.utils.isoToTimestamp
import com.android.sigemesapp.utils.setRentStatusColorAndText
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale

class HistoryAdapter(private val listHistory: MutableList<RentsDataItem>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null

    class HistoryViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(history: RentsDataItem, callback: OnItemClickCallback?) {
            binding.noPesanan.text = history.payment.id
            binding.totalPrice.text = String.format("Rp %s",
                NumberFormat.getNumberInstance(Locale("id", "ID")).format(history.payment.amount + 5550))

            var status = ""
            val createdAtTimestamp = isoToTimestamp(history.createdAt)
            val paymentTriggeredAtTimestamp = history.payment.paymentTriggeredAt?.let { isoToTimestamp(it) }

            status = if (history.rentStatus == "pending" &&
                ((System.currentTimeMillis() > createdAtTimestamp + 24 * 60 * 60 * 1000) ||
                        (paymentTriggeredAtTimestamp != null && System.currentTimeMillis() > paymentTriggeredAtTimestamp + 24 * 60 * 60 * 1000))){
                "expired"
            } else {
                history.rentStatus
            }

            setRentStatusColorAndText(binding, status)

            if(history.cityHallPricing == null){
                Glide.with(binding.photo.context)
                    .load(history.guesthouseRoomPricing?.guesthouseRoom?.guesthouse?.guesthouseMedia?.first()?.url)
                    .error(R.drawable.mess)
                    .into(binding.photo)
                binding.titlePesanan.text = history.guesthouseRoomPricing?.guesthouseRoom?.guesthouse?.name
                binding.roomTypeTitle.text = String.format("Kamar ${history.guesthouseRoomPricing?.guesthouseRoom?.name}")

                binding.checkInCheckOut.text = convertToDateRange(history.startDate, history.endDate)
            } else if (history.guesthouseRoomPricing == null){
                Glide.with(binding.photo.context)
                    .load(history.cityHallPricing.cityHall.media.first().url)
                    .error(R.drawable.gedung)
                    .into(binding.photo)
                binding.titlePesanan.text = history.cityHallPricing.cityHall.name
                binding.roomTypeTitle.text = String.format("Acara ${history.cityHallPricing.activityType}")
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

    fun updateData(newList: List<RentsDataItem>) {
        listHistory.clear()
        listHistory.addAll(newList)
        notifyDataSetChanged()
    }

    fun updateItem(position: Int, updatedItem: RentsDataItem) {
        listHistory[position] = updatedItem
        notifyItemChanged(position)
    }
}