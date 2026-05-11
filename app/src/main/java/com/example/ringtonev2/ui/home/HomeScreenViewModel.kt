package com.example.ringtonev2.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.example.ringtonev2.data.remote.api.ApiService
import com.example.ringtonev2.data.repository.RetrofitInstance
import com.example.ringtonev2.domain.Ringtone
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: ApiService
) : ViewModel() {

    private val _homeState = MutableStateFlow<HomeState>(HomeState.Idle)
    val homeState = _homeState.asStateFlow()

    private val selectedCategory = MutableStateFlow<Int?>(null)

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

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                _homeState.value = HomeState.Loading

                val response = RetrofitInstance.api.getCategories()

                if (!response.status || response.data.isEmpty()) {
                    _homeState.value = HomeState.Error("Category not found")
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
                _homeState.value = HomeState.Error(e.message ?: "Unknown error")
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
}
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