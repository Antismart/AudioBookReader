package com.example.audiobookreader.presentation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audiobookreader.core.parser.Chapter
import com.example.audiobookreader.core.player.AudioPlayer
import com.example.audiobookreader.domain.model.Book
import com.example.audiobookreader.domain.model.PlaybackSpeed
import com.example.audiobookreader.domain.usecase.ExtractBookTextUseCase
import com.example.audiobookreader.domain.usecase.GetBookByIdUseCase
import com.example.audiobookreader.domain.usecase.UpdateProgressUseCase
import com.example.audiobookreader.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val audioPlayer: AudioPlayer,
    private val getBookByIdUseCase: GetBookByIdUseCase,
    private val extractBookTextUseCase: ExtractBookTextUseCase,
    private val updateProgressUseCase: UpdateProgressUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<PlayerUiState>(PlayerUiState.Idle)
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()
    
    val playerState: StateFlow<AudioPlayer.PlayerState> = audioPlayer.playbackState
    
    private var progressUpdateJob: Job? = null
    private var currentBook: Book? = null
    
    init {
        // Initialize TTS
        viewModelScope.launch {
            audioPlayer.initialize()
        }
        
        // Observe player state changes
        viewModelScope.launch {
            playerState.collect { state ->
                if (state.isPlaying) {
                    startProgressUpdates()
                } else {
                    stopProgressUpdates()
                }
            }
        }
    }
    
    fun loadBook(bookId: Long) {
        viewModelScope.launch {
            _uiState.value = PlayerUiState.Loading
            
            // Get book details
            when (val bookResult = getBookByIdUseCase(bookId)) {
                is Result.Success -> {
                    currentBook = bookResult.data
                    
                    // Extract text
                    when (val textResult = extractBookTextUseCase(bookResult.data)) {
                        is Result.Success -> {
                            audioPlayer.loadBook(bookResult.data, textResult.data.fullText)
                            _uiState.value = PlayerUiState.Ready(
                                book = bookResult.data,
                                chapters = textResult.data.chapters
                            )
                        }
                        is Result.Error -> {
                            _uiState.value = PlayerUiState.Error(textResult.message)
                        }
                        is Result.Loading -> {
                            // Stay in loading
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.value = PlayerUiState.Error(bookResult.message)
                }
                is Result.Loading -> {
                    // Stay in loading
                }
            }
        }
    }
    
    fun play() {
        audioPlayer.play()
    }
    
    fun pause() {
        audioPlayer.pause()
        saveProgress()
    }
    
    fun stop() {
        audioPlayer.stop()
        saveProgress()
    }
    
    fun seekTo(position: Int) {
        audioPlayer.seekTo(position)
    }
    
    fun setSpeed(speed: PlaybackSpeed) {
        audioPlayer.setSpeed(speed)
    }
    
    private fun startProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = viewModelScope.launch {
            while (true) {
                delay(5000) // Save progress every 5 seconds
                saveProgress()
            }
        }
    }
    
    private fun stopProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = null
    }
    
    private fun saveProgress() {
        val book = currentBook ?: return
        val state = playerState.value
        
        viewModelScope.launch {
            updateProgressUseCase(
                UpdateProgressUseCase.Params(
                    bookId = book.id,
                    position = state.currentPosition.toLong(),
                    page = 0 // We'll calculate this based on chapters later
                )
            )
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        saveProgress()
        stopProgressUpdates()
    }
}

sealed class PlayerUiState {
    object Idle : PlayerUiState()
    object Loading : PlayerUiState()
    data class Ready(
        val book: Book,
        val chapters: List<Chapter>
    ) : PlayerUiState()
    data class Error(val message: String) : PlayerUiState()
}
