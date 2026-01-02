package com.example.audiobookreader.domain.usecase

import android.util.Log
import com.example.audiobookreader.domain.repository.BookRepository
import com.example.audiobookreader.domain.util.Result
import javax.inject.Inject

class UpdateProgressUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    
    companion object {
        private const val TAG = "UpdateProgressUseCase"
    }
    
    data class Params(
        val bookId: Long,
        val position: Long,
        val page: Int
    )
    
    suspend operator fun invoke(params: Params): Result<Unit> {
        return try {
            bookRepository.updateProgress(
                bookId = params.bookId,
                position = params.position,
                page = params.page
            )
            Log.d(TAG, "Progress updated for book ${params.bookId}: position=${params.position}, page=${params.page}")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update progress", e)
            Result.Error("Failed to update progress: ${e.message}", e)
        }
    }
}
