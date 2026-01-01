package com.example.audiobookreader.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val author: String,
    val filePath: String,
    val fileType: String,
    val coverImagePath: String? = null,
    val totalPages: Int = 0,
    val currentPage: Int = 0,
    val currentPosition: Long = 0,
    val duration: Long = 0,
    val lastReadTimestamp: Long = System.currentTimeMillis(),
    val dateAdded: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false
)
