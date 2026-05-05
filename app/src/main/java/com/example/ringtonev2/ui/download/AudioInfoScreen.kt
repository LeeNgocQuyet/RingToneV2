package com.example.ringtonev2.ui.download

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ringtonev2.R
import com.example.ringtonev2.data.remote.dto.TikTokData
import com.example.ringtonev2.ui.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioInfoScreen(
    data: TikTokData,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.audio_info_title),
                        style = AppTypography.titleMedium.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.W600
                        ),
                        color = colorResource(R.color.content_brand),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_left_01_sharp),
                            contentDescription = stringResource(R.string.cd_back),
                            tint = Color.White,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        containerColor = Color.Black,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(
                label = stringResource(R.string.audio_info_video_title),
                value = data.title.orEmpty().ifBlank { "—" },
            )
            InfoRow(
                label = stringResource(R.string.audio_info_author),
                value = listOfNotNull(data.author?.nickname, data.author?.uniqueId)
                    .firstOrNull { it.isNotBlank() } ?: "—",
            )
            InfoRow(
                label = stringResource(R.string.audio_info_music_title),
                value = data.musicInfo?.title.orEmpty().ifBlank { "—" },
            )
            InfoRow(
                label = stringResource(R.string.audio_info_duration_sec),
                value = formatDuration(data.duration ?: data.musicInfo?.duration),
            )
            InfoRow(
                label = stringResource(R.string.audio_info_audio_url),
                value = data.music.orEmpty().ifBlank { "—" },
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = AppTypography.labelMedium.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.W600
            ),
            color = colorResource(R.color.content_subtlest),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = AppTypography.bodyMedium.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.W500
            ),
            color = colorResource(R.color.content_secondary),
        )
    }
}

private fun formatDuration(seconds: Long?): String {
    if (seconds == null || seconds <= 0L) return "—"
    val s = seconds.toInt().coerceAtLeast(0)
    val m = s / 60
    val rem = s % 60
    return "%d:%02d".format(m, rem)
}
