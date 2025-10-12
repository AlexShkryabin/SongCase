package com.example.songcase.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.songcase.data.dao.ChordDao
import com.example.songcase.data.dao.SongDao
import com.example.songcase.data.model.Chord
import com.example.songcase.data.model.Song

@Database(
    entities = [Song::class, Chord::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SongDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun chordDao(): ChordDao

    companion object {
        @Volatile
        private var INSTANCE: SongDatabase? = null

        fun getDatabase(): SongDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    android.app.Application(),
                    SongDatabase::class.java,
                    "song_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
