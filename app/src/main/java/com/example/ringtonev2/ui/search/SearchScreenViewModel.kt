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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
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

    private val searchQuery = MutableStateFlow("")

    val suggestions = Pager(
        config = PagingConfig(
            pageSize = 25,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            SearchRingtonePagingSource(
                api = api,
                query = null
            )
        }
    ).flow.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val ringtones = searchQuery
        .debounce(300)
        //Khi List được thay đổi thì mới Update
        .distinctUntilChanged()
        .flatMapLatest { query ->
            Pager(
                config = PagingConfig(
                    pageSize = 25,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    SearchRingtonePagingSource(
                        api = api,
                        query = query.takeIf { it.isNotBlank() }
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
        searchQuery.value = query.trim()
    }

    fun onClear(){
        _uiState.value = _uiState.value.copy(query = "")
        searchQuery.value = ""
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
    }

    fun toggleFavorite(ringtone: Ringtone) {
        viewModelScope.launch {
            repository.toggleFavorite(ringtone)
        }
    }

    fun submitSearch() {
        val query = _uiState.value.query.trim()
        if (query.isBlank()) return

        val updatedHistory = buildList {
            add(query)
            addAll(_uiState.value.history.filterNot { it.equals(query, ignoreCase = true) })
        }.take(10)

        _uiState.value = _uiState.value.copy(history = updatedHistory)
        viewModelScope.launch {
            dataStoreManager.setSearchHistory(updatedHistory)
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
    private val query: String?
) : PagingSource<Int, Ringtone>() {

    override fun getRefreshKey(state: PagingState<Int, Ringtone>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Ringtone> {
        return try {
            val page = params.key ?: 1
            val response = api.getRingtones(
                page = page,
                limit = params.loadSize,
                search = query
            )

            if (!response.status) {
                return LoadResult.Error(Throwable("API error: status=false"))
            }

            val data = response.data
            LoadResult.Page(
                data = data,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (data.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
