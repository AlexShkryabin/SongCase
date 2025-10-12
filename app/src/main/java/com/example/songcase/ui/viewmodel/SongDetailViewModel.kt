package com.example.songcase.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.songcase.data.model.Chord
import com.example.songcase.data.model.Song
import com.example.songcase.data.repository.SongRepository
import com.example.songcase.utils.ChordUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SongDetailViewModel(
    private val repository: SongRepository = SongRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SongDetailUiState())
    val uiState: StateFlow<SongDetailUiState> = _uiState.asStateFlow()
    
    private var currentSong: Song? = null
    private var originalChords: List<Chord> = emptyList()
    private var currentTransposition = 0
    
    fun loadSong(songId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val song = repository.getSongById(songId)
                if (song != null) {
                    currentSong = song
                    _uiState.value = _uiState.value.copy(
                        song = song,
                        isLoading = false,
                        error = null
                    )
                    
                    // Загружаем аккорды
                    loadChords(songId)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Песня не найдена"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    private fun loadChords(songId: Long) {
        viewModelScope.launch {
            try {
                repository.getChordsForSong(songId).collect { chords ->
                    originalChords = chords
                    _uiState.value = _uiState.value.copy(
                        chords = chords,
                        showChords = chords.isNotEmpty()
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun toggleChordsVisibility() {
        _uiState.value = _uiState.value.copy(
            showChords = !_uiState.value.showChords
        )
    }
    
    fun transposeChords(semitones: Int) {
        currentTransposition = semitones
        val transposedChords = originalChords.map { chord ->
            val transposedChordName = ChordUtils.transposeChord(chord.chord, semitones)
            chord.copy(chord = transposedChordName)
        }
        _uiState.value = _uiState.value.copy(
            chords = transposedChords,
            transposition = semitones
        )
    }
    
    fun toggleFavorite() {
        val song = currentSong ?: return
        viewModelScope.launch {
            try {
                repository.updateFavoriteStatus(song.id, !song.isFavorite)
                currentSong = song.copy(isFavorite = !song.isFavorite)
                _uiState.value = _uiState.value.copy(
                    song = currentSong,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class SongDetailUiState(
    val song: Song? = null,
    val chords: List<Chord> = emptyList(),
    val showChords: Boolean = false,
    val transposition: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)
