package com.example.ringtonev2.ui.audioInfo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import android.util.Log
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import com.example.ringtonev2.R
import com.example.ringtonev2.data.remote.dto.TikTokData
import com.example.ringtonev2.data.remote.dto.resolveAudioDownloadUrl
import com.example.ringtonev2.ui.theme.AppTypography
import java.util.Locale

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioInfoScreen(
    data: TikTokData,
    onBack: () -> Unit,
    onStartDownload: (TikTokData) -> Unit,
) {
    val viewModel: AudioInfoScreenViewModel = hiltViewModel()
    val context = LocalContext.current

    val dataSourceFactory = DefaultHttpDataSource.Factory()
        .setUserAgent("Mozilla/5.0 (Linux; Android 10)")
        .setAllowCrossProtocolRedirects(true)
        .setConnectTimeoutMs(15000)
        .setReadTimeoutMs(15000)

    val player = remember {
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .build().apply {
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(playing: Boolean) {
                        viewModel.setPlaying(playing)
                    }
                    override fun onPlayerError(error: PlaybackException) {
                        Log.e("AudioInfoScreen", "Player Error: ${error.errorCodeName}", error)
                    }
                })
            }
    }
    Log.d("AudioInfoScreen", "Player: $player")

    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }

    LaunchedEffect(data) {
        //val url = data.hdPlay ?: data.play
        val url = data.resolveAudioDownloadUrl()
        Log.d("AudioInfoScreen", "URL: $url")
        url?.let {
            player.setMediaItem(MediaItem.fromUri(it))
            player.prepare()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.audio_info_title),
                        style = AppTypography.titleMedium.copy(
                            fontSize = 18.sp
                        ),
                        color = colorResource(R.color.content_default)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1F1F1F))
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_left_02),
                            contentDescription = null
                        )
                    }
                },
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
                .padding(16.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.75f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {

                PlayerSurface(
                    player = player,
                    surfaceType = SURFACE_TYPE_TEXTURE_VIEW,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(9f / 16f)
                )


                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable {
                            viewModel.togglePlay(
                                player.isPlaying,
                                onPlay = { player.play() },
                                onPause = { player.pause() }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (!viewModel.isPlaying.value) {
                        IconButton(
                            onClick = {
                                viewModel.togglePlay(
                                    player.isPlaying,
                                    onPlay = { player.play() },
                                    onPause = { player.pause() }
                                )
                            },
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    Color.Black.copy(alpha = 0.5f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_play),
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }
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
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { onStartDownload(data) },
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
    return String.format(Locale.getDefault(), "%.2f MB", mb)
}