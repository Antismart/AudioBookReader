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
    val coverPath: String? = null,
    val totalCharacters: Int = 0,
    val currentPosition: Int = 0,
    val lastReadAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
