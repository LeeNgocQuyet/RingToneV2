package com.example.ringtonev2.ui.playlist
import com.example.ringtonev2.domain.Ringtone

data class PlaylistState(
    val isLoading: Boolean = false,
    val selectedTab: PlaylistTab = PlaylistTab.Downloads,
    val downloads: List<Ringtone> = emptyList(),
    val favorites: List<Ringtone> = emptyList()
)