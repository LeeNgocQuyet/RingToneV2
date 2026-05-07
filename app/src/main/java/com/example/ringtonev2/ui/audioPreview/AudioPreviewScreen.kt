package com.example.ringtonev2.ui.audioPreview

import java.io.File
import android.net.Uri
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.delay

import com.example.ringtonev2.R
import com.example.ringtonev2.data.remote.dto.TikTokData
import com.example.ringtonev2.ui.theme.AppTypography


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPreviewScreen(
    ringtoneId: String,
    onBack: () -> Unit
) {
    val viewModel: AudioPreviewScreenViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val duration = uiState.duration *1000L

    val progress =
        if (duration > 0)
            uiState.currentPosition.toFloat() / duration
        else 0f
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    LaunchedEffect(ringtoneId) {
        viewModel.load(ringtoneId)
    }
    LaunchedEffect(uiState.audioPath) {

        if (uiState.audioPath.isNotEmpty()) {

            val mediaItem = MediaItem.fromUri(
                Uri.fromFile(
                    File(uiState.audioPath)
                )
            )

            exoPlayer.setMediaItem(mediaItem)

            exoPlayer.prepare()
        }
    }
    LaunchedEffect(exoPlayer) {
        while (true) {
            viewModel.seekTo(
                exoPlayer.currentPosition
            )
            delay(300)
        }
    }
    DisposableEffect(exoPlayer) {
        val listener =
            object : Player.Listener {
                override fun onIsPlayingChanged(
                    isPlaying: Boolean
                ) {
                    viewModel.setPlaying(isPlaying)
                }
            }

        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.audio_preview),
                        style = AppTypography.titleLarge.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.W700
                        ),
                        color = colorResource(R.color.content_default),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_left_02),
                            contentDescription = null,
                            tint = Color.White
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
                .padding(horizontal = 16.dp)
        ) {

            Spacer(Modifier.height(20.dp))

            // DISC IMAGE
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.bg_removal),
                    contentDescription = null,
                    modifier = Modifier.size(280.dp)
                )
            }

            Spacer(Modifier.height(32.dp))

            // TITLE + HEART
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = uiState.title,
                        style = AppTypography.bodyMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W600
                        ),
                        color = colorResource(R.color.content_default)
                    )
                }

                IconButton(onClick = { }) {

                    Icon(
                        painter = painterResource(R.drawable.ic_favourite),
                        contentDescription = null,
                        tint = Color.Red
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // PROGRESS BAR
            Slider(
                value = progress,
                colors = androidx.compose.material3.SliderDefaults.colors(
                    thumbColor = colorResource(R.color.background_brand),
                    activeTrackColor = colorResource(R.color.background_brand),
                    inactiveTrackColor = colorResource(R.color.background_neutral),

                    disabledThumbColor = colorResource(R.color.background_brand),
                    disabledActiveTrackColor = colorResource(R.color.background_brand),
                    disabledInactiveTrackColor = colorResource(R.color.background_neutral),
                ),
                enabled = true,
                onValueChange = {
                    val newPosition = (it * duration).toLong()
                    exoPlayer.seekTo(newPosition)
                    viewModel.seekTo(newPosition)
                },
                modifier = Modifier.fillMaxWidth()
            )

            // TIME
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    formatDuration(uiState.currentPosition),
                    style = AppTypography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 16.sp,
                    ),
                    color = colorResource(R.color.content_subtlest)
                )
                Text(
                    formatDuration(
                        duration
                    ),
                    style = AppTypography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 16.sp,
                    ),
                    color = colorResource(R.color.content_subtlest)
                )
            }

            Spacer(Modifier.height(30.dp))

            // 🔥 CONTROLS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(onClick = {
                    val newPosition =
                        (exoPlayer.currentPosition - 10_000L)
                            .coerceAtLeast(0L)

                    exoPlayer.seekTo(newPosition)
                }) {
                    Icon(
                        painter = painterResource(R.drawable.go_backward_10sec),
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(colorResource(R.color.background_brand)),
                    contentAlignment = Alignment.Center
                ) {
                    val icon =
                        if (uiState.isPlaying)
                            R.drawable.ic_pause
                        else
                            R.drawable.ic_play
                    IconButton(onClick = {
                        if (exoPlayer.isPlaying) {
                            exoPlayer.pause()
                        } else {
                            exoPlayer.play()
                        }
                    })
                    {
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                }

                IconButton(onClick = {
                    val newPosition =
                        (exoPlayer.currentPosition + 10_000L)
                            .coerceAtMost(duration)

                    exoPlayer.seekTo(newPosition)
                }) {
                    Icon(
                        painter = painterResource(R.drawable.go_forward_10sec),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // 🔥 BUTTON
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.background_secondary),
                )
            ) {
                Text(
                    stringResource(R.string.set_ring_tone),
                    color = colorResource(R.color.content_onsecondary),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

fun formatDuration(milliseconds: Long?): String {
    if (milliseconds  == null || milliseconds  <= 0L) return "00:00"

    val totalmilliseconds  = milliseconds / 1000
    val minutes = totalmilliseconds  / 60
    val secs = totalmilliseconds  % 60

    return "%02d:%02d".format(minutes, secs)
}