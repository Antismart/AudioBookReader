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
    
    @Query("SELECT * FROM books ORDER BY lastReadTimestamp DESC")
    fun getAllBooks(): Flow<List<BookEntity>>
    
    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getBookById(bookId: Long): BookEntity?
    
    @Query("SELECT * FROM books WHERE id = :bookId")
    fun getBookByIdFlow(bookId: Long): Flow<BookEntity?>
    
    @Query("SELECT * FROM books WHERE filePath = :filePath")
    suspend fun getBookByFilePath(filePath: String): BookEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity): Long
    
    @Update
    suspend fun updateBook(book: BookEntity)
    
    @Delete
    suspend fun deleteBook(book: BookEntity)
    
    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun deleteBookById(bookId: Long)
    
    @Query("UPDATE books SET currentPosition = :position, currentPage = :page, lastReadTimestamp = :timestamp WHERE id = :bookId")
    suspend fun updateProgress(bookId: Long, position: Long, page: Int, timestamp: Long)
    
    @Query("UPDATE books SET isCompleted = :completed WHERE id = :bookId")
    suspend fun markAsCompleted(bookId: Long, completed: Boolean)
    
    @Query("SELECT COUNT(*) FROM books")
    suspend fun getBookCount(): Int
}
