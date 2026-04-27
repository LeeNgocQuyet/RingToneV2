package com.example.ringtonev2.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ringtonev2.navigation.Routes
import com.example.ringtonev2.ui.category.CategoryScreen
import com.example.ringtonev2.ui.discover.DiscoverScreen
import com.example.ringtonev2.ui.download.DownloadScreen
import com.example.ringtonev2.ui.home.HomeScreen
import com.example.ringtonev2.ui.playlist.PlayListScreen
import com.example.ringtonev2.ui.search.SearchScreen
import com.example.ringtonev2.ui.settings.SettingsScreen

private enum class MainTab(val title: String, val icon: ImageVector) {
    Home("Home", Icons.Default.Home),
    Download("Download", Icons.Default.Download),
    Category("Category", Icons.Default.Category),
    Playlist("Playlist", Icons.Default.LibraryMusic),

    Setting("Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onOpenPlayer: (String) -> Unit,
    onOpenExtract: () -> Unit,
    onOpenHistory: () -> Unit,
) {
    var tab by rememberSaveable { mutableStateOf(MainTab.Home) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Main Screen") },
                actions = {
                    IconButton(onClick = {
                        tab = MainTab.Setting
                    }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                MainTab.entries
                    .filter { it != MainTab.Setting }
                    .forEach { entry ->
                    NavigationBarItem(
                        selected = tab == entry,
                        onClick = { tab = entry },
                        icon = { Icon(entry.icon, contentDescription = null) },
                        label = { Text(entry.title) },
                    )
                }
            }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (tab) {
                MainTab.Home -> HomeScreen(onOpenPlayer = onOpenPlayer)
                MainTab.Download -> DownloadScreen(onOpenPlayer = onOpenPlayer)
                MainTab.Category -> CategoryScreen(onOpenPlayer = onOpenPlayer)
                MainTab.Playlist -> PlayListScreen(onOpenPlayer = onOpenPlayer)

                MainTab.Setting -> SettingsScreen(onOpenPlayer = onOpenPlayer, onBack = {
                    tab = MainTab.Home
                })
            }
        }
    }
}
