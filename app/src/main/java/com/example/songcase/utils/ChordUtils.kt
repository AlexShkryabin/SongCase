package com.example.songcase.utils

object ChordUtils {
    
    // Маппинг аккордов для транспонирования
    private val chordMap = mapOf(
        "C" to 0, "C#" to 1, "Db" to 1, "D" to 2, "D#" to 3, "Eb" to 3,
        "E" to 4, "F" to 5, "F#" to 6, "Gb" to 6, "G" to 7, "G#" to 8,
        "Ab" to 8, "A" to 9, "A#" to 10, "Bb" to 10, "B" to 11
    )
    
    private val reverseChordMap = chordMap.entries.associate { it.value to it.key }
    
    /**
     * Транспонирует аккорд на указанное количество полутонов
     * @param chord Исходный аккорд (например, "C", "Am", "F#m")
     * @param semitones Количество полутонов для транспонирования (положительное - вверх, отрицательное - вниз)
     * @return Транспонированный аккорд
     */
    fun transposeChord(chord: String, semitones: Int): String {
        if (semitones == 0) return chord
        
        // Извлекаем основную ноту и суффикс (m, 7, maj7, etc.)
        val (rootNote, suffix) = extractRootAndSuffix(chord)
        
        // Получаем номер ноты
        val rootNumber = chordMap[rootNote] ?: return chord
        
        // Вычисляем новую ноту
        val newNumber = (rootNumber + semitones + 12) % 12
        val newRoot = reverseChordMap[newNumber] ?: return chord
        
        return newRoot + suffix
    }
    
    /**
     * Транспонирует список аккордов
     */
    fun transposeChords(chords: List<String>, semitones: Int): List<String> {
        return chords.map { transposeChord(it, semitones) }
    }
    
    /**
     * Извлекает основную ноту и суффикс из аккорда
     */
    private fun extractRootAndSuffix(chord: String): Pair<String, String> {
        // Убираем пробелы
        val cleanChord = chord.trim()
        
        // Ищем основную ноту (1-2 символа)
        val rootNote = when {
            cleanChord.length >= 2 && cleanChord[1] in listOf('#', 'b') -> cleanChord.substring(0, 2)
            else -> cleanChord.substring(0, 1)
        }
        
        val suffix = cleanChord.substring(rootNote.length)
        return Pair(rootNote, suffix)
    }
    
    /**
     * Проверяет, является ли строка аккордом
     */
    fun isChord(text: String): Boolean {
        val cleanText = text.trim()
        if (cleanText.isEmpty()) return false
        
        // Проверяем, начинается ли с известной ноты
        val rootNote = when {
            cleanText.length >= 2 && cleanText[1] in listOf('#', 'b') -> cleanText.substring(0, 2)
            else -> cleanText.substring(0, 1)
        }
        
        return chordMap.containsKey(rootNote)
    }
    
    /**
     * Парсит строку текста и извлекает аккорды
     */
    fun parseChordsFromText(text: String): List<Pair<String, Int>> {
        val chords = mutableListOf<Pair<String, Int>>()
        val lines = text.split('\n')
        
        lines.forEachIndexed { lineIndex, line ->
            val words = line.split(' ')
            var position = 0
            
            words.forEach { word ->
                if (isChord(word)) {
                    chords.add(Pair(word, position))
                }
                position += word.length + 1 // +1 для пробела
            }
        }
        
        return chords
    }
}
