package com.example.ringtonev2.ui.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.ringtonev2.R
import com.example.ringtonev2.components.RingtoneItemRow
import com.example.ringtonev2.domain.Category
import com.example.ringtonev2.ui.theme.AppTypography

@Composable
fun HomeScreen(
    onOpenPlayer: (String) -> Unit
) {

    val viewModel: HomeViewModel = hiltViewModel()
    val uiState by viewModel.homeState.collectAsState()

    val listState = rememberLazyListState()
    val pagingItems = viewModel.ringtones.collectAsLazyPagingItems()

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

        val ringtone = pagingItems.itemSnapshotList.items
            .find { it.id.toString() == currentPlayingId }

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
            .background(colorResource(id = R.color.Black))
    ) {
        when (val state = uiState) {
            HomeState.Idle -> {}
            HomeState.Loading -> {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            is HomeState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        color = Color.White
                    )
                }
            }

            is HomeState.Success -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {

                    item {
                        FeaturedBannerSection()
                    }

                    stickyHeader {
                        CategoryTabsSection(
                            categories = state.categories,
                            selectedCategoryId = state.selectedCategoryId,
                            onCategoryClick = {
                                viewModel.selectCategory(it)
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    items(
                        count = pagingItems.itemCount,
                        key = { index ->
                            pagingItems[index]?.id ?: index
                        }
                    ) { index ->

                        val ringtone = pagingItems[index] ?: return@items
                        RingtoneItemRow(
                            ringtone = ringtone,
                            onPlayClick = {
                                viewModel.togglePlaying(
                                    ringtone.id.toString()
                                )
                            },
                            isPlaying =
                                currentPlayingId == ringtone.id.toString()
                                        && isPlaying,
                            onSetClick = {
                                Log.d("HomeScreen", "onSetClick: ${ringtone.id}")
                                Log.d("HomeScreen", "onSetClick: ${ringtone.name}")
                                onOpenPlayer(ringtone.id.toString())
                            },
                            onSwipeRight = {
                                onOpenPlayer(ringtone.id.toString())
                            },
                            onFavorite = {

                            }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            thickness = 0.5.dp,
                            color = colorResource(id = R.color.border_subtlest)
                        )
                    }
                    if (pagingItems.loadState.append is LoadState.Loading) {
                        item {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturedBannerSection() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
    ) {

        Image(
            painter = painterResource(R.drawable.bg_home),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(
                    start = 28.dp,
                    top = 22.dp,
                    bottom = 20.dp
                ),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column {

                Text(
                    text = stringResource(R.string.top_ringtones),
                    style = AppTypography.displayLarge.copy(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.W700,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFFFFF),
                                Color(0xFFD6BEFF)
                            )
                        )
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.home_description),
                    style = AppTypography.labelLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600,
                        color = Color(0xFFD6BEFF)
                    )
                )
            }

            Button(
                onClick = { },
                modifier = Modifier.height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.background_secondary)
                ),
                shape = RoundedCornerShape(100.dp),
                contentPadding = PaddingValues(horizontal = 22.dp)
            ) {

                Image(
                    painter = painterResource(R.drawable.ic_home_play),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = stringResource(R.string.play),
                    style = AppTypography.labelLarge.copy(
                        color = colorResource(R.color.content_onbrand),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600
                    )
                )
            }
        }
    }
}

@Composable
fun CategoryTabsSection(
    categories: List<Category>,
    selectedCategoryId: Int?,
    onCategoryClick: (Int?) -> Unit
) {

    LazyRow(
        modifier = Modifier.background(Color.Black),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        items(categories) { category ->

            val isSelected = category.id == selectedCategoryId

            Surface(
                shape = RoundedCornerShape(50),
                color = if (isSelected) {
                    colorResource(id = R.color.background_brand)
                } else {
                    colorResource(id = R.color.accent)
                },
                onClick = {
                    onCategoryClick(category.id)
                }
            ) {

                Text(
                    text = category.name,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                    style = AppTypography.labelMedium.copy(
                        color = if (isSelected) {
                            colorResource(id = R.color.content_onbrand)
                        } else {
                            colorResource(id = R.color.content_subtlest)
                        }
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewHomeScreen() {

    HomeScreen(
        onOpenPlayer = {}
    )
}