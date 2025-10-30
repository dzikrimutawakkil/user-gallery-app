package com.techtestuserapp.ui.mediapreview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.techtestuserapp.data.local.entity.MediaEntity
import com.techtestuserapp.databinding.ItemMediaPreviewBinding
import java.io.File

class MediaPreviewAdapter(
    private val context: Context
) : ListAdapter<MediaEntity, MediaPreviewAdapter.PreviewViewHolder>(MediaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviewViewHolder {
        val binding = ItemMediaPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PreviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PreviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PreviewViewHolder(private val binding: ItemMediaPreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MediaEntity) {
            binding.ivPreviewImage.visibility = View.GONE
            binding.vvPreviewVideo.visibility = View.GONE
            binding.vvPreviewVideo.stopPlayback()

            val file = File(item.uri)
            val fileUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            if (item.type == "IMAGE") {
                binding.ivPreviewImage.visibility = View.VISIBLE
                binding.ivPreviewImage.load(fileUri) {
                    crossfade(true)
                }
            } else if (item.type == "VIDEO") {
                binding.vvPreviewVideo.visibility = View.VISIBLE
                binding.vvPreviewVideo.setVideoURI(fileUri)

                val mediaController = MediaController(context)
                mediaController.setAnchorView(binding.vvPreviewVideo)
                binding.vvPreviewVideo.setMediaController(mediaController)

                binding.vvPreviewVideo.setOnPreparedListener { mp ->
                    mp.isLooping = true
                }
            }
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