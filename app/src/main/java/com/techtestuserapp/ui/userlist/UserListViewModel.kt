package com.techtestuserapp.ui.userlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techtestuserapp.data.local.entity.UserEntity
import com.techtestuserapp.data.repository.UserRepository
import com.techtestuserapp.utils.NetworkMonitor
import com.techtestuserapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val userRepository: UserRepository,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow<UserListUiState>(UserListUiState.Loading)
    val uiState: StateFlow<UserListUiState> = _uiState.asStateFlow()

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = true
            )

    private val _events = MutableSharedFlow<String>()
    val events = _events.asSharedFlow()

    init {
        fetchUsers(forceRefresh = false)
    }

    fun onRefresh() {
        fetchUsers(forceRefresh = true)
    }

    private fun fetchUsers(forceRefresh: Boolean) {
        viewModelScope.launch {
            userRepository.getUsers(forceRefresh).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.value = UserListUiState.Loading
                    }
                    is Resource.Success -> {
                        val users = result.data ?: emptyList()
                        _uiState.value = if (users.isEmpty()) {
                            UserListUiState.Empty
                        } else {
                            UserListUiState.Success(users)
                        }
                    }
                    is Resource.Error -> {
                        result.message?.let { _events.emit(it) }
                        val cachedData = result.data
                        if (cachedData.isNullOrEmpty()) {
                            _uiState.value = UserListUiState.Error(result.message ?: "Unknown error")
                        } else {
                            _uiState.value = UserListUiState.Success(cachedData)
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
}

sealed class UserListUiState {
    data object Loading : UserListUiState()
    data class Success(val users: List<UserEntity>) : UserListUiState()
    data object Empty : UserListUiState()
    data class Error(val message: String) : UserListUiState()
}