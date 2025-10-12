package com.example.songcase.data.dao

import androidx.room.*
import com.example.songcase.data.model.Chord
import kotlinx.coroutines.flow.Flow

@Dao
interface ChordDao {
    @Query("SELECT * FROM chords WHERE songId = :songId ORDER BY lineNumber ASC, position ASC")
    fun getChordsForSong(songId: Long): Flow<List<Chord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChord(chord: Chord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChords(chords: List<Chord>)

    @Update
    suspend fun updateChord(chord: Chord)

    @Delete
    suspend fun deleteChord(chord: Chord)

    @Query("DELETE FROM chords WHERE songId = :songId")
    suspend fun deleteChordsForSong(songId: Long)

    @Query("SELECT * FROM chords WHERE songId = :songId AND lineNumber = :lineNumber ORDER BY position ASC")
    suspend fun getChordsForLine(songId: Long, lineNumber: Int): List<Chord>
}
