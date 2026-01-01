package com.example.audiobookreader.data.repository

import com.example.audiobookreader.data.local.dao.BookDao
import com.example.audiobookreader.data.local.entity.BookEntity
import com.example.audiobookreader.domain.model.Book
import com.example.audiobookreader.domain.model.FileType
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
    
    override suspend fun getBookById(id: Long): Book? {
        return bookDao.getBookById(id)?.toDomain()
    }
    
    override suspend fun getBookByFilePath(filePath: String): Book? {
        return bookDao.getBookByFilePath(filePath)?.toDomain()
    }
    
    override suspend fun insertBook(book: Book): Long {
        return bookDao.insertBook(book.toEntity())
    }
    
    override suspend fun updateBook(book: Book) {
        bookDao.updateBook(book.toEntity())
    }
    
    override suspend fun deleteBook(book: Book) {
        bookDao.deleteBook(book.toEntity())
    }
    
    override suspend fun updateReadingProgress(id: Long, position: Int) {
        bookDao.updateReadingProgress(id, position, System.currentTimeMillis())
    }
    
    private fun BookEntity.toDomain(): Book {
        return Book(
            id = id,
            title = title,
            author = author,
            filePath = filePath,
            fileType = FileType.fromExtension(fileType),
            coverImagePath = coverPath,
            totalPages = totalCharacters,
            currentPage = currentPosition,
            currentPosition = currentPosition.toLong(),
            lastReadTimestamp = lastReadAt,
            dateAdded = createdAt
        )
    }
    
    private fun Book.toEntity(): BookEntity {
        return BookEntity(
            id = id,
            title = title,
            author = author,
            filePath = filePath,
            fileType = fileType.name.lowercase(),
            coverPath = coverImagePath,
            totalCharacters = totalPages,
            currentPosition = currentPosition.toInt(),
            lastReadAt = lastReadTimestamp,
            createdAt = dateAdded
        )
    }
}
