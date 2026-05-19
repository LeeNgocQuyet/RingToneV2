package com.example.ringtonev2.ui.download

import com.example.ringtonev2.ui.theme.*

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ringtonev2.R
import com.example.ringtonev2.ui.theme.AppTypography
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.ringtonev2.components.RingtoneItemRow
import com.example.ringtonev2.data.remote.dto.TikTokData

@Composable
fun DownloadScreen(
    viewModel: DownloadScreenViewModel = hiltViewModel<DownloadScreenViewModel>(),
    onOpenDownload: (String) -> Unit,
    onOpenAudioInfo: (TikTokData) -> Unit,
    onOpenErrorScreen: () -> Unit
) {
    var error by remember { mutableStateOf<String?>(null) }
    var link by remember { mutableStateOf("") }
    val context = LocalContext.current
    val state by viewModel.audioState.collectAsState()
    val isLoading = state is AudioState.Loading
    val downloadHistory by viewModel.downloadHistory.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val currentPlayingId by viewModel.currentPlayingId.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val player = remember {
        ExoPlayer.Builder(context).build()
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
        val ringtone = downloadHistory.find { it.id == currentPlayingId }
        val url = ringtone?.audioPath ?: return@LaunchedEffect

        if (isPlaying) {
            player.setMediaItem(MediaItem.fromUri(url))
            player.prepare()
            player.play()
        } else {
            player.pause()
        }
    }

    LaunchedEffect(state) {
        when (val s = state) {
            is AudioState.Error -> {
                onOpenErrorScreen()
                viewModel.resetAudioState()
            }

            is AudioState.Success -> {
                onOpenAudioInfo(s.data)
                viewModel.resetAudioState()
            }

            else -> {
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(248.dp)
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0x00CFA3FF),
                                Color(0xFFCFA3FF)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(R.drawable.bg_download_screen),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop,
                )
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        stringResource(id = R.string.download_tik_audio),
                        style = AppTypography.titleMedium.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.W600
                        ),
                        color = ContentSecondary,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        stringResource(id = R.string.paste_tik_link),
                        style = AppTypography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500
                        ),
                        color = ContentSubtlest,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(id = R.drawable.ic_link_download),
                                contentDescription = null,
                            )
                        },
                        value = link,
                        onValueChange = { link = it },
                        placeholder = {
                            Text(
                                stringResource(id = R.string.paste_tik_link_here),
                                style = AppTypography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.W500
                                ),
                                color = ContentDisabled,
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = BorderBold,
                            unfocusedContainerColor = BorderBold,
                            focusedIndicatorColor = BorderBold,
                            unfocusedIndicatorColor = BorderBold,
                            focusedTextColor = ContentDefault,
                            unfocusedTextColor = ContentDisabled,
                            cursorColor = White
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (isLoading) {
                                return@Button
                            }
                            if (!link.isBlank())
                                viewModel.getInfoAudio(link)
                            else {
                                error = "Please enter a link"
                                Log.d("DOWNLOAD", "error = $error")
                            }

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BackgroundSecondary,
                            contentColor = Black
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(20.dp)
                                    .border(1.dp, Color(0xFF262626), CircleShape),
                                strokeWidth = 2.dp,
                                color = Color(0xFF262626),
                                trackColor = Color.Gray
                            )
                        } else {
                            Text(
                                text = stringResource(id = R.string.download_audio),
                                style = AppTypography.labelMedium.copy(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.W600
                                ),
                                color = Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(R.drawable.download_icon),
                                contentDescription = "download audio icon"
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (!downloadHistory.isEmpty()){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.download_history),
                    style = AppTypography.labelLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W700,
                        color = ContentDefault
                    )
                )
                //Todo Xoá đi
//                Text(
//                    text = "More  >",
//                    style = AppTypography.labelMedium.copy(
//                        fontSize = 13.sp,
//                        fontWeight = FontWeight.W600,
//                        color = ContentBrand
//                    )
//                )
            }
                }

            Spacer(modifier = Modifier.height(8.dp))
        }

        items(
            items = downloadHistory,
            key = { it.id }
        ) { ringtone ->
            val ringtoneId = ringtone.id

            RingtoneItemRow(
                ringtone = ringtone,
                isPlaying = currentPlayingId == ringtoneId && isPlaying,
                onPlayClick = {
                    viewModel.togglePlaying(ringtoneId)
                },
                onSetClick = {
                    onOpenDownload(ringtoneId)
                },
                onFavorite = {
                    viewModel.toggleFavorite(ringtone)
                },
                isFavorite = ringtoneId in favoriteIds
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 0.5.dp,
                color = BorderBold
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
