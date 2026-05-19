package com.example.ringtonev2.ui.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ringtonev2.domain.Ringtone
import com.example.ringtonev2.domain.RingtoneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistScreenViewModel @Inject constructor(
    private val repository: RingtoneRepository
) : ViewModel() {

    private val selectedTab = MutableStateFlow<PlaylistTab>(PlaylistTab.Downloads)

    private val downloadedRingtones = repository.observeDownloadedRingtones()
        .catch {
            emit(emptyList())
        }

    private val favorites = repository.observeFavorites()
        .catch {
            emit(emptyList())
        }

    val uiState: StateFlow<PlaylistState> =
        combine(
            selectedTab,
            downloadedRingtones,
            favorites
        ) { tab, downloads, favorites ->
            PlaylistState(
                isLoading = false,
                selectedTab = tab,
                downloads = downloads,
                favorites = favorites
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlaylistState(isLoading = true)
        )

    private val _currentPlayingId = MutableStateFlow<String?>(null)
    val currentPlayingId = _currentPlayingId.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    val favoriteIds =
        repository.observeFavorites()
            .map { list ->
                list.map { it.id }.toSet()
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptySet()
            )

    fun selectTab(tab: PlaylistTab) {
        selectedTab.value = tab
    }

    fun togglePlaying(ringtoneId: String) {
        if (_currentPlayingId.value == ringtoneId) {
            _isPlaying.value = !_isPlaying.value
        } else {
            _currentPlayingId.value = ringtoneId
            _isPlaying.value = true
        }
    }

    fun toggleFavorite(ringtone: Ringtone) {
        viewModelScope.launch {
            repository.toggleFavorite(ringtone)
        }
    }
}
