package com.android.sigemesapp.presentation.home.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.sigemesapp.R
import com.android.sigemesapp.databinding.ItemRoomPhotoBinding
import com.bumptech.glide.Glide

class PhotoRoomAdapter(private val photoUrls: List<String>) : RecyclerView.Adapter<PhotoRoomAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(private val binding: ItemRoomPhotoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(photoUrl: String) {
            Glide.with(binding.photo.context)
                .load(photoUrl)
                .error(R.drawable.mess)
                .into(binding.photo)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemRoomPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun getItemCount(): Int = photoUrls.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photoUrls[position])
    }
}