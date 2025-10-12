package com.example.songcase.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.songcase.data.SongDataStore
import com.example.songcase.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class SongEditorViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(SongEditorUiState())
    val uiState: StateFlow<SongEditorUiState> = _uiState.asStateFlow()
    
    private var currentSongId: Long? = null
    
    fun loadSong(songId: Long) {
        currentSongId = songId
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val song = SongDataStore.getSongById(songId)
                if (song != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        number = song.number,
                        title = song.title,
                        text = song.text
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Песня не найдена"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Ошибка при загрузке песни: ${e.message}"
                )
            }
        }
    }
    
    fun updateNumber(number: Int) {
        _uiState.value = _uiState.value.copy(number = number)
    }
    
    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }
    
    fun updateText(text: String) {
        _uiState.value = _uiState.value.copy(text = text)
    }
    
    fun saveSong() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val state = _uiState.value
                val song = Song(
                    id = currentSongId ?: 0L,
                    number = state.number,
                    title = state.title,
                    text = state.text,
                    isFavorite = false,
                    createdAt = Date(),
                    updatedAt = Date()
                )
                
                if (currentSongId != null) {
                    // Обновляем существующую песню
                    SongDataStore.updateSong(song)
                } else {
                    // Добавляем новую песню
                    SongDataStore.addSong(song)
                }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Ошибка при сохранении песни: ${e.message}"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    private fun validateForm(): Boolean {
        val state = _uiState.value
        return state.title.isNotBlank() && 
               state.text.isNotBlank() && 
               state.number > 0
    }
}

data class SongEditorUiState(
    val number: Int = 1,
    val title: String = "",
    val text: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
) {
    val canSave: Boolean
        get() = title.isNotBlank() && text.isNotBlank() && number > 0 && !isLoading
}