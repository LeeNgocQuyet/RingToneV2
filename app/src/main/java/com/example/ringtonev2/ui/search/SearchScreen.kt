package com.example.ringtonev2.ui.search

import com.example.ringtonev2.ui.theme.*

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.ringtonev2.R
import com.example.ringtonev2.components.BackNavigationIconButton
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
    val pagingItems = viewModel.ringtones.collectAsLazyPagingItems()
    val suggestionItems = viewModel.suggestions.collectAsLazyPagingItems()
    val noResult = state.submittedQuery.isNotBlank() &&
            pagingItems.loadState.refresh is LoadState.NotLoading &&
            pagingItems.itemCount == 0
    val shouldShowSuggestions = state.submittedQuery.isBlank() || noResult
    val visibleItems = if (shouldShowSuggestions) suggestionItems else pagingItems

    val context = LocalContext.current
    val currentPlayingId by viewModel.currentPlayingId.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    val player = remember {
        ExoPlayer.Builder(context).build()
    }

    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }
    DisposableEffect(player) {

        val listener = object : Player.Listener {

            override fun onPlaybackStateChanged(playbackState: Int) {

                if (playbackState == Player.STATE_ENDED) {

                    viewModel.onPlaybackCompleted()
                }
            }
        }

        player.addListener(listener)

        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }
    LaunchedEffect(currentPlayingId, isPlaying) {

        val ringtone = visibleItems.itemSnapshotList.items
            .find { it.id == currentPlayingId }

        val url = ringtone?.audioPath ?: return@LaunchedEffect

        if (isPlaying) {

            player.setMediaItem(
                MediaItem.fromUri(url)
            )

            player.prepare()
            player.play()

        } else {

            player.pause()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .imePadding()
            .padding(horizontal = 10.dp)
    ) {
        Spacer(modifier = Modifier.height(42.dp))

        SearchTopBar(
            query = state.query,
            onQueryChange = viewModel::onQueryChange,
            onBack = onBack,
            onClear = viewModel::onClear,
            onSearch = viewModel::submitSearch
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
            if (noResult) {
                item {
                    NoResultSearch()
                }
            }

            item {
                Spacer(modifier = Modifier.height(if (state.query.isBlank()) 18.dp else 4.dp))
                Text(
                    text = if (shouldShowSuggestions) stringResource(R.string.search_recommend)
                    else stringResource(R.string.search_recommend),
                    style = AppTypography.labelLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W700,
                        color = ContentDefault
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (visibleItems.loadState.refresh is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BackgroundBrand)
                    }
                }
            } else if (visibleItems.loadState.refresh is LoadState.Error) {
                val error = visibleItems.loadState.refresh as LoadState.Error
                item {
                    Text(
                        text = error.error.message ?: stringResource(R.string.unknown_error),
                        modifier = Modifier.padding(top = 24.dp),
                        style = AppTypography.bodyMedium.copy(color = ContentError)
                    )
                }
            } else if (visibleItems.itemCount == 0) {
                item {
                    Text(
                        text = stringResource(R.string.no_ringtones_found),
                        modifier = Modifier.padding(top = 24.dp),
                        style = AppTypography.bodyMedium.copy(color = ContentSubtlest)
                    )
                }
            } else {
                items(
                    count = visibleItems.itemCount,
                    key = { index -> visibleItems[index]?.id ?: index }
                ) { index ->
                    val ringtone = visibleItems[index] ?: return@items
                    val isFavorite = ringtone.id in favoriteIds
                    RingtoneItemRow(
                        ringtone = ringtone,
                        isPlaying = isPlaying && currentPlayingId == ringtone.id,
                        onPlayClick = {
                            viewModel.togglePlaying(
                                ringtone.id
                            )
                        },
                        onSetClick = {
                            onOpenPlayer(ringtone.id)
                        },
                        onFavorite = {
                            viewModel.toggleFavorite(ringtone)
                        },
                        isFavorite = isFavorite
                    )
                }

                if (visibleItems.loadState.append is LoadState.Loading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(28.dp),
                                color = BackgroundBrand
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    onClear: () -> Unit,
    onSearch: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackNavigationIconButton(onClick = onBack)

        Spacer(modifier = Modifier.width(10.dp))

        SearchField(
            query = query,
            onQueryChange = onQueryChange,
            onClear = onClear,
            onSearch = onSearch,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onSearch: () -> Unit,
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
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(onSearch = {
            onSearch()
        }),
        textStyle = TextStyle(
            color = ContentDefault,
            fontSize = 14.sp,
            fontWeight = FontWeight.W500
        ),
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(50))
            .background(Accent)
            .focusRequester(focusRequester),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp).height(34.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.search),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search_ringtone),
                            style = AppTypography.bodySmall.copy(
                                fontSize = 13.sp,
                                color = ContentSubtlest
                            )
                        )
                    }
                    innerTextField()
                }
                if (query.isNotBlank()) {
                    Spacer(modifier = Modifier.width(10.dp))

                    IconButton(
                        onClick = onClear,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Accent)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.cancel_01),
                            contentDescription = "Clear",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
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
            text = stringResource(R.string.history),
            style = AppTypography.labelLarge.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.W700,
                color = ContentDefault
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
            .background(Accent)
            .clickable(onClick = onClick)
            .padding(start = 14.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = AppTypography.bodySmall.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.W600,
                color = ContentSubtle
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = "Remove",
            modifier = Modifier
                .size(16.dp)
                .clickable(onClick = onRemove),
            tint = ContentSubtlest
        )
    }
}

@Composable
fun NoResultSearch(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        Image(
            painter = painterResource(R.drawable.bg_empty),
            contentDescription = null,
            modifier = Modifier.size(132.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.no_results),
            modifier = Modifier.padding(horizontal = 32.dp),
            textAlign = TextAlign.Center,
            style = AppTypography.bodyMedium.copy(
                color = ContentDisabled,
                fontSize = 14.sp,
                fontWeight = FontWeight.W500
            )
        )
        Spacer(modifier = Modifier.height(28.dp))
    }
}
