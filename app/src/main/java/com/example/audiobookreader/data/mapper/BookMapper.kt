package com.example.audiobookreader.data.mapper

import com.example.audiobookreader.data.local.entity.BookEntity
import com.example.audiobookreader.domain.model.Book
import com.example.audiobookreader.domain.model.FileType

fun BookEntity.toDomain(): Book {
    return Book(
        id = id,
        title = title,
        author = author,
        filePath = filePath,
        fileType = FileType.fromExtension(fileType),
        coverImagePath = coverImagePath,
        totalPages = totalPages,
        currentPage = currentPage,
        currentPosition = currentPosition,
        duration = duration,
        lastReadTimestamp = lastReadTimestamp,
        dateAdded = dateAdded,
        isCompleted = isCompleted
    )
}

fun Book.toEntity(): BookEntity {
    return BookEntity(
        id = id,
        title = title,
        author = author,
        filePath = filePath,
        fileType = fileType.name.lowercase(),
        coverImagePath = coverImagePath,
        totalPages = totalPages,
        currentPage = currentPage,
        currentPosition = currentPosition,
        duration = duration,
        lastReadTimestamp = lastReadTimestamp,
        dateAdded = dateAdded,
        isCompleted = isCompleted
    )
}
