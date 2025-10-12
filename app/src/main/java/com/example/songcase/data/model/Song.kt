package com.example.songcase.data.model

import java.util.Date

data class Song(
    val id: Long = 0,
    val number: Int,
    val title: String,
    val text: String,
    val tempo: String? = null,
    val timeSignature: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isFavorite: Boolean = false,
    val source: String? = null // URL источника, если песня импортирована
)

data class Chord(
    val id: Long = 0,
    val songId: Long,
    val chord: String, // Название аккорда (например, "C", "Am", "F#m")
    val position: Int, // Позиция в тексте (индекс символа)
    val lineNumber: Int // Номер строки для отображения
)

data class Favorite(
    val songId: Long,
    val addedAt: Date = Date()
)