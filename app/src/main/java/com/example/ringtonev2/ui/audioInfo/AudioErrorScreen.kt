package com.example.ringtonev2.ui.audioInfo

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ringtonev2.R
import com.example.ringtonev2.ui.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioErrorScreen(
    viewModel: AudioInfoScreenViewModel = hiltViewModel<AudioInfoScreenViewModel>(),
    onBack: () -> Unit,
) {
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
                        onClick = {onBack()},
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
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.link_error),
                    contentDescription = null,
                    modifier = Modifier.size(180.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = stringResource(R.string.link_unavailable),
                    style = AppTypography.headlineMedium.copy(
                        color = colorResource(R.color.content_error),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.try_another_link_description),
                    style = AppTypography.bodyMedium.copy(
                        color = colorResource(R.color.content_subtlest),
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = { onBack() },
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
                        text = stringResource(R.string.try_another_link),
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