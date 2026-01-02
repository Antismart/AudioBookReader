package com.example.audiobookreader.presentation.library.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.audiobookreader.domain.model.Book

@Composable
fun DeleteBookDialog(
    book: Book,
    onConfirm: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var deleteFile by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Book") },
        text = {
            androidx.compose.foundation.layout.Column {
                Text("Are you sure you want to delete \"${book.title}\"?")
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = deleteFile,
                        onCheckedChange = { deleteFile = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Also delete the file from device")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(deleteFile) }) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
