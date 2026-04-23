package com.example.ringtonev2.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    onOpenExtract: () -> Unit,
    onOpenHistory: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Settings")
        Button(onClick = onOpenExtract) {
            Text(text = "Extract Audio")
        }
        Button(onClick = onOpenHistory) {
            Text(text = "Extraction History")
        }
    }
}
