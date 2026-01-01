package com.example.audiobookreader.domain.usecase

import android.util.Log
import com.example.audiobookreader.domain.model.Book
import com.example.audiobookreader.domain.repository.BookRepository
import com.example.audiobookreader.domain.util.Result
import java.io.File
import javax.inject.Inject

class DeleteBookUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    
    companion object {
        private const val TAG = "DeleteBookUseCase"
    }
    
    suspend operator fun invoke(book: Book, deleteFile: Boolean = false): Result<Unit> {
        return try {
            // Delete from database
            bookRepository.deleteBook(book)
            
            // Optionally delete the physical file
            if (deleteFile) {
                try {
                    val file = File(book.filePath)
                    if (file.exists()) {
                        file.delete()
                        Log.d(TAG, "Deleted file: ${book.filePath}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error deleting physical file", e)
                    // Continue even if file deletion fails
                }
            }
            
            Log.d(TAG, "Book deleted: ${book.title}")
            Result.Success(Unit)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting book", e)
            Result.Error("Failed to delete book: ${e.message}", e)
        }
    }
}
