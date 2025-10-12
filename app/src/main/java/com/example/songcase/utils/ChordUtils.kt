package com.example.songcase.utils

import com.example.songcase.data.model.Chord

object ChordUtils {
    
    // Массив нот для транспонирования (включая H для немецкой системы)
    private val notes = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "H")
    
    // Регулярное выражение для поиска аккордов (включая H)
    private val chordPattern = Regex("""[A-H][#b]?(?:m|maj|min|dim|aug|sus|add|7|9|11|13)?(?:/[A-H][#b]?)?""")
    
    /**
     * Находит все аккорды в тексте песни
     */
    fun findChordsInText(text: String): List<Chord> {
        val chords = mutableListOf<Chord>()
        val lines = text.split("\n")
        
        lines.forEachIndexed { lineIndex, line ->
            val matches = chordPattern.findAll(line)
            matches.forEach { matchResult ->
                val chord = Chord(
                    id = 0,
                    songId = 0,
                    chord = matchResult.value,
                    position = matchResult.range.first,
                    lineNumber = lineIndex
                )
                chords.add(chord)
            }
        }
        
        return chords
    }
    
    /**
     * Транспонирует аккорд на указанное количество полутонов
     */
    fun transposeChord(chord: String, semitones: Int): String {
        if (semitones == 0) return chord
        
        // Разбираем аккорд на основную ноту и суффикс
        val (rootNote, suffix) = parseChord(chord)
        val transposedRoot = transposeNote(rootNote, semitones)
        
        return if (suffix.isNotEmpty()) {
            "$transposedRoot$suffix"
        } else {
            transposedRoot
        }
    }
    
    /**
     * Транспонирует ноту на указанное количество полутонов
     */
    private fun transposeNote(note: String, semitones: Int): String {
        val currentIndex = notes.indexOf(note)
        if (currentIndex == -1) return note
        
        val newIndex = (currentIndex + semitones) % 12
        val adjustedIndex = if (newIndex < 0) newIndex + 12 else newIndex
        
        return notes[adjustedIndex]
    }
    
    /**
     * Разбирает аккорд на основную ноту и суффикс
     */
    private fun parseChord(chord: String): Pair<String, String> {
        // Ищем основную ноту (C, C#, D, D#, E, F, F#, G, G#, A, A#, B, H)
        val rootNotePattern = Regex("""^([A-H][#b]?)""")
        val rootMatch = rootNotePattern.find(chord)
        
        if (rootMatch != null) {
            val rootNote = rootMatch.value
            val suffix = chord.substring(rootNote.length)
            return Pair(rootNote, suffix)
        }
        
        return Pair(chord, "")
    }
    
    /**
     * Транспонирует текст песни с аккордами
     */
    fun transposeText(text: String, semitones: Int): String {
        if (semitones == 0) return text
        
        val lines = text.split("\n")
        val transposedLines = lines.map { line ->
            chordPattern.replace(line) { matchResult ->
                transposeChord(matchResult.value, semitones)
            }
        }
        
        return transposedLines.joinToString("\n")
    }
    
    /**
     * Проверяет, является ли строка аккордом
     */
    fun isChord(text: String): Boolean {
        return chordPattern.matches(text.trim())
    }
    
    /**
     * Получает список всех возможных аккордов для транспонирования
     */
    fun getAllPossibleChords(): List<String> {
        val suffixes = listOf("", "m", "maj", "min", "dim", "aug", "sus2", "sus4", "7", "m7", "maj7", "9", "m9", "11", "13")
        val chords = mutableListOf<String>()
        
        notes.forEach { note ->
            suffixes.forEach { suffix ->
                chords.add(note + suffix)
            }
        }
        
        return chords
    }
}