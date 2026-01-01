package com.example.audiobookreader.domain.repository

import com.example.audiobookreader.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    
    fun getAllBooks(): Flow<List<Book>>
    
    suspend fun getBookById(id: Long): Book?
    
    suspend fun getBookByFilePath(filePath: String): Book?
    
    suspend fun insertBook(book: Book): Long
    
    suspend fun updateBook(book: Book)
    
    suspend fun deleteBook(book: Book)
    
    suspend fun updateReadingProgress(id: Long, position: Int)
}
