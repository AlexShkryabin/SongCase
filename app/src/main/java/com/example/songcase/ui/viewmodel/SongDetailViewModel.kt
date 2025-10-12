package com.example.songcase.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.songcase.data.SongDataStore
import com.example.songcase.data.model.Chord
import com.example.songcase.data.model.Song
import com.example.songcase.utils.ChordUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SongDetailViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(SongDetailUiState())
    val uiState: StateFlow<SongDetailUiState> = _uiState.asStateFlow()
    
    private var currentSong: Song? = null
    
    fun loadSong(songId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val song = SongDataStore.getSongById(songId)
                if (song != null) {
                    currentSong = song
                    val chords = ChordUtils.findChordsInText(song.text)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        song = song,
                        chords = chords
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
    
    fun toggleChordsVisibility() {
        _uiState.value = _uiState.value.copy(
            showChords = !_uiState.value.showChords
        )
    }
    
    fun transposeChords(semitones: Int) {
        val newTransposition = _uiState.value.transposition + semitones
        
        // Всегда получаем оригинальную песню из хранилища
        val originalSong = SongDataStore.getSongById(currentSong?.id ?: 0)
        if (originalSong != null) {
            val transposedText = ChordUtils.transposeText(originalSong.text, newTransposition)
            val transposedSong = originalSong.copy(text = transposedText)
            val transposedChords = ChordUtils.findChordsInText(transposedText)
            
            _uiState.value = _uiState.value.copy(
                transposition = newTransposition,
                song = transposedSong,
                chords = transposedChords
            )
        }
    }
    
    fun resetTransposition() {
        val originalSong = SongDataStore.getSongById(currentSong?.id ?: 0)
        if (originalSong != null) {
            val originalChords = ChordUtils.findChordsInText(originalSong.text)
            _uiState.value = _uiState.value.copy(
                transposition = 0,
                song = originalSong,
                chords = originalChords
            )
        }
    }
    
    fun toggleFavorite() {
        currentSong?.let { song ->
            SongDataStore.toggleFavorite(song.id)
            // Обновляем локальное состояние
            val updatedSong = song.copy(isFavorite = !song.isFavorite)
            currentSong = updatedSong
            _uiState.value = _uiState.value.copy(song = updatedSong)
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class SongDetailUiState(
    val song: Song? = null,
    val chords: List<Chord> = emptyList(),
    val showChords: Boolean = true,
    val transposition: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)