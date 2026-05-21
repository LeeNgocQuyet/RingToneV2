package com.example.ringtonev2.ui.main

import androidx.annotation.DrawableRes
import com.example.ringtonev2.R

enum class MainTab(val title: String, @DrawableRes val icon: Int) {
    Home("Home", R.drawable.home),
    Download("Download", R.drawable.download),
    Category("Category", R.drawable.category),
    Playlist("Playlist", R.drawable.playlist),
}