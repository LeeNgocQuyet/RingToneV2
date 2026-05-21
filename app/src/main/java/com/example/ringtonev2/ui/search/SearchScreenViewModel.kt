package com.example.ringtonev2.ui.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.example.ringtonev2.data.datastore.DataStoreManager
import com.example.ringtonev2.data.remote.api.ApiService
import com.example.ringtonev2.domain.Ringtone
import com.example.ringtonev2.domain.RingtoneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private val api: ApiService,
    @ApplicationContext appContext: Context
) : ViewModel() {

    private val dataStoreManager = DataStoreManager(appContext)
    private val _uiState = MutableStateFlow(SearchScreenState())
    val uiState = _uiState.asStateFlow()
    private val _currentPlayingId = MutableStateFlow<String?>(null)
    val currentPlayingId = _currentPlayingId.asStateFlow()
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()
    private val _activeSearchQuery = MutableStateFlow("")
    private val noResultQueryPrefixes = mutableSetOf<String>()

    val suggestions = Pager(
        config = PagingConfig(
            pageSize = 25,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            SearchRingtonePagingSource(
                api = api,
                query = null,
                allowEmptyQuery = true
            )
        }
    ).flow.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val ringtones = _activeSearchQuery
        .flatMapLatest { query ->
            Pager(
                config = PagingConfig(
                    pageSize = 25,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    SearchRingtonePagingSource(
                        api = api,
                        query = query,
                        allowEmptyQuery = false,
                        shouldSkipQuery = ::hasNoResultPrefix,
                        onNoResult = ::rememberNoResultQuery
                    )
                }
            ).flow
        }
        .cachedIn(viewModelScope)

    val favoriteIds =
        repository.observeFavorites()
            .map { list -> list.map { it.id }.toSet() }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptySet()
            )

    init {
        viewModelScope.launch {
            dataStoreManager.searchHistoryFlow.collect { history ->
                _uiState.value = _uiState.value.copy(history = history)
            }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }

    fun onClear(){
        _uiState.value = _uiState.value.copy(
            query = "",
            submittedQuery = ""
        )
        _activeSearchQuery.value = ""
    }

    fun removeHistoryItem(value: String) {
        val updatedHistory = _uiState.value.history.filterNot { it == value }
        _uiState.value = _uiState.value.copy(history = updatedHistory)
        viewModelScope.launch {
            dataStoreManager.setSearchHistory(updatedHistory)
        }
    }

    fun useHistoryItem(value: String) {
        onQueryChange(value)
        submitSearch()
    }

    fun toggleFavorite(ringtone: Ringtone) {
        viewModelScope.launch {
            repository.toggleFavorite(ringtone)
        }
    }

    fun submitSearch() {
        val currentQuery = _uiState.value.query.trim()
        if (currentQuery.isNotBlank()) {
            _activeSearchQuery.value = currentQuery
            _uiState.value = _uiState.value.copy(submittedQuery = currentQuery)

            val updatedHistory = buildList {
                add(currentQuery)
                addAll(_uiState.value.history.filterNot {
                    it.equals(
                        currentQuery,
                        ignoreCase = true
                    )
                })
            }.take(12)

            _uiState.value = _uiState.value.copy(history = updatedHistory)
            viewModelScope.launch {
                dataStoreManager.setSearchHistory(updatedHistory)
            }
        }
    }

    private fun hasNoResultPrefix(query: String): Boolean {
        val normalizedQuery = query.normalizeSearchQuery()
        return synchronized(noResultQueryPrefixes) {
            noResultQueryPrefixes.any { prefix ->
                normalizedQuery.startsWith(prefix)
            }
        }
    }

    private fun rememberNoResultQuery(query: String) {
        val normalizedQuery = query.normalizeSearchQuery()
        if (normalizedQuery.isBlank()) {
            return
        }

        synchronized(noResultQueryPrefixes) {
            if (noResultQueryPrefixes.any { normalizedQuery.startsWith(it) }) {
                return
            }

            noResultQueryPrefixes.removeAll { it.startsWith(normalizedQuery) }
            noResultQueryPrefixes.add(normalizedQuery)
        }
    }

    fun togglePlaying(ringtoneId: String) {
        if (_currentPlayingId.value == ringtoneId) {
            _isPlaying.value = !_isPlaying.value
        } else {
            _currentPlayingId.value = ringtoneId
            _isPlaying.value = true
        }
    }
    fun onPlaybackCompleted(){
        _isPlaying.value = false
    }
}

private class SearchRingtonePagingSource(
    private val api: ApiService,
    private val query: String?,
    private val allowEmptyQuery: Boolean,
    private val shouldSkipQuery: (String) -> Boolean = { false },
    private val onNoResult: (String) -> Unit = {}
) : PagingSource<Int, Ringtone>() {

    override fun getRefreshKey(state: PagingState<Int, Ringtone>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Ringtone> {
        val page = params.key ?: 1
        val searchQuery = query?.trim().orEmpty()

        return try {
            if (!allowEmptyQuery && searchQuery.isBlank()) {
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }

            if (searchQuery.isNotBlank() && shouldSkipQuery(searchQuery)) {
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }

            val response = api.getRingtones(
                page = page,
                limit = params.loadSize,
                search = searchQuery.takeIf { it.isNotBlank() }
            )

            if (!response.status) {
                return LoadResult.Error(Throwable("API error: status=false"))
            }

            val data = response.data
            if (page == 1 && searchQuery.isNotBlank() && data.isEmpty()) {
                onNoResult(searchQuery)
            }

            LoadResult.Page(
                data = data,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (data.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            if (page == 1 && searchQuery.isNotBlank() && e.isNullSearchResponse()) {
                onNoResult(searchQuery)
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }

            LoadResult.Error(e)
        }
    }
}

private fun String.normalizeSearchQuery(): String {
    return trim().lowercase()
}

private fun Throwable.isNullSearchResponse(): Boolean {
    return this is NullPointerException &&
            message?.contains("null", ignoreCase = true) == true
}
