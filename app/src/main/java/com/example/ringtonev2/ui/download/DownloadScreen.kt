package com.example.ringtonev2.ui.download

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
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
import com.example.ringtonev2.data.repository.RetrofitInstance
import com.example.ringtonev2.ui.theme.AppTypography
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

@SuppressLint("UnrememberedMutableState")
@Composable
fun DownloadScreen(
    onOpenPlayer: (String) -> Unit,
    link: String,
    onLinkChange: (String) -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    var audioUrl by remember { mutableStateOf<String?>(null) }
    //val audioUrl = viewModel.audioUrl


    LaunchedEffect(audioUrl) {
        Log.d("DOWNLOAD", "audioUrl updated = $audioUrl")
    }
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
                onValueChange = onLinkChange,
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
                //onClick = {viewModel.downloadAudio(link)},
                    onClick = {
                        downloadAudioDirect(
                            context = context,
                            link = link,
                            onResult = { url ->
                                if (url != null) {
                                    audioUrl = url
                                    downloadFileInternal(context, url)
                                } else {

                                    Log.d("DOWNLOAD", "Không lấy được audio")
                                }
                            }
                        )
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

fun downloadFileInternal(context: Context, audioUrl: String) {
    val fileName = "tiktok_${System.currentTimeMillis()}.mp3"
    val file = File(context.filesDir, fileName)
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(audioUrl)
        .addHeader("User-Agent", "Mozilla/5.0")
        .build()
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.e("downloadFileInternal", "Server returned ${response.code}")
                return@launch
            }
            val body = response.body
            if (body == null) {
                Log.e("downloadFileInternal", "Empty body")
                return@launch
            }
            Log.d("OKHTTP", "Code: ${response.code}")
            Log.d("OKHTTP", "Message: ${response.message}")

            val input = body.byteStream()
            val output = FileOutputStream(file)

            val data = ByteArray(4096)
            var count: Int

            while (input.read(data).also { count = it } != -1) {
                output.write(data, 0, count)
            }

            output.flush()
            output.close()
            input.close()

            Log.d("DOWNLOAD", "Saved internal: ${file.absolutePath}")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun downloadAudioDirect(
    context: Context,
    link: String,
    onResult: (String?) -> Unit
) {
    val scope = CoroutineScope(Dispatchers.IO)

    scope.launch {
        try {
            val api = RetrofitInstance.api
            val response = api.getAudio(link)
            Log.d("DOWNLOAD", "RAW RESPONSE = $response")

            val audioUrl = response?.data?.data?.music
            Log.d("DOWNLOAD", "WRAPPER = ${response?.data}")
            Log.d("DOWNLOAD", "DATA = ${response?.data?.data}")
            Log.d("DOWNLOAD", "MUSIC = $audioUrl")
            withContext(Dispatchers.Main) {
                onResult(audioUrl)
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onResult(null)
            }
        }
    }
}

@Preview
@Composable
fun DownloadScreenPreview() {
    DownloadScreen(
        onOpenPlayer = {},
        link = "",
        onLinkChange = {}
    )
}