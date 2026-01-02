package com.example.audiobookreader.domain.usecase

import com.example.audiobookreader.domain.model.Book
import com.example.audiobookreader.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBooksUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    operator fun invoke(): Flow<List<Book>> {
        return bookRepository.getAllBooks()
    }
}
