package com.example.audiobookreader.presentation.player.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.audiobookreader.domain.model.PlaybackSpeed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeedSelector(
    currentSpeed: PlaybackSpeed,
    onSpeedSelected: (PlaybackSpeed) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Playback Speed",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.menuAnchor()
            ) {
                Text(currentSpeed.label)
            }
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                PlaybackSpeed.entries.forEach { speed ->
                    DropdownMenuItem(
                        text = { Text(speed.label) },
                        onClick = {
                            onSpeedSelected(speed)
                            expanded = false
                        },
                        leadingIcon = if (speed == currentSpeed) {
                            { Text("✓") }
                        } else null
                    )
                }
            }
        }
    }
}
