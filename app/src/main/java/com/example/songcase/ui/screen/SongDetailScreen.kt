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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.songcase.data.AppSettings
// Hilt removed for simplicity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.songcase.data.model.Chord
import com.example.songcase.ui.viewmodel.SongDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailScreen(
    songId: Long,
    onBackClick: () -> Unit,
    onEditClick: (Long) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onDeleteClick: (Long) -> Unit = {},
    viewModel: SongDetailViewModel = remember { SongDetailViewModel() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    
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
                    IconButton(onClick = { onEditClick(songId) }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Редактировать песню"
                        )
                    }
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            if (uiState.song?.isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Добавить в избранное"
                        )
                    }
                    var showMenu by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Меню"
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Настройки") },
                                onClick = {
                                    onSettingsClick()
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Удалить песню") },
                                onClick = {
                                    showDeleteDialog = true
                                    showMenu = false
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    ErrorMessage(
                        message = uiState.error ?: "Неизвестная ошибка",
                        onRetry = { viewModel.loadSong(songId) },
                        onDismiss = { viewModel.clearError() }
                    )
                }
            }
            
            uiState.song != null -> {
                    SongContent(
                        song = uiState.song!!,
                        chords = uiState.chords,
                        showChords = uiState.showChords,
                        transposition = uiState.transposition,
                        onTranspose = { semitones -> viewModel.transposeChords(semitones) },
                        viewModel = viewModel,
                        modifier = Modifier.padding(paddingValues)
                    )
            }
        }
        
        // Диалог подтверждения удаления
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Удалить песню") },
                text = { Text("Вы уверены, что хотите удалить эту песню? Это действие нельзя отменить.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDeleteClick(songId)
                            showDeleteDialog = false
                        }
                    ) {
                        Text("Удалить")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteDialog = false }
                    ) {
                        Text("Отмена")
                    }
                }
            )
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
    viewModel: SongDetailViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Управление аккордами и транспонированием
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Переключение видимости аккордов
                Switch(
                    checked = showChords,
                    onCheckedChange = { viewModel.toggleChordsVisibility() }
                )
                
                // Управление транспонированием
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onTranspose(-1) }
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = "Понизить на полтона"
                        )
                    }
                    
                    Text(
                        text = if (transposition == 0) "0" else if (transposition > 0) "+$transposition" else "$transposition",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(
                        onClick = { onTranspose(1) }
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowUp,
                            contentDescription = "Повысить на полтона"
                        )
                    }
                    
                    IconButton(
                        onClick = { viewModel.resetTransposition() }
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Сбросить транспонирование"
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Заголовок песни
        Text(
            text = "${song.number}. ${song.title}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        // Текст песни
        val fontSize by AppSettings.fontSize.collectAsStateWithLifecycle()
        if (showChords) {
            Text(
                text = song.text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = fontSize.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            // Показываем текст без аккордов - убираем только строки с аккордами
            val textWithoutChords = song.text.lines().map { line ->
                // Проверяем, является ли строка строкой с аккордами
                // Строка с аккордами содержит только аккорды, пробелы и специальные символы
                val chordPattern = Regex("""^[\s\w#b/]+$""")
                if (chordPattern.matches(line) && line.isNotBlank() && 
                    line.any { it.isLetter() } && // Содержит буквы (аккорды)
                    !line.any { it.isDigit() } && // Не содержит цифр (не текст песни)
                    !line.contains(" ") || line.trim().matches(Regex("""^[A-H][#b]?[majmin]?[0-9]?(\s+[A-H][#b]?[majmin]?[0-9]?)*$"""))) { // Проверяем на аккорды
                    "" // Убираем строку с аккордами
                } else {
                    line // Оставляем текст песни
                }
            }.filter { it.isNotBlank() }.joinToString("\n")
            
            Text(
                text = textWithoutChords,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = fontSize.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
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
