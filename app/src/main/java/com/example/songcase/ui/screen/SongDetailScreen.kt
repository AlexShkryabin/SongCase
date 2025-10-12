package com.example.songcase.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
// Hilt removed for simplicity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.songcase.data.model.Chord
import com.example.songcase.ui.viewmodel.SongDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailScreen(
    songId: Long,
    onBackClick: () -> Unit,
    viewModel: SongDetailViewModel = remember { SongDetailViewModel() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(songId) {
        viewModel.loadSong(songId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Песня") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleChordsVisibility() }) {
                        Icon(
                            if (uiState.showChords) Icons.Default.PlayArrow else Icons.Default.Close,
                            contentDescription = if (uiState.showChords) "Скрыть аккорды" else "Показать аккорды"
                        )
                    }
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            if (uiState.song?.isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Добавить в избранное"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.error != null -> {
                ErrorMessage(
                    message = uiState.error ?: "Неизвестная ошибка",
                    onRetry = { viewModel.loadSong(songId) },
                    onDismiss = { viewModel.clearError() }
                )
            }
            
            uiState.song != null -> {
                SongContent(
                    song = uiState.song!!,
                    chords = uiState.chords,
                    showChords = uiState.showChords,
                    transposition = uiState.transposition,
                    onTranspose = { semitones -> viewModel.transposeChords(semitones) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun SongContent(
    song: com.example.songcase.data.model.Song,
    chords: List<Chord>,
    showChords: Boolean,
    transposition: Int,
    onTranspose: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Заголовок песни
        Text(
            text = "${song.number}. ${song.title}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        if (song.author != null) {
            Text(
                text = song.author,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Настройки аккордов
        if (chords.isNotEmpty() && showChords) {
            ChordControls(
                transposition = transposition,
                onTranspose = onTranspose
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Текст песни с аккордами
        SongTextWithChords(
            text = song.text,
            chords = if (showChords) chords else emptyList()
        )
    }
}

@Composable
private fun ChordControls(
    transposition: Int,
    onTranspose: (Int) -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Транспонирование аккордов",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = { onTranspose(-1) }) {
                    Text("-1")
                }
                TextButton(onClick = { onTranspose(-2) }) {
                    Text("-2")
                }
                TextButton(onClick = { onTranspose(0) }) {
                    Text("0")
                }
                TextButton(onClick = { onTranspose(1) }) {
                    Text("+1")
                }
                TextButton(onClick = { onTranspose(2) }) {
                    Text("+2")
                }
            }
            
            if (transposition != 0) {
                Text(
                    text = "Транспонирование: ${if (transposition > 0) "+" else ""}$transposition",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun SongTextWithChords(
    text: String,
    chords: List<Chord>
) {
    val lines = text.split('\n')
    
    lines.forEachIndexed { lineIndex, line ->
        val lineChords = chords.filter { it.lineNumber == lineIndex }
        
        if (lineChords.isNotEmpty()) {
            // Показываем аккорды над строкой
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                var currentPosition = 0
                lineChords.sortedBy { it.position }.forEach { chord ->
                    // Добавляем пробелы до позиции аккорда
                    val spaces = chord.position - currentPosition
                    if (spaces > 0) {
                        Spacer(modifier = Modifier.width((spaces * 8).dp))
                    }
                    
                    Text(
                        text = chord.chord,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    currentPosition = chord.position + chord.chord.length
                }
            }
        }
        
        // Показываем текст строки
        Text(
            text = line,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ошибка: ${message ?: "Неизвестная ошибка"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                TextButton(onClick = onRetry) {
                    Text("Повторить")
                }
                TextButton(onClick = onDismiss) {
                    Text("Закрыть")
                }
            }
        }
    }
}
