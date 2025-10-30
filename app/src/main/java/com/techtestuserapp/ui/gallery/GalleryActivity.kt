package com.techtestuserapp.ui.gallery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.techtestuserapp.databinding.ActivityGalleryBinding
import com.techtestuserapp.ui.mediapreview.MediaPreviewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class GalleryActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
        const val EXTRA_USER_NAME = "extra_user_name"
    }

    private lateinit var binding: ActivityGalleryBinding
    private val viewModel: GalleryViewModel by viewModels()
    private lateinit var galleryAdapter: GalleryAdapter

    private var userId: Int = -1
    private var tempImageUri: Uri? = null
    private var userName: String = "User"

    // Camera Permission
    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                launchCamera()
            } else {
                Toast.makeText(this, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
            }
        }

    // Gallery Permission
    private val requestStoragePermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                launchGalleryPicker()
            } else {
                Toast.makeText(this, "Izin galeri ditolak", Toast.LENGTH_SHORT).show()
            }
        }

    // Camera Picture Result
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                val localUri = tempImageUri
                localUri?.let {
                    viewModel.saveMedia(userId, it, "IMAGE")
                }
            }
        }

    // Gallery Result
    private val pickMediaLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val type = if (it.toString().contains("video")) "VIDEO" else "IMAGE"
                viewModel.saveMedia(userId, it, type) //
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getIntExtra(EXTRA_USER_ID, -1)
        userName = intent.getStringExtra(EXTRA_USER_NAME) ?: "User"

        if (userId == -1) {
            Toast.makeText(this, "User ID tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar(userName)
        setupRecyclerView()
        setupListeners()
        observeViewModel()

        viewModel.loadMedia(userId)
    }

    private fun setupToolbar(userName: String) {
        binding.toolbarGallery.title = "Gallery - $userName"
        binding.toolbarGallery.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        galleryAdapter = GalleryAdapter { _, position ->
            val intent = Intent(this, MediaPreviewActivity::class.java).apply {
                putExtra(MediaPreviewActivity.EXTRA_USER_ID, userId)
                putExtra(MediaPreviewActivity.EXTRA_USER_NAME, userName)
                putExtra(MediaPreviewActivity.EXTRA_CLICKED_POSITION, position)
            }
            startActivity(intent)
        }
        binding.recyclerViewGallery.adapter = galleryAdapter
    }

    private fun setupListeners() {
        binding.fabAddMedia.setOnClickListener {
            showMediaSourceDialog()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.galleryState.collect { mediaList ->
                    galleryAdapter.submitList(mediaList)
                    binding.tvGalleryEmpty.visibility = if (mediaList.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun showMediaSourceDialog() {
        val options = arrayOf("Ambil Foto (Kamera)", "Pilih dari Gallery")
        AlertDialog.Builder(this)
            .setTitle("Tambah Media")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> checkCameraPermission()
                    1 -> checkStoragePermission()
                }
                dialog.dismiss()
            }
            .show()
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            else -> {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun launchCamera() {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val cacheFile = File(cacheDir, "JPEG_${timeStamp}_.jpg")

        val newUri = FileProvider.getUriForFile(
            this,
            "$packageName.provider",
            cacheFile
        )

        tempImageUri = newUri
        takePictureLauncher.launch(newUri)
    }

    private fun checkStoragePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchGalleryPicker()
            }
            else -> {
                requestStoragePermission.launch(permission)
            }
        }
    }

    private fun launchGalleryPicker() {
        pickMediaLauncher.launch("*/*")
    }
}