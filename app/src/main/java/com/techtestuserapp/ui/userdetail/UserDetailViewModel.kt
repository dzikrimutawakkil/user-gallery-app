package com.techtestuserapp.ui.userdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techtestuserapp.data.repository.UserRepository
import com.techtestuserapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _postState = MutableStateFlow<PostUiState>(PostUiState.Loading)
    val postState: StateFlow<PostUiState> = _postState.asStateFlow()

    fun fetchPosts(userId: Int) {
        userRepository.getPosts(userId).onEach { result ->
            when (result) {
                is Resource.Loading -> _postState.value = PostUiState.Loading
                is Resource.Success -> {
                    val posts = result.data ?: emptyList()
                    _postState.value = if (posts.isEmpty()) {
                        PostUiState.Empty
                    } else {
                        // Proses data untuk grafik (total post)
                        val totalPosts = posts.size
                        PostUiState.Success(totalPosts)
                    }
                }
                is Resource.Error -> {
                    _postState.value = PostUiState.Error(result.message ?: "Unknown error")
                }
            }
        }.launchIn(viewModelScope)
    }
}

sealed class PostUiState {
    data object Loading : PostUiState()
    data class Success(val postCount: Int) : PostUiState()
    data object Empty : PostUiState()
    data class Error(val message: String) : PostUiState()
}