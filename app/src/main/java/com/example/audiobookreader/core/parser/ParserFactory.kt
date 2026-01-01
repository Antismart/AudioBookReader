package com.example.audiobookreader.core.parser

import java.io.File

class ParserFactory {
    
    private val parsers: List<TextParser> = listOf(
        TxtParser(),
        EpubParser()
        // Add PdfParser() when ready
    )
    
    fun getParser(file: File): TextParser? {
        val extension = file.extension
        return parsers.find { it.supportsFileType(extension) }
    }
    
    fun getSupportedExtensions(): List<String> {
        return listOf("txt", "epub") // Add "pdf" when ready
    }
}
