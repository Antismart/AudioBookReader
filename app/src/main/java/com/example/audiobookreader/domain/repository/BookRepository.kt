package com.example.audiobookreader.domain.repository

import com.example.audiobookreader.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    
    fun getAllBooks(): Flow<List<Book>>
    
    suspend fun getBookById(bookId: Long): Book?
    
    fun getBookByIdFlow(bookId: Long): Flow<Book?>
    
    suspend fun addBook(book: Book): Long
    
    suspend fun updateBook(book: Book)
    
    suspend fun deleteBook(book: Book)
    
    suspend fun deleteBookById(bookId: Long)
    
    suspend fun updateProgress(bookId: Long, position: Long, page: Int)
    
    suspend fun markAsCompleted(bookId: Long, completed: Boolean)
    
    suspend fun getBookCount(): Int
}
