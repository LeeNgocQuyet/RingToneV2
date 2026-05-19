package com.example.ringtonev2.ui.categorylist

import com.example.ringtonev2.ui.theme.*

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
@OptIn(ExperimentalMaterial3Api::class)
fun CategoryListScreen(
    onOpenPlayer: (String) -> Unit,
    onBack: () -> Unit,
    categoryId: String,
){
    val viewModel: CategoryListScreenViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

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

        //  Todo id là String rồi còn toString gì nữa ???
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
    LaunchedEffect(categoryId) {
        viewModel.initCategory(categoryId.toInt())
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = viewModel.categoryName,
                        style = AppTypography.titleMedium.copy(
                            fontSize = 18.sp
                        ),
                        color = ContentDefault
                    )
                },
                navigationIcon = {
                    BackNavigationIconButton(onClick = onBack)
                },
                //  Todo
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Black)
        ) {
            when (val state = uiState) {
                CategoryState.Idle -> {}
                CategoryState.Loading -> {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                is CategoryState.Error -> {
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

                is CategoryState.Success -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
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
                            //  Todo ????? đọc document Recomposition
                            val favoriteIds by viewModel.favoriteIds.collectAsState()
                            //  Todo id là String rồi còn toString gì nữa ???
                            val isFavorite = ringtone.id.toString() in favoriteIds

                            RingtoneItemRow(
                                ringtone = ringtone,
                                onPlayClick = {
                                    //  Todo id là String rồi còn toString gì nữa ???
                                    viewModel.togglePlaying(
                                        ringtone.id.toString()
                                    )
                                },
                                //  Todo id là String rồi còn toString gì nữa ???
                                isPlaying =
                                    currentPlayingId == ringtone.id.toString()
                                            && isPlaying,
                                onSetClick = {
                                    onOpenPlayer(ringtone.id.toString())
                                },
                                onFavorite = {
                                    viewModel.toggleFavorite(
                                        ringtone)
                                },
                                isFavorite = isFavorite
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                thickness = 0.5.dp,
                                color = BorderSubtlest
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
}
