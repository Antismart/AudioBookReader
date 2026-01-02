package com.example.audiobookreader.domain.usecase

import android.util.Log
import com.example.audiobookreader.core.parser.ParserFactory
import com.example.audiobookreader.core.parser.ParserResult
import com.example.audiobookreader.domain.model.Book
import com.example.audiobookreader.domain.model.FileType
import com.example.audiobookreader.domain.repository.BookRepository
import com.example.audiobookreader.domain.util.Result
import java.io.File
import javax.inject.Inject

class AddBookUseCase @Inject constructor(
    private val bookRepository: BookRepository,
    private val parserFactory: ParserFactory
) {
    
    companion object {
        private const val TAG = "AddBookUseCase"
    }
    
    suspend operator fun invoke(filePath: String): Result<Book> {
        return try {
            val file = File(filePath)
            
            if (!file.exists()) {
                return Result.Error("File not found: $filePath")
            }
            
            val parser = parserFactory.getParser(file)
                ?: return Result.Error("Unsupported file type: ${file.extension}")
            
            // Parse the file to extract metadata
            val parseResult = parser.extractText(file)
            
            val book = when (parseResult) {
                is ParserResult.Success -> {
                    Book(
                        title = parseResult.metadata.title,
                        author = parseResult.metadata.author ?: "Unknown",
                        filePath = filePath,
                        fileType = FileType.fromExtension(file.extension),
                        totalPages = parseResult.chapters.size,
                        duration = parseResult.text.length.toLong()
                    )
                }
                is ParserResult.Error -> {
                    // Create book with basic info if parsing fails
                    Book(
                        title = file.nameWithoutExtension,
                        author = "Unknown",
                        filePath = filePath,
                        fileType = FileType.fromExtension(file.extension)
                    )
                }
            }
            
            // Save to database
            val bookId = bookRepository.addBook(book)
            val savedBook = book.copy(id = bookId)
            
            Log.d(TAG, "Book added: ${savedBook.title}")
            Result.Success(savedBook)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error adding book", e)
            Result.Error("Failed to add book: ${e.message}", e)
        }
    }
}
