package com.example.audiobookreader.core.player

import android.util.Log
import com.example.audiobookreader.core.tts.TTSEngine
import com.example.audiobookreader.core.tts.TTSEvent
import com.example.audiobookreader.domain.model.Book
import com.example.audiobookreader.domain.model.PlaybackSpeed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioPlayer @Inject constructor(
    private val ttsEngine: TTSEngine
) {
    
    companion object {
        private const val TAG = "AudioPlayer"
        private const val CHUNK_SIZE = 3500 // Stay under 4000 char limit
    }
    
    private val _playbackState = MutableStateFlow(PlayerState())
    val playbackState: StateFlow<PlayerState> = _playbackState.asStateFlow()
    
    private var currentText: String = ""
    private var currentChunks: List<String> = emptyList()
    private var currentChunkIndex: Int = 0
    private var isInitialized = false
    
    data class PlayerState(
        val isPlaying: Boolean = false,
        val currentBook: Book? = null,
        val currentPosition: Int = 0, // Character position in text
        val totalCharacters: Int = 0,
        val playbackSpeed: Float = 1.0f,
        val isPaused: Boolean = false,
        val error: String? = null
    ) {
        val progress: Float
            get() = if (totalCharacters > 0) currentPosition.toFloat() / totalCharacters else 0f
    }
    
    suspend fun initialize(): Boolean {
        if (!isInitialized) {
            isInitialized = ttsEngine.initialize()
            Log.d(TAG, "TTS Engine initialized: $isInitialized")
        }
        return isInitialized
    }
    
    fun loadBook(book: Book, text: String) {
        currentText = text
        currentChunks = splitTextIntoChunks(text)
        currentChunkIndex = 0
        
        _playbackState.value = _playbackState.value.copy(
            currentBook = book,
            totalCharacters = text.length,
            currentPosition = 0,
            error = null
        )
        
        Log.d(TAG, "Loaded book: ${book.title}, ${currentChunks.size} chunks")
    }
    
    fun play() {
        if (!isInitialized) {
            _playbackState.value = _playbackState.value.copy(
                error = "TTS not initialized"
            )
            return
        }
        
        if (currentChunks.isEmpty()) {
            _playbackState.value = _playbackState.value.copy(
                error = "No content to play"
            )
            return
        }
        
        _playbackState.value = _playbackState.value.copy(
            isPlaying = true,
            isPaused = false,
            error = null
        )
        
        playCurrentChunk()
    }
    
    private fun playCurrentChunk() {
        if (currentChunkIndex >= currentChunks.size) {
            // Finished reading
            _playbackState.value = _playbackState.value.copy(
                isPlaying = false,
                currentPosition = currentText.length
            )
            Log.d(TAG, "Finished reading all chunks")
            return
        }
        
        val chunk = currentChunks[currentChunkIndex]
        Log.d(TAG, "Playing chunk $currentChunkIndex of ${currentChunks.size}")
        
        val success = ttsEngine.speak(chunk, "chunk_$currentChunkIndex")
        
        if (success) {
            // Update position (approximate based on chunks read)
            val charsPerChunk = currentText.length / currentChunks.size
            _playbackState.value = _playbackState.value.copy(
                currentPosition = currentChunkIndex * charsPerChunk
            )
            
            currentChunkIndex++
            
            // This is a simplified version - in production you'd use TTS callbacks
            // to know when a chunk finishes before starting the next one
        } else {
            _playbackState.value = _playbackState.value.copy(
                error = "Failed to speak text",
                isPlaying = false
            )
        }
    }
    
    fun pause() {
        ttsEngine.pause()
        _playbackState.value = _playbackState.value.copy(
            isPlaying = false,
            isPaused = true
        )
        Log.d(TAG, "Playback paused")
    }
    
    fun stop() {
        ttsEngine.stop()
        currentChunkIndex = 0
        _playbackState.value = _playbackState.value.copy(
            isPlaying = false,
            isPaused = false,
            currentPosition = 0
        )
        Log.d(TAG, "Playback stopped")
    }
    
    fun setSpeed(speed: PlaybackSpeed) {
        ttsEngine.setSpeechRate(speed.speed)
        _playbackState.value = _playbackState.value.copy(
            playbackSpeed = speed.speed
        )
        Log.d(TAG, "Playback speed set to ${speed.label}")
    }
    
    fun seekTo(position: Int) {
        // Calculate which chunk this position falls into
        val charsPerChunk = if (currentChunks.isNotEmpty()) 
            currentText.length / currentChunks.size else 0
        
        currentChunkIndex = if (charsPerChunk > 0) position / charsPerChunk else 0
        currentChunkIndex = currentChunkIndex.coerceIn(0, currentChunks.size - 1)
        
        _playbackState.value = _playbackState.value.copy(
            currentPosition = position
        )
        
        // If currently playing, restart from new position
        if (_playbackState.value.isPlaying) {
            ttsEngine.stop()
            playCurrentChunk()
        }
        
        Log.d(TAG, "Seeked to position $position (chunk $currentChunkIndex)")
    }
    
    fun isSpeaking(): Boolean {
        return ttsEngine.isSpeaking()
    }
    
    fun shutdown() {
        ttsEngine.shutdown()
        isInitialized = false
        _playbackState.value = PlayerState()
        Log.d(TAG, "Audio player shut down")
    }
    
    private fun splitTextIntoChunks(text: String): List<String> {
        val chunks = mutableListOf<String>()
        var currentIndex = 0
        
        while (currentIndex < text.length) {
            var endIndex = minOf(currentIndex + CHUNK_SIZE, text.length)
            
            // Try to break at sentence boundaries
            if (endIndex < text.length) {
                val lastPeriod = text.lastIndexOf('.', endIndex)
                val lastExclamation = text.lastIndexOf('!', endIndex)
                val lastQuestion = text.lastIndexOf('?', endIndex)
                
                val breakPoint = maxOf(lastPeriod, lastExclamation, lastQuestion)
                if (breakPoint > currentIndex) {
                    endIndex = breakPoint + 1
                }
            }
            
            chunks.add(text.substring(currentIndex, endIndex).trim())
            currentIndex = endIndex
        }
        
        return chunks
    }
}
