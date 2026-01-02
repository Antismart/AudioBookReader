package com.example.audiobookreader.presentation.library

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.audiobookreader.presentation.library.components.BookCard
import com.example.audiobookreader.presentation.library.components.DeleteBookDialog
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onBookClick: (Long) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val events by viewModel.events.collectAsState()
    val context = LocalContext.current
    
    var bookToDelete by remember { mutableStateOf<com.example.audiobookreader.domain.model.Book?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                // Copy file to app's internal storage
                val inputStream = context.contentResolver.openInputStream(uri)
                val fileName = "book_${System.currentTimeMillis()}.${getFileExtension(uri.toString())}"
                val file = File(context.filesDir, fileName)
                
                inputStream?.use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }
                
                viewModel.addBook(file.absolutePath)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    // Handle events
    LaunchedEffect(events) {
        events?.let { event ->
            when (event) {
                is LibraryEvent.BookAdded -> {
                    // Show success message if needed
                }
                is LibraryEvent.BookDeleted -> {
                    // Show success message if needed
                }
                is LibraryEvent.Error -> {
                    // Show error snackbar
                }
            }
            viewModel.onEventConsumed()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Library") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Launch file picker for supported formats
                    filePickerLauncher.launch("*/*")
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Book")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is LibraryUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                is LibraryUiState.Empty -> {
                    EmptyLibraryMessage(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                is LibraryUiState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.books) { book ->
                            BookCard(
                                book = book,
                                onClick = { onBookClick(book.id) },
                                onDeleteClick = {
                                    bookToDelete = book
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
                
                is LibraryUiState.Error -> {
                    ErrorMessage(
                        message = state.message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
        
        // Delete confirmation dialog
        if (showDeleteDialog && bookToDelete != null) {
            DeleteBookDialog(
                book = bookToDelete!!,
                onConfirm = { deleteFile ->
                    viewModel.deleteBook(bookToDelete!!, deleteFile)
                    showDeleteDialog = false
                    bookToDelete = null
                },
                onDismiss = {
                    showDeleteDialog = false
                    bookToDelete = null
                }
            )
        }
    }
}

@Composable
fun EmptyLibraryMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📚",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No books yet",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap the + button to add your first book",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ErrorMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "⚠️",
            style = MaterialTheme.typography.displayMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Error",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
    }
}

private fun getFileExtension(uriString: String): String {
    return when {
        uriString.contains(".txt", ignoreCase = true) -> "txt"
        uriString.contains(".epub", ignoreCase = true) -> "epub"
        uriString.contains(".pdf", ignoreCase = true) -> "pdf"
        else -> "txt"
    }
}
