package com.example.ringtonev2.ui.download

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ringtonev2.R
import com.example.ringtonev2.ui.theme.AppTypography
import androidx.hilt.navigation.compose.hiltViewModel

@SuppressLint("UnrememberedMutableState")
@Composable
fun DownloadScreen(
    viewModel: DownloadScreenViewModel = hiltViewModel< DownloadScreenViewModel>(),
    onOpenPlayer: (String) -> Unit,
) {
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var link by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(248.dp)
            .padding(start = 16.dp, end = 16.dp)
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
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(id = R.string.download_tik_audio),
                style = AppTypography.titleMedium.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W600
                ),
                color = colorResource(R.color.content_secondary),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringResource(id = R.string.paste_tik_link),
                style = AppTypography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500
                ),
                color = colorResource(R.color.content_subtlest),
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
                onValueChange ={ link = it } ,
                placeholder = {
                    Text(
                        stringResource(id = R.string.paste_tik_link_here),
                        style = AppTypography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500
                        ),
                        color = colorResource(R.color.content_disabled),
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorResource(R.color.border_bold),
                    unfocusedContainerColor = colorResource(R.color.border_bold),
                    focusedIndicatorColor = colorResource(R.color.border_bold),
                    unfocusedIndicatorColor = colorResource(R.color.border_bold),
                    focusedTextColor = colorResource(R.color.content_default),
                    unfocusedTextColor = colorResource(R.color.content_disabled),
                    cursorColor = colorResource(R.color.White)
                ),
                singleLine = true,
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                    onClick = {
                        if(!link.isBlank())
                            viewModel.download(context, link)
                        else {

                        }

                    },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.background_secondary),
                    contentColor = colorResource(R.color.Black)
                )
            ) {
                Text(
                    text = stringResource(id = R.string.download_audio),
                    style = AppTypography.labelMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600
                    ),
                    color = colorResource(R.color.Black)
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



@Preview
@Composable
fun DownloadScreenPreview() {
    DownloadScreen(
        onOpenPlayer = {},
    )
}