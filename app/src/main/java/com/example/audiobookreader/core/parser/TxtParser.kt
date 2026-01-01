package com.example.audiobookreader.core.parser

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class TxtParser : TextParser {
    
    override suspend fun extractText(file: File): ParserResult = withContext(Dispatchers.IO) {
        try {
            if (!file.exists()) {
                return@withContext ParserResult.Error("File does not exist")
            }
            
            val text = file.readText()
            
            if (text.isEmpty()) {
                return@withContext ParserResult.Error("File is empty")
            }
            
            // Extract basic metadata from filename
            val metadata = BookMetadata(
                title = file.nameWithoutExtension,
                author = "Unknown"
            )
            
            // Try to detect chapters by looking for common patterns
            val chapters = detectChapters(text)
            
            ParserResult.Success(
                text = text,
                chapters = chapters,
                metadata = metadata
            )
        } catch (e: Exception) {
            ParserResult.Error("Failed to parse TXT file: ${e.message}", e)
        }
    }
    
    override fun supportsFileType(extension: String): Boolean {
        return extension.lowercase() == "txt"
    }
    
    private fun detectChapters(text: String): List<Chapter> {
        val chapters = mutableListOf<Chapter>()
        
        // Common chapter patterns
        val chapterPatterns = listOf(
            Regex("(?m)^Chapter\\s+\\d+.*$", RegexOption.IGNORE_CASE),
            Regex("(?m)^\\d+\\.\\s+.*$"),
            Regex("(?m)^[IVXLCDM]+\\.\\s+.*$") // Roman numerals
        )
        
        for (pattern in chapterPatterns) {
            val matches = pattern.findAll(text)
            if (matches.count() > 1) {
                // Found likely chapter markers
                var lastEnd = 0
                matches.forEachIndexed { index, match ->
                    if (index > 0) {
                        val prevMatch = matches.elementAt(index - 1)
                        chapters.add(
                            Chapter(
                                title = prevMatch.value.trim(),
                                startPosition = prevMatch.range.first,
                                endPosition = match.range.first,
                                text = text.substring(prevMatch.range.first, match.range.first)
                            )
                        )
                    }
                    lastEnd = match.range.first
                }
                
                // Add last chapter
                matches.lastOrNull()?.let { lastMatch ->
                    chapters.add(
                        Chapter(
                            title = lastMatch.value.trim(),
                            startPosition = lastMatch.range.first,
                            endPosition = text.length,
                            text = text.substring(lastMatch.range.first)
                        )
                    )
                }
                
                if (chapters.isNotEmpty()) break
            }
        }
        
        // If no chapters found, treat entire text as one chapter
        if (chapters.isEmpty()) {
            chapters.add(
                Chapter(
                    title = "Full Text",
                    startPosition = 0,
                    endPosition = text.length,
                    text = text
                )
            )
        }
        
        return chapters
    }
}
