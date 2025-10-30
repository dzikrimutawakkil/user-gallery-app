package com.techtestuserapp.ui.userdetail

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.techtestuserapp.data.local.entity.UserEntity
import com.techtestuserapp.databinding.ActivityUserDetailBinding
import com.techtestuserapp.ui.gallery.GalleryActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_USER = "extra_user"
    }

    private lateinit var binding: ActivityUserDetailBinding
    private val viewModel: UserDetailViewModel by viewModels()
    private var user: UserEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_USER, UserEntity::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_USER)
        }

        if (user == null) {
            Toast.makeText(this, "Gagal memuat data user", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        populateUserData()
        setupListeners()
        observeViewModel()

        user?.id?.let { viewModel.fetchPosts(it) }
    }

    private fun setupToolbar() {
        binding.toolbarDetail.title = user?.name ?: "User Detail"
        binding.toolbarDetail.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun populateUserData() {
        user?.let {
            binding.tvDetailName.text = it.name
            binding.tvDetailEmail.text = it.email
            binding.tvDetailPhone.text = it.phone
            binding.tvDetailAddress.text = it.address
            binding.tvDetailCompany.text = it.company
        }
    }

    private fun setupListeners() {
        binding.btnViewGallery.setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java).apply {
                putExtra(GalleryActivity.EXTRA_USER_ID, user?.id ?: -1)
                putExtra(GalleryActivity.EXTRA_USER_NAME, user?.name ?: "User")
            }
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.postState.collect { state ->
                    handlePostState(state)
                }
            }
        }
    }

    private fun handlePostState(state: PostUiState) {
        binding.progressBarChart.visibility = if (state is PostUiState.Loading) View.VISIBLE else View.GONE
        binding.barChart.visibility = if (state is PostUiState.Success) View.VISIBLE else View.GONE

        when (state) {
            is PostUiState.Success -> {
                setupChart(state.postCount)
            }
            is PostUiState.Error -> {
                Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                binding.barChart.visibility = View.GONE
            }
            is PostUiState.Empty -> {
                Toast.makeText(this, "User ini belum memiliki post", Toast.LENGTH_SHORT).show()
                binding.barChart.visibility = View.GONE
            }
            is PostUiState.Loading -> {
            }
        }
    }

    private fun setupChart(postCount: Int) {
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(1f, postCount.toFloat()))

        val dataSet = BarDataSet(entries, "Jumlah Post")
        dataSet.color = Color.GRAY
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 16f

        val barData = BarData(dataSet)
        barData.barWidth = 0.5f

        binding.barChart.apply {
            data = barData
            description.isEnabled = false
            legend.isEnabled = false
            xAxis.isEnabled = false
            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false
            setFitBars(true)
            invalidate() // refresh chart
        }
    }
}