package com.example.audiobookreader.data.repository

import com.example.audiobookreader.data.local.dao.BookDao
import com.example.audiobookreader.data.mapper.toDomain
import com.example.audiobookreader.data.mapper.toEntity
import com.example.audiobookreader.domain.model.Book
import com.example.audiobookreader.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val bookDao: BookDao
) : BookRepository {
    
    override fun getAllBooks(): Flow<List<Book>> {
        return bookDao.getAllBooks().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getBookById(bookId: Long): Book? {
        return bookDao.getBookById(bookId)?.toDomain()
    }
    
    override fun getBookByIdFlow(bookId: Long): Flow<Book?> {
        return bookDao.getBookByIdFlow(bookId).map { it?.toDomain() }
    }
    
    override suspend fun addBook(book: Book): Long {
        return bookDao.insertBook(book.toEntity())
    }
    
    override suspend fun updateBook(book: Book) {
        bookDao.updateBook(book.toEntity())
    }
    
    override suspend fun deleteBook(book: Book) {
        bookDao.deleteBook(book.toEntity())
    }
    
    override suspend fun deleteBookById(bookId: Long) {
        bookDao.deleteBookById(bookId)
    }
    
    override suspend fun updateProgress(bookId: Long, position: Long, page: Int) {
        bookDao.updateProgress(
            bookId = bookId,
            position = position,
            page = page,
            timestamp = System.currentTimeMillis()
        )
    }
    
    override suspend fun markAsCompleted(bookId: Long, completed: Boolean) {
        bookDao.markAsCompleted(bookId, completed)
    }
    
    override suspend fun getBookCount(): Int {
        return bookDao.getBookCount()
    }
}
