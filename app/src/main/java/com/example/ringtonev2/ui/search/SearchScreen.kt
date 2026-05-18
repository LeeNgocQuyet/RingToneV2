package com.example.ringtonev2.ui.search

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.exoplayer.ExoPlayer
import androidx.room.util.query
import com.example.ringtonev2.R
import com.example.ringtonev2.components.RingtoneItemRow
import com.example.ringtonev2.ui.theme.AppTypography

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onOpenPlayer: (String) -> Unit
) {
    val viewModel: SearchScreenViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.Black))
            .imePadding()
            .padding(horizontal = 10.dp)
    ) {
        Spacer(modifier = Modifier.height(42.dp))

        SearchTopBar(
            query = state.query,
            onQueryChange = viewModel::onQueryChange,
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            if (state.query.isBlank() && state.history.isNotEmpty()) {
                item {
                    SearchHistorySection(
                        items = state.history,
                        onHistoryClick = viewModel::useHistoryItem,
                        onRemoveHistory = viewModel::removeHistoryItem
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(if (state.query.isBlank()) 18.dp else 4.dp))
                Text(
                    text = if (state.query.isBlank()) "You might like these" else "Search results",
                    style = AppTypography.labelLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W700,
                        color = colorResource(id = R.color.content_default)
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colorResource(id = R.color.background_brand))
                    }
                }
            } else if (state.errorMessage != null) {
                item {
                    Text(
                        text = state.errorMessage ?: "",
                        modifier = Modifier.padding(top = 24.dp),
                        style = AppTypography.bodyMedium.copy(color = colorResource(id = R.color.content_error))
                    )
                }
            } else if (state.visibleRingtones.isEmpty()) {
                item {
                    Text(
                        text = "No ringtones found",
                        modifier = Modifier.padding(top = 24.dp),
                        style = AppTypography.bodyMedium.copy(color = colorResource(id = R.color.content_subtlest))
                    )
                }
            } else {
                items(
                    items = state.visibleRingtones,
                    key = { it.id }
                ) { ringtone ->
                    val isFavorite = ringtone.id in favoriteIds

                    RingtoneItemRow(
                        ringtone = ringtone,
                        isPlaying = false,
                        onPlayClick = {
                            Log.d("SearchScreen", "preview play requested: ${ringtone.id}")
                            onOpenPlayer(ringtone.id)
                        },
                        onSetClick = {
                            onOpenPlayer(ringtone.id)
                        },
                        onFavorite = {
                            viewModel.toggleFavorite(ringtone)
                        },
                        isFavorite = isFavorite
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        thickness = 0.5.dp,
                        color = colorResource(id = R.color.border_bold)
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(colorResource(id = R.color.accent))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        SearchField(
            query = query,
            onQueryChange = onQueryChange,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        textStyle = TextStyle(
            color = colorResource(id = R.color.content_default),
            fontSize = 14.sp,
            fontWeight = FontWeight.W500
        ),
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(50))
            .background(colorResource(id = R.color.accent))
            .focusRequester(focusRequester),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            text = "Search ringtones...",
                            style = AppTypography.bodySmall.copy(
                                fontSize = 13.sp,
                                color = colorResource(id = R.color.content_subtlest)
                            )
                        )
                    }
                    innerTextField()
                }
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SearchHistorySection(
    items: List<String>,
    onHistoryClick: (String) -> Unit,
    onRemoveHistory: (String) -> Unit
) {
    Column {
        Text(
            text = "History",
            style = AppTypography.labelLarge.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.W700,
                color = colorResource(id = R.color.content_default)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items.forEach { item ->
                HistoryChip(
                    text = item,
                    onClick = { onHistoryClick(item) },
                    onRemove = { onRemoveHistory(item) }
                )
            }
        }
    }
}

@Composable
private fun HistoryChip(
    text: String,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(30.dp)
            .clip(RoundedCornerShape(50))
            .background(colorResource(id = R.color.accent))
            .clickable(onClick = onClick)
            .padding(start = 14.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = AppTypography.bodySmall.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.W600,
                color = colorResource(id = R.color.content_subtle)
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = "Remove",
            modifier = Modifier
                .size(16.dp)
                .clickable(onClick = onRemove),
            tint = colorResource(id = R.color.content_subtlest)
        )
    }
}