package com.example.audiobookreader.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.audiobookreader.data.local.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    
    @Query("SELECT * FROM books ORDER BY lastReadAt DESC")
    fun getAllBooks(): Flow<List<BookEntity>>
    
    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookById(id: Long): BookEntity?
    
    @Query("SELECT * FROM books WHERE filePath = :filePath")
    suspend fun getBookByFilePath(filePath: String): BookEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity): Long
    
    @Update
    suspend fun updateBook(book: BookEntity)
    
    @Delete
    suspend fun deleteBook(book: BookEntity)
    
    @Query("UPDATE books SET currentPosition = :position, lastReadAt = :lastReadAt WHERE id = :id")
    suspend fun updateReadingProgress(id: Long, position: Int, lastReadAt: Long)
}
