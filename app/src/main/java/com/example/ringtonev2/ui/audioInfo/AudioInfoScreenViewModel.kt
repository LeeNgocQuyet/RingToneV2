package com.example.ringtonev2.ui.audioInfo

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.ringtonev2.data.remote.dto.TikTokData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import androidx.compose.runtime.State

@HiltViewModel
class AudioInfoScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    val player: ExoPlayer = ExoPlayer.Builder(context).build()
    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

    private val _showControls = mutableStateOf(true)

    fun init( data: TikTokData,
    ) {
        val videoUrl = data.hdPlay ?: data.play
        videoUrl?.let  {
            player.setMediaItem(MediaItem.fromUri(it))
            player.prepare()
        }
    }

    fun togglePlay() {
        if (player.isPlaying) {
            player.pause()
            _isPlaying.value = false
        } else {
            player.play()
            _isPlaying.value = true
        }
    }
    fun onVideoTap() {
        if (player.isPlaying) {
            player.pause()
            _isPlaying.value = false
        } else {
            player.play()
            _isPlaying.value = true

        }
    }

    override fun onCleared() {
        player.release()
        super.onCleared()
    }

    fun downloadFileInternal(context: Context, audioUrl: String) {
        val fileName = "tiktok_${System.currentTimeMillis()}.mp3"
        val file = File(context.filesDir, fileName)
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(audioUrl)
            .addHeader("User-Agent", "Mozilla/5.0")
            .build()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val response = client.newCall(request).execute()
                    if (!response.isSuccessful) {
                        Log.e("downloadFileInternal", "Server returned ${response.code}")
                        return@withContext
                    }
                    val body = response.body ?: return@withContext
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
    }
}