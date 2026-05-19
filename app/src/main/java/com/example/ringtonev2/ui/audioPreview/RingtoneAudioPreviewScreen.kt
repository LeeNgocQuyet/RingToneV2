package com.example.ringtonev2.ui.audioPreview

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay

import com.example.ringtonev2.R
import com.example.ringtonev2.components.AssignUsageDialog
import com.example.ringtonev2.components.BackNavigationIconButton
import com.example.ringtonev2.components.DeleteRingtoneDialog
import com.example.ringtonev2.components.SetRingtoneSuccessDialog
import com.example.ringtonev2.ui.theme.AppTypography


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RingtoneAudioPreviewScreen(
    ringtoneId: String,
    onBack: () -> Unit,
    onDeleted: () -> Unit
) {
    val viewModel: RingtoneAudioPreviewScreenViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(ringtoneId) {
        viewModel.load(ringtoneId)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is RingtoneAudioPreviewState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = colorResource(R.color.background_brand))
                }
            }

            is RingtoneAudioPreviewState.Success -> {
                AudioPreviewContent(
                    state = state,
                    viewModel = viewModel,
                    onBack = onBack,
                    onDeleted = onDeleted
                )
            }

            is RingtoneAudioPreviewState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = Color.Red)
                }
            }

            else -> Unit
        }
    }
    val successState = uiState as? RingtoneAudioPreviewState.Success
    val data = successState?.data

    if (data?.isDownloading ?: false) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                CircularProgressIndicator(
                    progress = data.downloadProgress / 100f,
                    color = colorResource(R.color.background_brand)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Downloading ${data.downloadProgress}%...",
                    style = AppTypography.bodyMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500
                    ),
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPreviewContent(
    state: RingtoneAudioPreviewState.Success,
    viewModel: RingtoneAudioPreviewScreenViewModel,
    onBack: () -> Unit,
    onDeleted: () -> Unit
) {
    val context = LocalContext.current
    val data = state.data
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val isFavorite = data.ringtoneId in favoriteIds
    val duration = data.duration
    var showAssignDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }
    val progress = if (duration > 0) data.currentPosition.toFloat() / duration else 0f

    if (showAssignDialog) {
        AssignUsageDialog(
            onDismiss = { showAssignDialog = false },
            onRingtoneClick = {
                if (canWriteSettings(context)) {
                    viewModel.setAsSystemSound(context, RingtoneType.RINGTONE) {
                        showAssignDialog = false
                        showSuccessDialog = true
                    }
                } else {
                    requestWriteSettingsPermission(context)
                }
            },
            onNotifiClick = {
                if (canWriteSettings(context)) {
                    viewModel.setAsSystemSound(context, RingtoneType.NOTIFICATION) {
                        showAssignDialog = false
                        showSuccessDialog = true
                    }
                } else {
                    requestWriteSettingsPermission(context)
                }
            },
            onAlarmClick = {
                if (canWriteSettings(context)) {
                    viewModel.setAsSystemSound(context, RingtoneType.ALARM) {
                        showAssignDialog = false
                        showSuccessDialog = true
                    }
                } else {
                    requestWriteSettingsPermission(context)
                }
            }
        )
    }
    if (showSuccessDialog) {
        SetRingtoneSuccessDialog(onDismiss = { showSuccessDialog = false })
    }
    if (showDeleteDialog) {
        DeleteRingtoneDialog(
            onDismiss = {
                showDeleteDialog = false
            },
            onDelete = {
                showDeleteDialog = false
                viewModel.deleteRingtone {
                    onDeleted()
                }
            }
        )
    }

    LaunchedEffect(data.audioPath) {
        if (data.audioPath.isNotEmpty()) {
            val uri = Uri.parse(data.audioPath)
            val mediaItem = MediaItem.fromUri(uri)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
        }
    }

    LaunchedEffect(exoPlayer) {
        while (true) {
            viewModel.seekTo(exoPlayer.currentPosition)
            delay(300)
        }
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
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
                        color = colorResource(R.color.content_default)
                    )
                },
                navigationIcon = {
                    BackNavigationIconButton(onClick = onBack)
                },
                actions = {
                    if (data.isDownloaded) {
                        IconButton(onClick = {
                            showDeleteDialog = true
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_delete),
                                contentDescription = "Delete ringtone",
                                tint = Color.Red
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(6.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(R.drawable.bg_removal),
                    contentDescription = null,
                    modifier = Modifier.size(280.dp)
                )
            }
            Spacer(Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = data.title, color = colorResource(R.color.content_default),
                        style = AppTypography.bodyMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W600
                        )
                    )
                }
                IconButton(onClick = {
                    viewModel.toggleFavorite(data.ringtoneId)
                }) {
                    Icon(
                        painter = painterResource(if (isFavorite) R.drawable.ic_favorite_fullfill
                        else R.drawable.ic_favorite),
                        contentDescription = null,
                        tint = if (isFavorite) Color.Red else Color.White
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Slider(
                value = progress,
                colors = androidx.compose.material3.SliderDefaults.colors(
                    thumbColor = colorResource(R.color.background_brand),
                    activeTrackColor = colorResource(R.color.background_brand),
                    inactiveTrackColor = colorResource(R.color.background_neutral)
                ),
                onValueChange = {
                    val newPosition = (it * duration).toLong()
                    exoPlayer.seekTo(newPosition)
                    viewModel.seekTo(newPosition)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    formatDurationMilisecond(data.currentPosition),
                    color = colorResource(R.color.content_subtlest),
                    style = AppTypography.bodySmall
                )
                Text(
                    formatDurationMilisecond(duration),
                    color = colorResource(R.color.content_subtlest),
                    style = AppTypography.bodySmall
                )
            }

            Spacer(Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    exoPlayer.seekTo(
                        (exoPlayer.currentPosition - 10000).coerceAtLeast(
                            0
                        )
                    )
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
                    val icon = if (data.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                    IconButton(onClick = { if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play() }) {
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                }
                IconButton(onClick = {
                    exoPlayer.seekTo(
                        (exoPlayer.currentPosition + 10000).coerceAtMost(
                            duration
                        )
                    )
                }) {
                    Icon(
                        painter = painterResource(R.drawable.go_forward_10sec),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    if (data.isDownloaded) {
                        showAssignDialog = true // Đã tải -> Hiện Dialog cài đặt
                    } else {
                        viewModel.downloadRingtone(context) // Chưa tải -> Bắt đầu tải
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                enabled = !data.isDownloading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.background_secondary)
                )
            ) {
                Text(
                    text = if (data.isDownloaded) stringResource(R.string.set_ring_tone) else "Download",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

fun formatDurationMilisecond(miliseconds: Long?): String {
    if (miliseconds == null || miliseconds <= 0L) return "00:00"

    val totalSeconds = miliseconds / 1000
    val minutes = totalSeconds / 60
    val secs = totalSeconds % 60

    return "%02d:%02d".format(minutes, secs)
}

