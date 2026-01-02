package com.example.audiobookreader.domain.usecase

import android.util.Log
import com.example.audiobookreader.core.parser.ParserFactory
import com.example.audiobookreader.core.parser.ParserResult
import com.example.audiobookreader.domain.model.Book
import com.example.audiobookreader.domain.util.Result
import java.io.File
import javax.inject.Inject

class ExtractBookTextUseCase @Inject constructor(
    private val parserFactory: ParserFactory
) {
    
    companion object {
        private const val TAG = "ExtractBookTextUseCase"
    }
    
    data class BookContent(
        val fullText: String,
        val chapters: List<com.example.audiobookreader.core.parser.Chapter>
    )
    
    suspend operator fun invoke(book: Book): Result<BookContent> {
        return try {
            val file = File(book.filePath)
            
            if (!file.exists()) {
                return Result.Error("File not found: ${book.filePath}")
            }
            
            val parser = parserFactory.getParser(file)
                ?: return Result.Error("No parser available for file type: ${book.fileType}")
            
            when (val parseResult = parser.extractText(file)) {
                is ParserResult.Success -> {
                    Log.d(TAG, "Extracted ${parseResult.text.length} characters from ${book.title}")
                    Result.Success(
                        BookContent(
                            fullText = parseResult.text,
                            chapters = parseResult.chapters
                        )
                    )
                }
                
                is ParserResult.Error -> {
                    Log.e(TAG, "Failed to extract text: ${parseResult.message}", parseResult.throwable)
                    Result.Error(parseResult.message, parseResult.throwable)
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting book text", e)
            Result.Error("Error extracting text: ${e.message}", e)
        }
    }
}
