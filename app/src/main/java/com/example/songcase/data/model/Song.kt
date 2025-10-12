package com.example.songcase.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val number: Int,
    val title: String,
    val text: String,
    val author: String? = null,
    val key: String? = null, // Тональность песни
    val tempo: String? = null,
    val timeSignature: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isFavorite: Boolean = false,
    val source: String? = null // URL источника, если песня импортирована
)

@Entity(tableName = "chords")
data class Chord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val songId: Long,
    val chord: String, // Название аккорда (например, "C", "Am", "F#m")
    val position: Int, // Позиция в тексте (индекс символа)
    val lineNumber: Int // Номер строки для отображения
)

@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey
    val songId: Long,
    val addedAt: Date = Date()
)
