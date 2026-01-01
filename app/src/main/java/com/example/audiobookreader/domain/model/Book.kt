package com.example.audiobookreader.domain.model

data class Book(
    val id: Long = 0,
    val title: String,
    val author: String = "Unknown",
    val filePath: String,
    val fileType: FileType,
    val coverImagePath: String? = null,
    val totalPages: Int = 0,
    val currentPage: Int = 0,
    val currentPosition: Long = 0,
    val duration: Long = 0,
    val lastReadTimestamp: Long = System.currentTimeMillis(),
    val dateAdded: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false
) {
    val progress: Float
        get() = if (duration > 0) currentPosition.toFloat() / duration else 0f
}

enum class FileType {
    TXT, PDF, EPUB, UNKNOWN;
    
    companion object {
        fun fromExtension(extension: String): FileType {
            return when (extension.lowercase()) {
                "txt" -> TXT
                "pdf" -> PDF
                "epub" -> EPUB
                else -> UNKNOWN
            }
        }
    }
}
