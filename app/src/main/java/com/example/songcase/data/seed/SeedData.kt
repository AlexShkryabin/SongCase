package com.example.songcase.data.seed

import com.example.songcase.data.model.Chord
import com.example.songcase.data.model.Song
import java.util.Date

object SeedData {
    
    fun getSampleSongs(): List<Song> {
        return listOf(
            Song(
                number = 1,
                title = "Аминь",
                text = "C G Am F\n" +
                        "Аминь, аминь, аминь\n" +
                        "C G Am F\n" +
                        "Слава Богу нашему\n\n" +
                        "C G Am F\n" +
                        "Аминь, аминь, аминь\n" +
                        "C G Am F\n" +
                        "Слава Богу нашему",
                author = "Народная",
                key = "C",
                createdAt = Date(),
                updatedAt = Date()
            ),
            Song(
                number = 2,
                title = "Великий Бог",
                text = "C F C G\n" +
                        "Великий Бог, когда на мир смотрю я\n" +
                        "C F C G\n" +
                        "И вижу все дела Твои\n" +
                        "Am F C G\n" +
                        "И звезды, и луну, и солнце яркое\n" +
                        "C F C G\n" +
                        "Тогда пою я: \"Слава Тебе!\"",
                author = "Слово Жизни",
                key = "C",
                createdAt = Date(),
                updatedAt = Date()
            )
        )
    }
    
    fun getSampleChords(): List<Chord> {
        return listOf(
            // Аккорды для первой песни
            Chord(songId = 1, chord = "C", position = 0, lineNumber = 0),
            Chord(songId = 1, chord = "G", position = 2, lineNumber = 0),
            Chord(songId = 1, chord = "Am", position = 4, lineNumber = 0),
            Chord(songId = 1, chord = "F", position = 7, lineNumber = 0),
            Chord(songId = 1, chord = "C", position = 0, lineNumber = 1),
            Chord(songId = 1, chord = "G", position = 2, lineNumber = 1),
            Chord(songId = 1, chord = "Am", position = 4, lineNumber = 1),
            Chord(songId = 1, chord = "F", position = 7, lineNumber = 1),
            
            // Аккорды для второй песни
            Chord(songId = 2, chord = "C", position = 0, lineNumber = 0),
            Chord(songId = 2, chord = "F", position = 2, lineNumber = 0),
            Chord(songId = 2, chord = "C", position = 4, lineNumber = 0),
            Chord(songId = 2, chord = "G", position = 6, lineNumber = 0),
            Chord(songId = 2, chord = "C", position = 0, lineNumber = 1),
            Chord(songId = 2, chord = "F", position = 2, lineNumber = 1),
            Chord(songId = 2, chord = "C", position = 4, lineNumber = 1),
            Chord(songId = 2, chord = "G", position = 6, lineNumber = 1),
            Chord(songId = 2, chord = "Am", position = 0, lineNumber = 2),
            Chord(songId = 2, chord = "F", position = 3, lineNumber = 2),
            Chord(songId = 2, chord = "C", position = 5, lineNumber = 2),
            Chord(songId = 2, chord = "G", position = 7, lineNumber = 2),
            Chord(songId = 2, chord = "C", position = 0, lineNumber = 3),
            Chord(songId = 2, chord = "F", position = 2, lineNumber = 3),
            Chord(songId = 2, chord = "C", position = 4, lineNumber = 3),
            Chord(songId = 2, chord = "G", position = 6, lineNumber = 3)
        )
    }
}
