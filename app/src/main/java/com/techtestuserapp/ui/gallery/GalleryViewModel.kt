package com.techtestuserapp.ui.gallery

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techtestuserapp.data.local.entity.MediaEntity
import com.techtestuserapp.data.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _galleryState = MutableStateFlow<List<MediaEntity>>(emptyList())
    val galleryState: StateFlow<List<MediaEntity>> = _galleryState.asStateFlow()

    fun loadMedia(userId: Int) {
        mediaRepository.getMediaForUser(userId)
            .onEach { _galleryState.value = it }
            .launchIn(viewModelScope)
    }

    fun saveMedia(userId: Int, uri: Uri, type: String) {
        viewModelScope.launch {
            mediaRepository.saveMedia(userId, uri, type)
        }
    }
}