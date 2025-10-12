package com.example.songcase.data.repository

import com.example.songcase.data.dao.ChordDao
import com.example.songcase.data.dao.SongDao
import com.example.songcase.data.database.SongDatabase
import com.example.songcase.data.model.Chord
import com.example.songcase.data.model.Song
import kotlinx.coroutines.flow.Flow
class SongRepository(
    private val songDao: SongDao = SongDatabase.getDatabase().songDao(),
    private val chordDao: ChordDao = SongDatabase.getDatabase().chordDao()
) {
    fun getAllSongs(): Flow<List<Song>> = songDao.getAllSongs()
    
    suspend fun getSongById(id: Long): Song? = songDao.getSongById(id)
    
    suspend fun getSongByNumber(number: Int): Song? = songDao.getSongByNumber(number)
    
    fun searchSongs(query: String): Flow<List<Song>> = songDao.searchSongs(query)
    
    fun getFavoriteSongs(): Flow<List<Song>> = songDao.getFavoriteSongs()
    
    suspend fun insertSong(song: Song): Long = songDao.insertSong(song)
    
    suspend fun updateSong(song: Song) = songDao.updateSong(song)
    
    suspend fun deleteSong(song: Song) = songDao.deleteSong(song)
    
    suspend fun updateFavoriteStatus(songId: Long, isFavorite: Boolean) = 
        songDao.updateFavoriteStatus(songId, isFavorite)
    
    suspend fun getSongCount(): Int = songDao.getSongCount()
    
    suspend fun getMaxSongNumber(): Int? = songDao.getMaxSongNumber()
    
    fun getChordsForSong(songId: Long): Flow<List<Chord>> = chordDao.getChordsForSong(songId)
    
    suspend fun insertChord(chord: Chord) = chordDao.insertChord(chord)
    
    suspend fun insertChords(chords: List<Chord>) = chordDao.insertChords(chords)
    
    suspend fun updateChord(chord: Chord) = chordDao.updateChord(chord)
    
    suspend fun deleteChord(chord: Chord) = chordDao.deleteChord(chord)
    
    suspend fun deleteChordsForSong(songId: Long) = chordDao.deleteChordsForSong(songId)
    
    suspend fun getChordsForLine(songId: Long, lineNumber: Int): List<Chord> = 
        chordDao.getChordsForLine(songId, lineNumber)
}
