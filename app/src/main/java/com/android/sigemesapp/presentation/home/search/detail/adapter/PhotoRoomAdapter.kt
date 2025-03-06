package com.android.sigemesapp.presentation.home.search.detail.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.sigemesapp.R
import com.android.sigemesapp.databinding.ItemRoomPhotoBinding
import com.android.sigemesapp.utils.dialog.PhotoDialog
import com.bumptech.glide.Glide

class PhotoRoomAdapter(
    private val activity: Activity,
    private val photoUrls: List<String>
) : RecyclerView.Adapter<PhotoRoomAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(
        private val binding: ItemRoomPhotoBinding,
        private val activity: Activity
    ) : RecyclerView.ViewHolder(binding.root) {

        private val photoDialog = PhotoDialog(activity)

        fun bind(photoUrl: String) {
            Glide.with(binding.photo.context)
                .load(photoUrl)
                .error(R.drawable.mess)
                .into(binding.photo)

            binding.photo.setOnClickListener {
                photoDialog.startPhotoDialog(photoUrl)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemRoomPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding, activity)
    }

    override fun getItemCount(): Int = photoUrls.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photoUrls[position])
    }
}
