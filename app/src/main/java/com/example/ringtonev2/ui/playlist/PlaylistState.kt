package com.example.ringtonev2.ui.playlist
//Todo
import com.example.ringtonev2.domain.Category
import com.example.ringtonev2.domain.DownloadItem
import com.example.ringtonev2.domain.Ringtone

data class PlaylistState(
    val isLoading: Boolean = false,
    val selectedTab: PlaylistTab = PlaylistTab.Downloads,
    val downloads: List<Ringtone> = emptyList(),
    val favorites: List<Ringtone> = emptyList()
)
sealed class PlaylistTab {
    data object Downloads : PlaylistTab()
    data object Favorites : PlaylistTab()
}