package com.example.ringtonev2.ui.audioInfo

sealed interface AudioDownloadUiState {
    data object Idle : AudioDownloadUiState
    data class Downloading(val progress: Float) : AudioDownloadUiState
    data class Success(val savedPath: String) : AudioDownloadUiState
    data object Error : AudioDownloadUiState
}
