package com.example.songcase.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
// Hilt removed for simplicity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.songcase.data.model.Song
import com.example.songcase.ui.viewmodel.SongListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongListScreen(
    onSongClick: (Long) -> Unit,
    onAddSongClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onJsonImportClick: () -> Unit,
    viewModel: SongListViewModel = remember { SongListViewModel() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Песенник") },
                actions = {
                    IconButton(onClick = { showSearch = !showSearch }) {
                        Icon(Icons.Default.Search, contentDescription = "Поиск")
                    }
                    IconButton(onClick = onAddSongClick) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить песню")
                    }
                    IconButton(onClick = onJsonImportClick) {
                        Text("JSON", style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = onFavoritesClick) {
                        Icon(Icons.Default.Favorite, contentDescription = "Избранное")
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
                        }
                    )
                }
            }
        }
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
    onToggleFavorite: (Song) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(songs) { song ->
            SongItem(
                song = song,
                onClick = { onSongClick(song.id) },
                onToggleFavorite = { onToggleFavorite(song) }
            )
        }
    }
}

@Composable
private fun SongItem(
    song: Song,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                    Icons.Default.Favorite,
                    contentDescription = "Добавить в избранное",
                    tint = if (song.isFavorite) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
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
