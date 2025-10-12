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
import java.util.Date

class SongEditorViewModel(
    private val repository: SongRepository = SongRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SongEditorUiState())
    val uiState: StateFlow<SongEditorUiState> = _uiState.asStateFlow()
    
    private var currentSongId: Long? = null
    
    fun loadSong(songId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val song = repository.getSongById(songId)
                if (song != null) {
                    currentSongId = songId
                    _uiState.value = _uiState.value.copy(
                        number = song.number,
                        title = song.title,
                        author = song.author ?: "",
                        text = song.text,
                        key = song.key ?: "",
                        isLoading = false,
                        error = null
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
                    error = e.message
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
    
    fun updateAuthor(author: String) {
        _uiState.value = _uiState.value.copy(author = author)
    }
    
    fun updateText(text: String) {
        _uiState.value = _uiState.value.copy(text = text)
    }
    
    fun updateKey(key: String) {
        _uiState.value = _uiState.value.copy(key = key)
    }
    
    fun saveSong() {
        viewModelScope.launch {
            try {
                val state = _uiState.value
                
                if (state.title.isBlank()) {
                    _uiState.value = _uiState.value.copy(error = "Название песни не может быть пустым")
                    return@launch
                }
                
                if (state.text.isBlank()) {
                    _uiState.value = _uiState.value.copy(error = "Текст песни не может быть пустым")
                    return@launch
                }
                
                val song = if (currentSongId != null) {
                    // Обновляем существующую песню
                    Song(
                        id = currentSongId!!,
                        number = state.number,
                        title = state.title,
                        text = state.text,
                        author = state.author.takeIf { it.isNotBlank() },
                        key = state.key.takeIf { it.isNotBlank() },
                        updatedAt = Date()
                    )
                } else {
                    // Создаем новую песню
                    val maxNumber = repository.getMaxSongNumber() ?: 0
                    Song(
                        number = if (state.number > 0) state.number else maxNumber + 1,
                        title = state.title,
                        text = state.text,
                        author = state.author.takeIf { it.isNotBlank() },
                        key = state.key.takeIf { it.isNotBlank() }
                    )
                }
                
                val savedSongId = if (currentSongId != null) {
                    repository.updateSong(song)
                    currentSongId!!
                } else {
                    repository.insertSong(song)
                }
                
                // Парсим и сохраняем аккорды
                parseAndSaveChords(savedSongId, state.text)
                
                _uiState.value = _uiState.value.copy(error = null)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    private suspend fun parseAndSaveChords(songId: Long, text: String) {
        try {
            // Удаляем старые аккорды
            repository.deleteChordsForSong(songId)
            
            // Парсим аккорды из текста
            val lines = text.split('\n')
            val chords = mutableListOf<Chord>()
            
            lines.forEachIndexed { lineIndex, line ->
                val words = line.split(' ')
                var position = 0
                
                words.forEach { word ->
                    if (ChordUtils.isChord(word)) {
                        chords.add(
                            Chord(
                                songId = songId,
                                chord = word,
                                position = position,
                                lineNumber = lineIndex
                            )
                        )
                    }
                    position += word.length + 1 // +1 для пробела
                }
            }
            
            // Сохраняем аккорды
            if (chords.isNotEmpty()) {
                repository.insertChords(chords)
            }
            
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(error = "Ошибка при сохранении аккордов: ${e.message}")
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class SongEditorUiState(
    val number: Int = 0,
    val title: String = "",
    val author: String = "",
    val text: String = "",
    val key: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val canSave: Boolean
        get() = title.isNotBlank() && text.isNotBlank()
}
