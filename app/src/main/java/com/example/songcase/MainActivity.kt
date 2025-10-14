package com.example.songcase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.songcase.navigation.SongNavigation
import com.example.songcase.data.SongDataStore
import com.example.songcase.data.AppSettings
import com.example.songcase.ui.theme.SongCaseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Инициализируем SongDataStore с контекстом
        SongDataStore.initialize(this)
        // Инициализируем настройки приложения
        AppSettings.initialize(this)
        // Импортируем встроенный песенник при первом запуске (файл должен лежать в assets)
        // Изменить имя файла здесь, если нужно
        SongDataStore.importBuiltInFromAssets(this, "songs_formatted.json")
        // Импортируем кастомный сид при первом запуске кастомного песенника
        SongDataStore.importCustomSeedFromAssets(this, "custom_seed.json")
        
        setContent {
            SongCaseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SongNavigation()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SongCasePreview() {
    SongCaseTheme {
        SongNavigation()
    }
}