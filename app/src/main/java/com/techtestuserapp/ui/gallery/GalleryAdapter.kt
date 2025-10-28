package com.techtestuserapp.ui.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.techtestuserapp.data.local.entity.MediaEntity
import com.techtestuserapp.databinding.ItemMediaBinding
import java.io.File

class GalleryAdapter(
    private val onClick: (MediaEntity) -> Unit
) : ListAdapter<MediaEntity, GalleryAdapter.MediaViewHolder>(MediaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = ItemMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class MediaViewHolder(private val binding: ItemMediaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MediaEntity) {
            binding.ivPlayIcon.visibility = if (item.type == "VIDEO") View.VISIBLE else View.GONE

            binding.ivMedia.load(File(item.uri)) {
                crossfade(true)
            }

            binding.root.setOnClickListener { onClick(item) }
        }
    }

    class MediaDiffCallback : DiffUtil.ItemCallback<MediaEntity>() {
        override fun areItemsTheSame(oldItem: MediaEntity, newItem: MediaEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MediaEntity, newItem: MediaEntity): Boolean {
            return oldItem == newItem
        }
    }
}