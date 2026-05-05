package com.example.ringtonev2.ui.audioInfo

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ringtonev2.R
import com.example.ringtonev2.data.remote.dto.TikTokData
import com.example.ringtonev2.ui.theme.AppTypography
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.ringtonev2.ui.download.DownloadScreenViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioInfoScreen(
    viewModel: AudioInfoScreenViewModel = hiltViewModel<AudioInfoScreenViewModel>(),
    data: TikTokData,
    onBack: () -> Unit,
    //onDownload: () -> Unit,
) {

    LaunchedEffect(data) {
        viewModel.init(data)
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.audio_info_title),
                        style = AppTypography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.W700
                        ),
                        color = colorResource(R.color.content_default),
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.player.pause()
                            viewModel.player.clearVideoSurface()
                                onBack()},
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(colorResource(R.color.content_subtlest))
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_left_02),
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        bottomBar = {

        },
        containerColor = Color.Black,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
                    .padding(horizontal = 20.dp)
                    .aspectRatio(0.75f)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ){
                VideoPlayer(
                    player = viewModel.player,
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            data.author?.nickname?.let {
                Text(
                    text = it,
                    style = AppTypography.headlineMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.W600
                    ),
                    color = colorResource(R.color.content_default),
                )
            }
            data.title?.let {
                Text(
                    text = it,
                    style = AppTypography.bodyLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W500
                    ),
                    color = colorResource(R.color.content_subtlest),
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDuration(data.musicInfo?.duration),
                    style = AppTypography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W500
                    ),
                    color = colorResource(R.color.content_subtlest),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = formatSize(data.size),
                    style = AppTypography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W500
                    ),
                    color = colorResource(R.color.content_subtlest),
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Button(
                    onClick = {  },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.background_secondary),
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = stringResource(R.string.download_screen),
                        style = AppTypography.labelMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W600
                        ),
                        color = colorResource(R.color.Black)
                    )
                }
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.player.pause()
            viewModel.player.stop()
            viewModel.player.clearVideoSurface()
        }
    }
}


private fun formatDuration(seconds: Long?): String {
    if (seconds == null || seconds <= 0L) return "—"
    val s = seconds.toInt().coerceAtLeast(0)
    val m = s / 60
    val rem = s % 60
    return "%d:%02d".format(m, rem)
}

fun formatSize(size: Long?): String {
    if (size == null) return "—"
    val mb = size.toDouble() / (1024 * 1024)
    return String.format("%.2f MB", mb)
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    player: ExoPlayer,
    viewModel: AudioInfoScreenViewModel,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {

        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    this.player = player
                    useController = false
                }
            },
            update = {
                it.player = player
            },
            onRelease = {
                it.player?.clearVideoSurface()
                it.player = null
            },
            modifier = Modifier.matchParentSize()
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
                .clickable {
                    viewModel.onVideoTap()
                },
            contentAlignment = Alignment.Center
        ) {

            if (viewModel.isPlaying.value) {
                IconButton(
                    onClick = { viewModel.togglePlay() },
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            CircleShape
                        )
                ) {
                    Icon(
                        painter = painterResource(
                                R.drawable.ic_play
                        ),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}

fun ExoPlayer.safeRelease() {
    stop()
    clearMediaItems()
    clearVideoSurface()
    release()
}