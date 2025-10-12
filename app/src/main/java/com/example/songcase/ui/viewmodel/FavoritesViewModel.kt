package com.example.songcase.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.songcase.data.SongDataStore
import com.example.songcase.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(SongListUiState())
    val uiState: StateFlow<SongListUiState> = _uiState.asStateFlow()
    
    init {
        loadFavoriteSongs()
        
        // Подписываемся на изменения в SongDataStore для обновления избранных
        viewModelScope.launch {
            SongDataStore.songs.collect { allSongs ->
                val favoriteSongs = allSongs.filter { it.isFavorite }
                _uiState.value = _uiState.value.copy(songs = favoriteSongs)
            }
        }
    }
    
    fun loadFavoriteSongs() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val favoriteSongs = SongDataStore.getFavoriteSongs()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    songs = favoriteSongs
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Ошибка при загрузке избранных песен: ${e.message}"
                )
            }
        }
    }
    
    fun toggleFavorite(song: Song) {
        SongDataStore.toggleFavorite(song.id)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}