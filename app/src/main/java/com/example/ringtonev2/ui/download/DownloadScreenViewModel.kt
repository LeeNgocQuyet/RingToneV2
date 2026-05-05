package com.example.ringtonev2.ui.download

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ringtonev2.data.repository.RetrofitInstance
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

data class DownloadState(
    val isLoading: Boolean = false,
    val filePath: String? = null,
    val error: String? = null
)
@HiltViewModel
class DownloadScreenViewModel @Inject constructor() : ViewModel()
{

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
        viewModelScope.launch {
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
    fun download(context: Context, link: String){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getAudio(link)
                val audioUrl = response?.data?.data?.music

                if (audioUrl == null) {
                    Log.e("DownloadViewModel", "Không lấy được audio")
                    return@launch
                }

                downloadFileInternal(context, audioUrl)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}