package com.example.ringtonev2.ui.playlist

sealed class PlaylistTab {
    data object Downloads : PlaylistTab()
    data object Favorites : PlaylistTab()
}