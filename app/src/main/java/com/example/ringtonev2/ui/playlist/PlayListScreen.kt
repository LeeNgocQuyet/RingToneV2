package com.example.ringtonev2.ui.playlist

import com.example.ringtonev2.ui.theme.*

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ringtonev2.R
import com.example.ringtonev2.components.RingtoneItemRow
import com.example.ringtonev2.ui.theme.AppTypography
import com.example.ringtonev2.ui.theme.White

@Composable
fun PlaylistScreen(
    onOpenPlayer: (String) -> Unit,
    onOpenDownloadScreen: () -> Unit,
    onOpenCategoryScreen: () -> Unit
) {
    val viewModel: PlaylistScreenViewModel = hiltViewModel()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentPlayingId by viewModel.currentPlayingId.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()

    val isEmpty = when (uiState.selectedTab) {
        PlaylistTab.Downloads -> uiState.downloads.isEmpty()
        PlaylistTab.Favorites -> uiState.favorites.isEmpty()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(horizontal = 16.dp)
    ) {
        PlaylistTabRow(
            selectedTab = uiState.selectedTab,
            onTabSelected = viewModel::selectTab
        )

        Spacer(modifier = Modifier.height(24.dp))

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            isEmpty -> {
                PlaylistEmptyState(
                    selectedTab = uiState.selectedTab,
                    onClick = if (uiState.selectedTab == PlaylistTab.Downloads) {
                        onOpenDownloadScreen
                    } else {
                        onOpenCategoryScreen
                    }
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (uiState.selectedTab) {
                        PlaylistTab.Downloads -> {
                            items(
                                items = uiState.downloads,
                                key = { it.id }
                            ) { download ->

                                val ringtoneId = download.id

                                RingtoneItemRow(
                                    ringtone = download,
                                    onPlayClick = {
                                        viewModel.togglePlaying(ringtoneId)
                                    },
                                    isPlaying = currentPlayingId == ringtoneId && isPlaying,
                                    onSetClick = {
                                        onOpenPlayer(ringtoneId)
                                    },
                                    onFavorite = {
                                        viewModel.toggleFavorite(download)
                                    },
                                    isFavorite = ringtoneId in favoriteIds
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    thickness = 0.5.dp,
                                    color = BorderSubtlest
                                )
                            }
                        }

                        PlaylistTab.Favorites -> {
                            items(
                                items = uiState.favorites,
                                key = { it.id }
                            ) { ringtone ->

                                //Todo
                                val ringtoneId = ringtone.id.toString()

                                RingtoneItemRow(
                                    ringtone = ringtone,
                                    onPlayClick = {
                                        viewModel.togglePlaying(ringtoneId)
                                    },
                                    isPlaying = currentPlayingId == ringtoneId && isPlaying,
                                    onSetClick = {
                                        onOpenPlayer(ringtoneId)
                                    },
                                    onFavorite = {
                                        viewModel.toggleFavorite(ringtone)
                                    },
                                    isFavorite = ringtoneId in favoriteIds
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    thickness = 0.5.dp,
                                    color = BorderSubtlest
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlaylistTabRow(
    selectedTab: PlaylistTab,
    onTabSelected: (PlaylistTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlaylistTabButton(
            text = stringResource(R.string.downloads_tab),
            selected = selectedTab == PlaylistTab.Downloads,
            onClick = {
                onTabSelected(PlaylistTab.Downloads)
            }
        )

        PlaylistTabButton(
            text = stringResource(R.string.favorites_tab),
            selected = selectedTab == PlaylistTab.Favorites,
            onClick = {
                onTabSelected(PlaylistTab.Favorites)
            }
        )
    }
}

@Composable
private fun PlaylistTabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) {
        ContentBrand
    } else {
        White.copy(0.12f)
    }

    val textColor = if (selected) {
        ContentOnBrand
    } else {
        ContentSubtlest
    }

    Surface(
        modifier = Modifier
            .height(32.dp)
            .clip(RoundedCornerShape(50))
            .clickable(
                onClick = onClick
            ),
        color = backgroundColor,
        shape = RoundedCornerShape(50)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = AppTypography.labelMedium.copy(
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W600
                )
            )
        }
    }
}

@Composable
fun PlaylistEmptyState(
    selectedTab: PlaylistTab,
    onClick: () -> Unit
) {
    val buttonEmpty = when (selectedTab) {
        PlaylistTab.Downloads -> stringResource(R.string.download_tab_button)
        PlaylistTab.Favorites -> stringResource(R.string.favorite_tab_button)
    }

    val description = when (selectedTab) {
        PlaylistTab.Downloads -> stringResource(R.string.download_tab_description)
        PlaylistTab.Favorites -> stringResource(R.string.favorite_tab_description)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 96.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.bg_empty),
            contentDescription = null,
            modifier = Modifier.size(132.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.empty_title),
            style = AppTypography.headlineMedium.copy(
                color = ContentDefault,
                fontSize = 24.sp,
                fontWeight = FontWeight.W600
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            modifier = Modifier.padding(horizontal = 32.dp),
            textAlign = TextAlign.Center,
            style = AppTypography.bodyLarge.copy(
                color = ContentSubtlest,
                fontSize = 16.sp,
                fontWeight = FontWeight.W500
            )
        )


            Spacer(modifier = Modifier.height(28.dp))

            Box(
                modifier = Modifier
                    .height(48.dp)
                    .width(232.dp)
                    .clip(RoundedCornerShape(50))
                    .background(BackgroundSecondary)
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = buttonEmpty,
                    style = AppTypography.labelLarge.copy(
                        color = ContentOnBrand,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600
                    )
                )

        }
    }
}