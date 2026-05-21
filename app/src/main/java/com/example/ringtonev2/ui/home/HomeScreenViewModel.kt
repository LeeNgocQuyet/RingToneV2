package com.example.ringtonev2.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.example.ringtonev2.R
import com.example.ringtonev2.data.remote.api.ApiService
import com.example.ringtonev2.domain.Ringtone
import com.example.ringtonev2.domain.RingtoneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: ApiService,
    private val repository: RingtoneRepository
) : ViewModel() {

    private val _homeState = MutableStateFlow<HomeState>(HomeState.Idle)
    val homeState = _homeState.asStateFlow()

    private val selectedCategory = MutableStateFlow<Int?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val ringtones = selectedCategory.flatMapLatest { categoryId ->

        Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                RingtonePagingSource(api, categoryId)
            }
        ).flow
    }.cachedIn(viewModelScope)

    private val _currentPlayingId = MutableStateFlow<String?>(null)
    val currentPlayingId = _currentPlayingId.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    val favoriteIds =
        repository.observeFavorites()
            .map { list ->
                //Todo
                list.map {
                    it.id
                }.toSet()
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptySet()
            )
    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                _homeState.value = HomeState.Loading

                val response = api.getCategories()

                if (!response.status || response.data.isEmpty()) {
                    _homeState.value = HomeState.Error(R.string.category_not_found)
                    return@launch
                }

                val categories = response.data
                val firstCategoryId = categories.first().id

                selectedCategory.value = firstCategoryId

                _homeState.value = HomeState.Success(
                    categories = categories,
                    selectedCategoryId = firstCategoryId
                )

            } catch (e: Exception) {
                _homeState.value = HomeState.Error(e.message ?: R.string.unknown_error)
            }
        }
    }

    fun selectCategory(id: Int?) {
        selectedCategory.value = id
        _homeState.value = HomeState.Success(
            categories = (_homeState.value as HomeState.Success).categories,
            selectedCategoryId = id
        )
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

    fun toggleFavorite(
        ringtone: Ringtone
    ) {

        viewModelScope.launch {

            repository
                .toggleFavorite(ringtone)
        }
    }

    fun isFavorite(
        ringtoneId: String
    ): Flow<Boolean> {

        return repository
            .isFavorite(ringtoneId)
    }
}
//Todo Tách ra đi và sử lý trong repository
class RingtonePagingSource(
    private val api: ApiService,
    private val categoryId: Int?
) : PagingSource<Int, Ringtone>() {

    override fun getRefreshKey(state: PagingState<Int, Ringtone>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, Ringtone> {

        return try {

            val page = params.key ?: 1

            val response = api.getRingtones(
                page = page,
                limit = params.loadSize,
                categoryIds = categoryId?.toString()
            )

            if (!response.status) {
                return LoadResult.Error(
                    Throwable("API error: status=false")
                )
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