package com.example.audiobookreader.core.parser

import java.io.File

interface TextParser {
    suspend fun extractText(file: File): ParserResult
    fun supportsFileType(extension: String): Boolean
}

sealed class ParserResult {
    data class Success(
        val text: String,
        val chapters: List<Chapter>,
        val metadata: BookMetadata
    ) : ParserResult()

    data class Error(val message: String, val throwable: Throwable? = null) : ParserResult()
}

data class BookMetadata(
    val title: String,
    val author: String?,
    val publisher: String? = null,
    val language: String? = null
)

data class Chapter(
    val title: String,
    val startPosition: Int,
    val endPosition: Int,
    val text: String
)
