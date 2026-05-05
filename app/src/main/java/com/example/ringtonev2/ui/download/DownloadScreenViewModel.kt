package com.example.ringtonev2.ui.download

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ringtonev2.data.repository.RetrofitInstance
import com.example.ringtonev2.ui.download.state.AudioState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadScreenViewModel @Inject constructor() : ViewModel() {
    private val _audioState = MutableStateFlow<AudioState>(AudioState.Idle)
    val audioState: StateFlow<AudioState> = _audioState

//    fun downloadFileInternal(context: Context, audioUrl: String) {
//        val fileName = "tiktok_${System.currentTimeMillis()}.mp3"
//        val file = File(context.filesDir, fileName)
//        val client = OkHttpClient()
//        val request = Request.Builder()
//            .url(audioUrl)
//            .addHeader("User-Agent", "Mozilla/5.0")
//            .build()
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                try {
//                    val response = client.newCall(request).execute()
//                    if (!response.isSuccessful) {
//                        Log.e("downloadFileInternal", "Server returned ${response.code}")
//                        return@withContext
//                    }
//                    val body = response.body ?: return@withContext
//                    Log.d("OKHTTP", "Code: ${response.code}")
//                    Log.d("OKHTTP", "Message: ${response.message}")
//
//                    val input = body.byteStream()
//                    val output = FileOutputStream(file)
//
//                    val data = ByteArray(4096)
//                    var count: Int
//
//                    while (input.read(data).also { count = it } != -1) {
//                        output.write(data, 0, count)
//                    }
//
//                    output.flush()
//                    output.close()
//                    input.close()
//
//                    Log.d("DOWNLOAD", "Saved internal: ${file.absolutePath}")
//
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }
//    }

    fun resetAudioState() {
        _audioState.value = AudioState.Idle
    }

    fun getInfoAudio(context: Context, link: String) {
        _audioState.value = AudioState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getAudio(link)
                val infoAudio = response?.data?.data
                if (infoAudio != null) {
                    _audioState.value = AudioState.Success(infoAudio)
                } else {
                    _audioState.value = AudioState.Error("Data null")
                }

            } catch (e: Exception) {
                _audioState.value = AudioState.Error(e.message ?: "Unknown error")
            }
        }
    }
}