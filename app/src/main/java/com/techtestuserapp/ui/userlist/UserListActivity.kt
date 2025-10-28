package com.techtestuserapp.ui.userlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.techtestuserapp.data.local.entity.UserEntity
import com.techtestuserapp.databinding.ActivityUserListBinding
import com.techtestuserapp.ui.userdetail.UserDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserListBinding
    private val viewModel: UserListViewModel by viewModels()
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter { user ->
            navigateToDetail(user)
        }
        binding.recyclerViewUsers.adapter = userAdapter
    }

    private fun setupListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.onRefresh()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    handleUiState(state)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.isOnline.collect { isOnline ->
                    binding.tvNetworkStatus.visibility = if (isOnline) View.GONE else View.VISIBLE
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.events.collect { message ->
                    Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleUiState(state: UserListUiState) {
        binding.shimmerLayout.visibility = if (state is UserListUiState.Loading) View.VISIBLE else View.GONE
        if (state is UserListUiState.Loading) binding.shimmerLayout.startShimmer() else binding.shimmerLayout.stopShimmer()

        binding.recyclerViewUsers.visibility = if (state is UserListUiState.Success) View.VISIBLE else View.GONE
        binding.tvEmpty.visibility = if (state is UserListUiState.Empty) View.VISIBLE else View.GONE

        binding.swipeRefreshLayout.isRefreshing = false

        if (state is UserListUiState.Success) {
            userAdapter.submitList(state.users)
        }
    }

    private fun navigateToDetail(user: UserEntity) {
        val intent = Intent(this, UserDetailActivity::class.java).apply {
            putExtra(UserDetailActivity.EXTRA_USER, user)
        }
        startActivity(intent)
    }
}