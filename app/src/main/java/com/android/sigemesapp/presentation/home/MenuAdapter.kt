package com.android.sigemesapp.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.sigemesapp.R
import com.android.sigemesapp.databinding.ItemPhotoMenuBinding
import com.bumptech.glide.Glide

class MenuAdapter(private val photoUrls: List<String>) : RecyclerView.Adapter<MenuAdapter.PhotoMenuViewHolder>() {

    class PhotoMenuViewHolder(private val binding: ItemPhotoMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(photoUrl: String) {
            Glide.with(binding.photoMenu.context)
                .load(photoUrl)
                .error(R.drawable.mess)
                .into(binding.photoMenu)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoMenuViewHolder {
        val binding = ItemPhotoMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoMenuViewHolder(binding)
    }

    override fun getItemCount(): Int = photoUrls.size

    override fun onBindViewHolder(holder: PhotoMenuViewHolder, position: Int) {
        holder.bind(photoUrls[position])
    }
}
