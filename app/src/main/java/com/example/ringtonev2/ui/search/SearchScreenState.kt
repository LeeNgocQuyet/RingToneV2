package com.example.ringtonev2.ui.search

data class SearchScreenState(
    val query: String = "",
    val history: List<String> = listOf("Lo-fi", "Chill", "Techno", "Drake", "Anime Opening", "Chill")
)
