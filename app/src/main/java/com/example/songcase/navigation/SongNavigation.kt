package com.example.songcase.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.songcase.ui.screen.FavoritesScreen
import com.example.songcase.ui.screen.ImportSongScreen
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
                    navController.navigate("song_editor")
                },
                onFavoritesClick = {
                    navController.navigate("favorites")
                },
                onImportClick = {
                    navController.navigate("import_song")
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
