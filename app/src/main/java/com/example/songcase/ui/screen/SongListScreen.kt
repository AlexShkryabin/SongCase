package com.example.songcase.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
// Hilt removed for simplicity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.songcase.data.SongDataStore
import com.example.songcase.data.model.Song
import com.example.songcase.ui.viewmodel.SongListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongListScreen(
    onSongClick: (Long) -> Unit,
    onAddSongClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onJsonImportClick: () -> Unit,
    onDeleteSong: (Long) -> Unit = {},
    viewModel: SongListViewModel = remember { SongListViewModel() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var songToDelete by remember { mutableStateOf<Long?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Выпадающее меню переключения между песенниками
                    var expanded by remember { mutableStateOf(false) }
                    val current by SongDataStore.currentSongbook.collectAsStateWithLifecycle()
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val currentTitle = when (current) {
                            SongDataStore.SongbookType.BUILT_IN -> "Песнь возрождения"
                            SongDataStore.SongbookType.CUSTOM -> "Мои псалмы"
                        }
                        Text(
                            text = currentTitle,
                            modifier = Modifier
                                .clickable { expanded = true }
                        )
                        // Маленький треугольник, сигнализирующий выпадающее меню
                        Text(" \u25BE", modifier = Modifier.clickable { expanded = true })
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(
                                text = { Text("Песнь возрождения") },
                                onClick = {
                                    SongDataStore.switchSongbook(SongDataStore.SongbookType.BUILT_IN)
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Мои псалмы") },
                                onClick = {
                                    SongDataStore.switchSongbook(SongDataStore.SongbookType.CUSTOM)
                                    expanded = false
                                }
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showSearch = !showSearch }) {
                        Icon(Icons.Default.Search, contentDescription = "Поиск")
                    }
                    IconButton(onClick = onAddSongClick) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить песню")
                    }
                    IconButton(onClick = onFavoritesClick) {
                        // Иконка избранного не меняется, но мы меняем иконку в ячейке на звезду
                        Icon(Icons.Default.Star, contentDescription = "Избранное", tint =  androidx.compose.ui.graphics.Color(0xFFFFC107))

                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (showSearch) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { query ->
                        searchQuery = query
                        viewModel.updateSearchQuery(query)
                    },
                    modifier = Modifier.padding(16.dp)
                )
            }
            
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
                        onRetry = { viewModel.loadSongs() },
                        onDismiss = { viewModel.clearError() }
                    )
                }
                
                else -> {
                    SongList(
                        songs = uiState.songs,
                        onSongClick = onSongClick,
                        onToggleFavorite = { song -> 
                            viewModel.toggleFavorite(song)
                        },
                        onDeleteSong = { songId ->
                    songToDelete = songId
                    showDeleteDialog = true
                }
                    )
                }
            }
        }
    }
    
    // Диалог подтверждения удаления
    if (showDeleteDialog && songToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteDialog = false
                songToDelete = null
            },
            title = { Text("Удалить песню") },
            text = { Text("Вы уверены, что хотите удалить эту песню?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        songToDelete?.let { onDeleteSong(it) }
                        showDeleteDialog = false
                        songToDelete = null
                    }
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        songToDelete = null
                    }
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Поиск по номеру, названию или тексту") },
        modifier = modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
private fun SongList(
    songs: List<Song>,
    onSongClick: (Long) -> Unit,
    onToggleFavorite: (Song) -> Unit,
    onDeleteSong: (Long) -> Unit = {}
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(songs) { song ->
            SongItem(
                song = song,
                onClick = { onSongClick(song.id) },
                onToggleFavorite = { onToggleFavorite(song) },
                onLongPress = { 
                    // Удаление песни (проверка типа песенника будет в onDeleteSong)
                    onDeleteSong(song.id)
                }
            )
        }
    }
}

@Composable
private fun SongItem(
    song: Song,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onLongPress: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onLongPress() }
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), // уменьшить высоту строки: изменяйте отступ здесь
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${song.number}. ${song.title}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Добавить в избранное",
                    // ЗВЕЗДОЧКА: желтая при активном состоянии, с контуром при неактивном
                    tint = if (song.isFavorite) androidx.compose.ui.graphics.Color(0xFFFFC107) // Желтая
                    else androidx.compose.ui.graphics.Color(0xFFc9c5d8) // С контуром (цвет по умолчанию)
                )
            }
        }
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
                color = MaterialTheme.colorScheme.error
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
