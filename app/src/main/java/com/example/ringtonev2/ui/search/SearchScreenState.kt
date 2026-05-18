package com.example.ringtonev2.ui.search

import androidx.compose.runtime.remember
import androidx.media3.exoplayer.ExoPlayer
import com.example.ringtonev2.domain.Ringtone

data class SearchScreenState(
    val query: String = "",
    val history: List<String> = emptyList(),
    val suggestions: List<Ringtone> = emptyList(),
    val results: List<Ringtone> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val visibleRingtones: List<Ringtone>
        get() = if (query.isBlank()) suggestions else results
}