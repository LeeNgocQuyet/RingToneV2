package com.example.ringtonev2.ui.audioInfo

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ringtonev2.R
import com.example.ringtonev2.data.remote.dto.TikTokData
import com.example.ringtonev2.data.remote.dto.resolveAudioDownloadUrl
import com.example.ringtonev2.ui.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioDownloadScreen(
    data: TikTokData,
    onContinue: () -> Unit,
    onBack: () -> Unit,
) {
    val viewModel: AudioDownloadViewModel = hiltViewModel()
    var uiState by remember(viewModel) { mutableStateOf(viewModel.uiState.value) }

    LaunchedEffect(viewModel) {
        viewModel.uiState.collect { uiState = it }
    }

    val audioUrl = remember(data) { data.resolveAudioDownloadUrl() }

    LaunchedEffect(audioUrl) {
        when {
            audioUrl.isNullOrBlank() -> viewModel.markInvalidUrl()
            else -> viewModel.startDownloadIfNeeded(audioUrl)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.downloading_audio_title),
                        style = AppTypography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = colorResource(R.color.content_default),
                    )
                },
                navigationIcon = {
                    Box(Modifier.size(40.dp))
                },
                actions = {
                    Box(Modifier.size(40.dp))
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Black,
                ),
            )
        },
        containerColor = Color.Black,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (val s = uiState) {
                is AudioDownloadUiState.Error -> {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = stringResource(R.string.download_failed),
                        style = AppTypography.bodyLarge,
                        color = colorResource(R.color.content_default),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = onBack) {
                        Text(
                            text = stringResource(R.string.cd_back),
                            color = colorResource(R.color.background_brand),
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }

                is AudioDownloadUiState.Success -> {
                    Spacer(modifier = Modifier.weight(0.2f))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        AudioDownloadRing(
                            progress = 1f,
                            modifier = Modifier.padding(vertical = 40.dp),
                        )
                        Text(
                            text = stringResource(R.string.download_success_title),
                            style = AppTypography.headlineMedium.copy(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 30.sp,
                            ),
                            color = colorResource(R.color.content_default),
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.download_success_description),
                            style = AppTypography.bodyLarge.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 20.sp,
                            ),
                            color = colorResource(R.color.content_subtlest),
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = onContinue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.background_secondary),
                            contentColor = Color.Black
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.continue_label),
                            style = AppTypography.labelMedium.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                is AudioDownloadUiState.Downloading -> {
                    Spacer(modifier = Modifier.weight(0.2f))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        AudioDownloadRing(
                            progress = s.progress,
                            modifier = Modifier.padding(vertical = 40.dp),
                        )
                        Text(
                            text = stringResource(R.string.downloading_audio_subtitle),
                            style = AppTypography.headlineMedium.copy(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 30.sp,
                            ),
                            color = colorResource(R.color.content_default),
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.downloading_audio_description),
                            style = AppTypography.bodyLarge.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 20.sp,
                            ),
                            color = colorResource(R.color.content_subtlest),
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }

                AudioDownloadUiState.Idle -> {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun AudioDownloadRing(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val brand = colorResource(R.color.background_brand)
    val track = Color(0xFF2A2A2A)
    val p = progress.coerceIn(0f, 1f)
    Box(
        modifier = modifier.size(200.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier = Modifier
                .size(200.dp)
                .aspectRatio(1f),
        ) {
            val stroke = 14.dp.toPx()
            val radius = size.minDimension / 2f - stroke / 2
            val topLeft = Offset(center.x - radius, center.y - radius)
            val arcSize = Size(radius * 2, radius * 2)
            drawArc(
                color = track,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            drawArc(
                color = brand,
                startAngle = -90f,
                sweepAngle = 360f * p,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
        }
        Text(
            text = "${(p * 100).toInt()}%",
            style = AppTypography.headlineLarge.copy(
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 44.sp,
            ),
            color = brand,
        )
    }
}
