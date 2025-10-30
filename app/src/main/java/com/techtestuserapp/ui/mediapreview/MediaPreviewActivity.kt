package com.techtestuserapp.ui.mediapreview

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.techtestuserapp.databinding.ActivityMediaPreviewBinding
import com.techtestuserapp.ui.gallery.GalleryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MediaPreviewActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
        const val EXTRA_USER_NAME = "extra_user_name"
        const val EXTRA_CLICKED_POSITION = "extra_clicked_position"
    }

    private lateinit var binding: ActivityMediaPreviewBinding
    private val viewModel: GalleryViewModel by viewModels() // Re-using existing ViewModel
    private lateinit var mediaAdapter: MediaPreviewAdapter

    private var userId: Int = -1
    private var clickedPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getIntExtra(EXTRA_USER_ID, -1)
        clickedPosition = intent.getIntExtra(EXTRA_CLICKED_POSITION, 0)
        val userName = intent.getStringExtra(EXTRA_USER_NAME) ?: "User"

        if (userId == -1) {
            Toast.makeText(this, "User ID tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar(userName)
        setupViewPager()
        observeViewModel()

        viewModel.loadMedia(userId)
    }

    private fun setupToolbar(userName: String) {
        binding.toolbarPreview.title = "Preview - $userName"
        binding.toolbarPreview.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupViewPager() {
        mediaAdapter = MediaPreviewAdapter(this)
        binding.viewPagerPreview.adapter = mediaAdapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.galleryState.collect { mediaList ->
                    if (mediaList.isNotEmpty()) {
                        mediaAdapter.submitList(mediaList) {
                            binding.viewPagerPreview.setCurrentItem(clickedPosition, false)
                        }
                    }
                }
            }
        }
    }
}