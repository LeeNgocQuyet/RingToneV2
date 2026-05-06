package com.example.ringtonev2.ui.audioDownload

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ringtonev2.R
import com.example.ringtonev2.ui.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadingScreen(
    progress: Float,
    isDone: Boolean,
    onContinue: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.download_audio),
                        style = AppTypography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.W700
                        ),
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = colorResource(R.color.content_default),
                    navigationIconContentColor = colorResource(R.color.content_default),
                    actionIconContentColor = colorResource(R.color.content_default)
                )
            )
        },
        containerColor = colorResource(R.color.Black),
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(140.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(140.dp),
                        progress = progress,
                        color = colorResource(R.color.border_focused),
                        strokeWidth = 8.dp,
                        trackColor = ProgressIndicatorDefaults.circularTrackColor,
                        strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                    )

                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.border_focused)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (!isDone)
                            stringResource(R.string.title_processing_downloading)
                        else
                            stringResource(R.string.title_processing_finish),
                        style = AppTypography.headlineMedium.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.W600
                        ),
                        color = colorResource(R.color.content_default)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (!isDone)
                            stringResource(R.string.description_processing_downloading)
                        else
                            stringResource(R.string.description_processing_finish),
                        style = AppTypography.headlineMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W500
                        ),
                        color = colorResource(R.color.content_subtlest)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (isDone) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black)
                                .padding(horizontal = 20.dp, vertical = 24.dp)
                        ) {
                            Button(
                                onClick = {} ,
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
                                    text = stringResource(R.string.continue_audio),
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
            }
        }
    }
}
