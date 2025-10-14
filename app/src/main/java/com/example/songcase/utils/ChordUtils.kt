package com.example.songcase.utils

import com.example.songcase.data.model.Chord

object ChordUtils {
    // Маппинг аккордов для транспонирования (включая H для немецкой нотации)
    private val chordMap = mapOf(
        "C" to 0, "C#" to 1, "Db" to 1, "D" to 2, "D#" to 3, "Eb" to 3,
        "E" to 4, "F" to 5, "F#" to 6, "Gb" to 6, "G" to 7, "G#" to 8,
        "Ab" to 8, "A" to 9, "A#" to 10, "Bb" to 10, "B" to 11, "H" to 11
    )

    private val reverseChordMap = mapOf(
        0 to "C", 1 to "C#", 2 to "D", 3 to "D#", 4 to "E", 5 to "F",
        6 to "F#", 7 to "G", 8 to "G#", 9 to "A", 10 to "A#", 11 to "B"
    )

    // Простое регулярное выражение для поиска аккордов
    // Поддерживает: F, (F), Em7, (Em7), Am/G, (Am/G), Hm (H)
    private val chordRegex = Regex("""(?:\([A-H][#b]?(?:m|maj|min|dim|aug|sus|add|7|9|11|13)?(?:/[A-H][#b]?)?\)|[A-H][#b]?(?:m|maj|min|dim|aug|sus|add|7|9|11|13)?(?:/[A-H][#b]?)?(?:\([A-H][#b]?\))?)""")

    /** Транспонирует один аккорд, сохраняя суффиксы и бас */
    fun transposeChord(chord: String, semitones: Int): String {
        if (semitones == 0) return chord
        val (root, suffix) = extractRootAndSuffix(chord)
        val rootNumber = chordMap[root] ?: return chord
        val newNumber = (rootNumber + semitones + 12) % 12
        val newRoot = reverseChordMap[newNumber] ?: return chord

        // Транспонируем бас, если он есть
        val bassMatch = Regex("""/([A-H][#b]?)""").find(suffix)
        val newSuffix = if (bassMatch != null) {
            val bass = bassMatch.groupValues[1]
            val bassNum = chordMap[bass]
            if (bassNum != null) {
                val newBassNum = (bassNum + semitones + 12) % 12
                val newBass = reverseChordMap[newBassNum] ?: bass
                suffix.replace(bass, newBass)
            } else suffix
        } else suffix

        return newRoot + newSuffix
    }

    /** Возвращает список аккордов с позициями построчно */
    fun findChordsInText(text: String): List<Chord> {
        val result = mutableListOf<Chord>()
        val lines = text.split("\n")
        lines.forEachIndexed { lineIndex, line ->
            chordRegex.findAll(line).forEach { mr ->
                result.add(
                    Chord(
                        id = 0,
                        songId = 0,
                        chord = mr.value,
                        position = mr.range.first,
                        lineNumber = lineIndex
                    )
                )
            }
        }
        return result
    }

    /** Транспонирует все аккорды внутри текста */
    fun transposeText(text: String, semitones: Int): String {
        if (semitones == 0) return text
        return chordRegex.replace(text) { mr -> transposeChord(mr.value, semitones) }
    }

    private fun extractRootAndSuffix(chord: String): Pair<String, String> {
        val clean = chord.trim()
        val root = Regex("""^([A-H][#b]?)""").find(clean)?.value ?: return chord to ""
        val suffix = clean.substring(root.length)
        return root to suffix
    }

    fun isChord(token: String): Boolean {
        val trimmed = token.trim()
        if (trimmed.isEmpty()) return false
        
        // Полное регулярное выражение для всех типов аккордов
        // Поддерживает: E, E5, E6/9, E7(#9), E7(b5), E7sus4, Eadd9, Eaug, Eb, Ebm, Ebm7(b5), E#maj7, (E), (Em7), E/G
        val fullChordPattern = Regex("""^[A-H][#b]?(?:[0-9]+(?:/[0-9]+)?|m(?:aj)?(?:[0-9]+)?|min(?:[0-9]+)?|dim(?:[0-9]+)?|aug|sus[0-9]+|add[0-9]+|maj[0-9]+)?(?:\([#b]?[0-9]+\))?(?:/[A-H][#b]?)?(?:\([A-H][#b]?\))?$""")
        
        // Паттерн для аккордов в скобках
        val parenthesesPattern = Regex("""^\([A-H][#b]?(?:[0-9]+(?:/[0-9]+)?|m(?:aj)?(?:[0-9]+)?|min(?:[0-9]+)?|dim(?:[0-9]+)?|aug|sus[0-9]+|add[0-9]+|maj[0-9]+)?(?:\([#b]?[0-9]+\))?(?:/[A-H][#b]?)?\)$""")
        
        return fullChordPattern.matches(trimmed) || parenthesesPattern.matches(trimmed)
    }

    fun isChordLine(line: String): Boolean {
        val trimmed = line.trim()
        if (trimmed.isEmpty()) return false
        
        // Заменяем слэши на пробелы, чтобы Am/G стал Am G
        val normalizedLine = trimmed.replace("/", " ")
        
        // Разбиваем на токены по пробелам
        val tokens = normalizedLine.split(Regex("\\s+")).filter { it.isNotEmpty() }
        
        // Проверяем, что все токены являются аккордами
        return tokens.isNotEmpty() && tokens.all { isChord(it) }
    }
}

