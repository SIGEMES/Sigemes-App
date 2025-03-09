package com.android.sigemesapp.presentation.home.search.detail.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.sigemesapp.R
import com.android.sigemesapp.data.source.remote.response.CityHallReviews
import com.android.sigemesapp.databinding.ItemReviewBinding
import com.android.sigemesapp.utils.calculateTimeDifference
import com.bumptech.glide.Glide

class ReviewCityHallAdapter(private val listReviews: List<CityHallReviews>) : RecyclerView.Adapter<ReviewCityHallAdapter.ReviewCityHallViewHolder>() {
    private var onItemClickCallback: OnItemClickCallback? = null

    class ReviewCityHallViewHolder(private val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: CityHallReviews, callback: OnItemClickCallback?) {
            binding.desc.text = review.comment
            binding.username.text = review.rent.renter.fullname
            Glide.with(binding.profilePic.context)
                .load(review.rent.renter.profilePicture)
                .error(R.drawable.person_24)
                .into(binding.profilePic)

            binding.rvPhotoReview.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
            val photoUrls = review.reviewMedia.map { it.url }
            if(photoUrls.isNotEmpty()){
                binding.rvPhotoReview.visibility = View.VISIBLE
                val photoAdapter = PhotoRoomAdapter(binding.root.context as Activity, photoUrls)
                binding.rvPhotoReview.adapter = photoAdapter
            } else {
                binding.rvPhotoReview.visibility = View.GONE
            }


            val createdAt = review.createdAt
            val hoursDifference = calculateTimeDifference(createdAt)
            binding.hour.text = hoursDifference

            binding.rating.text = String.format("${review.rating}/5")

            if(review.reviewReply == null){
                binding.cardAdminReply.visibility = View.GONE
            }else{
                binding.cardAdminReply.visibility = View.VISIBLE
                binding.hour2.text = calculateTimeDifference(review.reviewReply.createdAt)
                binding.adminReply.text = review.reviewReply.comment
            }

            binding.root.setOnClickListener {
                callback?.onItemClicked(review)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ReviewCityHallViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ReviewCityHallViewHolder(binding)
    }

    override fun getItemCount(): Int = listReviews.size

    override fun onBindViewHolder(holder: ReviewCityHallViewHolder, position: Int) {
        val event = listReviews[position]
        holder.bind(event, onItemClickCallback)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: CityHallReviews)
    }

    fun setOnItemClickCallback(callback: OnItemClickCallback) {
        onItemClickCallback = callback
    }
}