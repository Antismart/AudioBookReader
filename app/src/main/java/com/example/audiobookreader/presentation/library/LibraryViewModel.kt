package com.example.audiobookreader.presentation.library

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audiobookreader.domain.model.Book
import com.example.audiobookreader.domain.usecase.AddBookUseCase
import com.example.audiobookreader.domain.usecase.DeleteBookUseCase
import com.example.audiobookreader.domain.usecase.GetBooksUseCase
import com.example.audiobookreader.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getBooksUseCase: GetBooksUseCase,
    private val addBookUseCase: AddBookUseCase,
    private val deleteBookUseCase: DeleteBookUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<LibraryUiState>(LibraryUiState.Loading)
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()
    
    private val _events = MutableStateFlow<LibraryEvent?>(null)
    val events: StateFlow<LibraryEvent?> = _events.asStateFlow()
    
    init {
        loadBooks()
    }
    
    private fun loadBooks() {
        viewModelScope.launch {
            getBooksUseCase()
                .catch { e ->
                    _uiState.value = LibraryUiState.Error(e.message ?: "Unknown error")
                }
                .collect { books ->
                    _uiState.value = if (books.isEmpty()) {
                        LibraryUiState.Empty
                    } else {
                        LibraryUiState.Success(books)
                    }
                }
        }
    }
    
    fun addBook(filePath: String) {
        viewModelScope.launch {
            _uiState.value = LibraryUiState.Loading
            
            when (val result = addBookUseCase(filePath)) {
                is Result.Success -> {
                    _events.value = LibraryEvent.BookAdded(result.data)
                    // Books will auto-update via Flow
                }
                is Result.Error -> {
                    _events.value = LibraryEvent.Error(result.message)
                    loadBooks() // Reload to restore previous state
                }
                is Result.Loading -> {
                    // Already in loading state
                }
            }
        }
    }
    
    fun deleteBook(book: Book, deleteFile: Boolean = false) {
        viewModelScope.launch {
            when (val result = deleteBookUseCase(book, deleteFile)) {
                is Result.Success -> {
                    _events.value = LibraryEvent.BookDeleted
                }
                is Result.Error -> {
                    _events.value = LibraryEvent.Error(result.message)
                }
                is Result.Loading -> {
                    // Handle if needed
                }
            }
        }
    }
    
    fun onEventConsumed() {
        _events.value = null
    }
}

sealed class LibraryUiState {
    object Loading : LibraryUiState()
    object Empty : LibraryUiState()
    data class Success(val books: List<Book>) : LibraryUiState()
    data class Error(val message: String) : LibraryUiState()
}

sealed class LibraryEvent {
    data class BookAdded(val book: Book) : LibraryEvent()
    object BookDeleted : LibraryEvent()
    data class Error(val message: String) : LibraryEvent()
}
