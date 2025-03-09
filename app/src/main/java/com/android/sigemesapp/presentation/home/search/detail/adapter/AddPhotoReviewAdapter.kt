package com.android.sigemesapp.presentation.home.search.detail.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.android.sigemesapp.R

class AddPhotoReviewAdapter(
    private var photos: List<Uri>,
    private val onDeleteClick: (Uri) -> Unit
) : RecyclerView.Adapter<AddPhotoReviewAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_room_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoUri = photos[position]
        holder.bind(photoUri)
    }

    override fun getItemCount(): Int = photos.size

    fun updatePhotos(newPhotos: List<Uri>) {
        photos = newPhotos
        notifyDataSetChanged()
    }

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photo: ImageView = itemView.findViewById(R.id.photo)
        private val closeButton: ImageView = itemView.findViewById(R.id.closeButton)

        fun bind(photoUri: Uri) {
            photo.setImageURI(photoUri)
            closeButton.visibility = View.VISIBLE
            closeButton.setOnClickListener {
                onDeleteClick(photoUri)
            }
        }
    }
}