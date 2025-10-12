package com.example.songcase.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.songcase.data.SongDataStore
import com.example.songcase.ui.screen.AddSongScreen
import com.example.songcase.ui.screen.FavoritesScreen
import com.example.songcase.ui.screen.ImportSongScreen
import com.example.songcase.ui.screen.JsonImportScreen
import com.example.songcase.ui.screen.SettingsScreen
import com.example.songcase.ui.screen.SongDetailScreen
import com.example.songcase.ui.screen.SongEditorScreen
import com.example.songcase.ui.screen.SongListScreen

@Composable
fun SongNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "song_list"
    ) {
        composable("song_list") {
            SongListScreen(
                onSongClick = { songId ->
                    navController.navigate("song_detail/$songId")
                },
                onAddSongClick = {
                    navController.navigate("add_song")
                },
                onFavoritesClick = {
                    navController.navigate("favorites")
                },
                onJsonImportClick = {
                    navController.navigate("json_import")
                }
            )
        }
        
        composable("add_song") {
            AddSongScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("import_song") {
            ImportSongScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onImportSuccess = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("song_detail/{songId}") { backStackEntry ->
            val songId = backStackEntry.arguments?.getString("songId")?.toLongOrNull() ?: 0L
            SongDetailScreen(
                songId = songId,
                onBackClick = {
                    navController.popBackStack()
                },
                onEditClick = { editSongId ->
                    navController.navigate("song_editor/$editSongId")
                },
                onSettingsClick = {
                    navController.navigate("settings")
                },
                onDeleteClick = { deleteSongId ->
                    SongDataStore.deleteSong(deleteSongId)
                    navController.popBackStack()
                }
            )
        }
        
        composable("song_editor") {
            SongEditorScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("song_editor/{songId}") { backStackEntry ->
            val songId = backStackEntry.arguments?.getString("songId")?.toLongOrNull()
            SongEditorScreen(
                songId = songId,
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("json_import") {
            JsonImportScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onImportSuccess = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("settings") {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("favorites") {
            FavoritesScreen(
                onSongClick = { songId ->
                    navController.navigate("song_detail/$songId")
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
