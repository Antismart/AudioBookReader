package com.example.audiobookreader.domain.usecase

import com.example.audiobookreader.domain.model.Book
import com.example.audiobookreader.domain.repository.BookRepository
import com.example.audiobookreader.domain.util.Result
import javax.inject.Inject

class GetBookByIdUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(bookId: Long): Result<Book> {
        return try {
            val book = bookRepository.getBookById(bookId)
            if (book != null) {
                Result.Success(book)
            } else {
                Result.Error("Book not found")
            }
        } catch (e: Exception) {
            Result.Error("Failed to get book: ${e.message}", e)
        }
    }
}
