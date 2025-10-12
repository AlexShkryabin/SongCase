package com.example.songcase.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.songcase.data.SongDataStore
import com.example.songcase.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SongListViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(SongListUiState())
    val uiState: StateFlow<SongListUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    init {
        loadSongs()
        
        // Объединяем поток песен с поисковым запросом для фильтрации
        viewModelScope.launch {
            combine(SongDataStore.songs, _searchQuery) { songs, query ->
                if (query.isBlank()) {
                    songs
                } else {
                    SongDataStore.searchSongs(query)
                }
            }.collect { filteredSongs ->
                _uiState.value = _uiState.value.copy(songs = filteredSongs)
            }
        }
    }
    
    fun loadSongs() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                // Данные уже загружены в SongDataStore, просто обновляем состояние
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    songs = SongDataStore.songs.value
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Ошибка при загрузке песен: ${e.message}"
                )
            }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun toggleFavorite(song: Song) {
        SongDataStore.toggleFavorite(song.id)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class SongListUiState(
    val songs: List<Song> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)