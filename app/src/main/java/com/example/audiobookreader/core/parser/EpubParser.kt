package com.example.audiobookreader.core.parser

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.documentnode.epub4j.domain.Book
import io.documentnode.epub4j.domain.TOCReference
import io.documentnode.epub4j.epub.EpubReader
import java.io.File
import java.io.FileInputStream

class EpubParser : TextParser {

    companion object {
        private const val TAG = "EpubParser"
    }

    override suspend fun extractText(file: File): ParserResult = withContext(Dispatchers.IO) {
        try {
            if (!file.exists()) {
                return@withContext ParserResult.Error("File does not exist")
            }

            val epubReader = EpubReader()
            val book: Book = FileInputStream(file).use { inputStream ->
                epubReader.readEpub(inputStream)
            }

            // Extract metadata
            val metadata = BookMetadata(
                title = book.metadata.firstTitle ?: file.nameWithoutExtension,
                author = book.metadata.authors.firstOrNull()?.let { "${it.firstname} ${it.lastname}" } ?: "Unknown",
                publisher = book.metadata.publishers.firstOrNull() ?: "",
                language = book.metadata.language ?: "en"
            )

            // Extract chapters in the correct order from the book's spine
            val chapters = mutableListOf<Chapter>()
            val fullTextBuilder = StringBuilder()
            var currentPosition = 0

            val tocMap = book.tableOfContents.tocReferences.flatMap { flatten(it) }.associate { it.resource.href to it.title }

            book.spine.spineReferences.forEach { spineReference ->
                val resource = spineReference.resource
                try {
                    val chapterText = extractTextFromHtml(String(resource.data, Charsets.UTF_8))
                    if (chapterText.isNotBlank()) {
                        val title = tocMap[resource.href] ?: resource.title ?: "Chapter ${chapters.size + 1}"
                        
                        chapters.add(
                            Chapter(
                                title = title,
                                startPosition = currentPosition,
                                endPosition = currentPosition + chapterText.length,
                                text = chapterText
                            )
                        )

                        fullTextBuilder.append(chapterText)
                        fullTextBuilder.append("\n\n")
                        currentPosition += chapterText.length + 2
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing chapter: ${resource.href}", e)
                }
            }

            val fullText = fullTextBuilder.toString()

            if (fullText.isEmpty()) {
                return@withContext ParserResult.Error("No text content found in EPUB")
            }

            ParserResult.Success(
                text = fullText,
                chapters = chapters,
                metadata = metadata
            )

        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse EPUB", e)
            ParserResult.Error("Failed to parse EPUB file: ${e.message}", e)
        }
    }

    override fun supportsFileType(extension: String): Boolean {
        return extension.lowercase() == "epub"
    }

    private fun flatten(tocReference: TOCReference): List<TOCReference> {
        return listOf(tocReference) + tocReference.children.flatMap { flatten(it) }
    }

    private fun extractTextFromHtml(html: String): String {
        // A more robust HTML parser might be needed for complex content
        return html
            .replace(Regex("<script[^>]*>.*?</script>", RegexOption.DOT_MATCHES_ALL), "")
            .replace(Regex("<style[^>]*>.*?</style>", RegexOption.DOT_MATCHES_ALL), "")
            .replace(Regex("<[^>]+>"), " ")
            .replace("&nbsp;", " ", ignoreCase = true)
            .replace("&amp;", "&", ignoreCase = true)
            .replace("&lt;", "<", ignoreCase = true)
            .replace("&gt;", ">", ignoreCase = true)
            .replace("&quot;", "\"", ignoreCase = true)
            .replace(Regex("\\s+"), " ")
            .trim()
    }
}
