package com.example.ringtonev2.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.ringtonev2.data.remote.api.ApiService
import com.example.ringtonev2.domain.Ringtone
import com.example.ringtonev2.domain.RingtoneRepository
import com.example.ringtonev2.ui.home.RingtonePagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val repository: RingtoneRepository,
    private val api: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchScreenState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    val favoriteIds =
        repository.observeFavorites()
            .map { list -> list.map { it.id }.toSet() }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptySet()
            )


    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query)

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)

            if (query.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    results = emptyList(),
                    isLoading = false,
                    errorMessage = null
                )
            } else {
                val response = api.getSearch(
                    search = query,
                )
                if (!response.status) {
                    _uiState.value = _uiState.value.copy(
                        results = emptyList(),
                        isLoading = false,
                        errorMessage = "Response error"
                    )
                    return@launch
                }

                val data = response.data
                if (data.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        results = emptyList(),
                        isLoading = false,
                        errorMessage = "Data not found"
                    )
                    return@launch
                }
                _uiState.value = _uiState.value.copy(
                    results = data,
                    isLoading = false,
                    errorMessage = null
                )

            }
        }
    }

    fun removeHistoryItem(value: String) {
        _uiState.value = _uiState.value.copy(
            history = _uiState.value.history.filterNot { it == value }
        )
    }

    fun useHistoryItem(value: String) {
        onQueryChange(value)
    }

    fun toggleFavorite(ringtone: Ringtone) {
        viewModelScope.launch {
            repository.toggleFavorite(ringtone)
        }
    }

    private fun loadSuggestions() {
        viewModelScope.launch {
            try {
                val response = api.getRingtones(limit = 10)
                if (response.status) {
                    val data = response.data
                    _uiState.value = _uiState.value.copy(
                        suggestions = data
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Unknown Error"
                )
            }
        }
    }
}

