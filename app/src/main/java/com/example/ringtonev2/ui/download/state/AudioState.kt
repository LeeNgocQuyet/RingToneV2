package com.example.ringtonev2.ui.download.state

import com.example.ringtonev2.data.remote.dto.TikTokData

sealed class AudioState {
    object Idle : AudioState()
    object Loading : AudioState()
    data class Success(val data: TikTokData) : AudioState()
    data class Error(val message: String) : AudioState()
}