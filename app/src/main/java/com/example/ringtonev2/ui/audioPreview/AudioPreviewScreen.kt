package com.example.ringtonev2.ui.audioPreview

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

import com.example.ringtonev2.R
import com.example.ringtonev2.ui.theme.AppTypography


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPreviewScreen(
    onBack: () -> Unit
) {
    val viewModel: AudioPreviewScreenViewModel = hiltViewModel()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Audio Preview",
                        color = Color.White,
                        fontSize = 18.sp
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
                        text = "Tik Viral Hit 2024",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = "Audio track",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Icon(
                    painter = painterResource(R.drawable.ic_favourite),
                    contentDescription = null,
                    tint = Color.Red
                )
            }

            Spacer(Modifier.height(20.dp))

            // PROGRESS BAR
            Slider(
                value = 0.4f,
                colors = androidx.compose.material3.SliderDefaults.colors(
                    thumbColor = colorResource(R.color.background_brand),
                    activeTrackColor = colorResource(R.color.background_brand),
                    inactiveTrackColor = colorResource(R.color.background_neutral),
                ),
                enabled = false,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth()
            )

            // TIME
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    formatDuration(0),
                    style = AppTypography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 16.sp,
                    ),
                    color = colorResource(R.color.content_subtlest)
                )
                Text(
                    formatDuration(
                        90
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

                IconButton(onClick = { }) {
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
                    Icon(
                        painter = painterResource(R.drawable.ic_pause),
                        contentDescription = null,
                        tint = Color.Black
                    )
                }

                IconButton(onClick = { }) {
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

@Preview
@Composable
fun previewAudioPreviewScreen() {
    AudioPreviewScreen(onBack = {})
}

fun formatDuration(seconds: Long?): String {
    if (seconds == null || seconds <= 0L) return "00:00"

    val totalSeconds = seconds.toInt()
    val minutes = totalSeconds / 60
    val secs = totalSeconds % 60

    return "%02d:%02d".format(minutes, secs)
}