package com.example.songcase.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
// Hilt removed for simplicity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.songcase.ui.viewmodel.SongEditorViewModel
import com.example.songcase.ui.viewmodel.SongEditorUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongEditorScreen(
    songId: Long? = null,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    viewModel: SongEditorViewModel = remember { SongEditorViewModel() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(songId) {
        if (songId != null) {
            viewModel.loadSong(songId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (songId == null) "Новая песня" else "Редактировать песню") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.saveSong()
                            onSaveClick()
                        },
                        enabled = uiState.canSave
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Сохранить")
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
                        onDismiss = { viewModel.clearError() }
                    )
                }
            }
            
            else -> {
                        SongEditorForm(
                            uiState = uiState,
                            onTitleChange = { viewModel.updateTitle(it) },
                            onTextChange = { viewModel.updateText(it) },
                            onNumberChange = { viewModel.updateNumber(it) },
                            modifier = Modifier.padding(paddingValues)
                        )
            }
        }
    }
}

@Composable
private fun SongEditorForm(
    uiState: SongEditorUiState,
    onTitleChange: (String) -> Unit,
    onTextChange: (String) -> Unit,
    onNumberChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Номер песни
        OutlinedTextField(
            value = uiState.number.toString(),
            onValueChange = { value ->
                value.toIntOrNull()?.let { onNumberChange(it) }
            },
            label = { Text("Номер песни") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        // Название песни
        OutlinedTextField(
            value = uiState.title,
            onValueChange = onTitleChange,
            label = { Text("Название песни") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        // Текст песни
        OutlinedTextField(
            value = uiState.text,
            onValueChange = onTextChange,
            label = { Text("Текст песни") },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            maxLines = 15
        )
        
        // Подсказка по аккордам
        Card {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Подсказка по аккордам",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Для добавления аккордов в текст используйте формат:\n" +
                            "C Am F G\n" +
                            "Текст первой строки\n\n" +
                            "Dm G C\n" +
                            "Текст второй строки",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ErrorMessage(
    message: String,
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
                text = "Ошибка: $message",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onDismiss) {
                Text("Закрыть")
            }
        }
    }
}
