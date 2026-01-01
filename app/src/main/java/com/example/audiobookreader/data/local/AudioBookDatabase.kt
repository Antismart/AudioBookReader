package com.example.audiobookreader.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.audiobookreader.data.local.dao.BookDao
import com.example.audiobookreader.data.local.entity.BookEntity

@Database(
    entities = [BookEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AudioBookDatabase : RoomDatabase() {
    
    abstract fun bookDao(): BookDao
    
    companion object {
        const val DATABASE_NAME = "audiobook_database"
    }
}
