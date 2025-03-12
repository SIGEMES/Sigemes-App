package com.android.sigemesapp.presentation.home.search.detail.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.sigemesapp.databinding.ItemRoomPhotoBinding

class AddPhotoReviewAdapter(
    private var photos: List<Uri>,
    private val onDeleteClick: (Uri) -> Unit
) : RecyclerView.Adapter<AddPhotoReviewAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemRoomPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount(): Int = photos.size

    fun updatePhotos(newPhotos: List<Uri>) {
        photos = newPhotos
        notifyDataSetChanged()
    }

    inner class PhotoViewHolder(private val binding: ItemRoomPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photoUri: Uri) {
            binding.photo.setImageURI(photoUri)
            binding.closeButton.visibility = android.view.View.VISIBLE
            binding.closeButton.setOnClickListener { onDeleteClick(photoUri) }
        }
    }
}
