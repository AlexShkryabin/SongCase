package com.example.songcase.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.songcase.data.model.Song
import com.example.songcase.data.repository.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class ImportSongViewModel(
    private val repository: SongRepository = SongRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ImportSongUiState())
    val uiState: StateFlow<ImportSongUiState> = _uiState.asStateFlow()
    
    fun updateUrl(url: String) {
        _uiState.value = _uiState.value.copy(url = url)
    }
    
    fun importSong() {
        val url = _uiState.value.url
        if (url.isBlank()) return
        
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null,
                    isSuccess = false
                )
                
                // Простая заглушка для демонстрации
                val song = Song(
                    number = 1,
                    title = "Импортированная песня",
                    author = "Неизвестный автор",
                    key = "C",
                    text = "Текст песни будет здесь",
                    isFavorite = false,
                    createdAt = Date()
                )
                
                repository.insertSong(song)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Ошибка при загрузке песни: ${e.message}"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun resetSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }
}

data class ImportSongUiState(
    val url: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)