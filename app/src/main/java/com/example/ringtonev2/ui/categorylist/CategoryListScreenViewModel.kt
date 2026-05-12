package com.example.ringtonev2.ui.categorylist

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryListScreenViewModel @Inject constructor(
    private val api: ApiService,
    private val repository: RingtoneRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CategoryState>(CategoryState.Idle)
    val uiState = _uiState.asStateFlow()

    private val selectedCategory = MutableStateFlow<Int?>(null)

    val ringtones = selectedCategory
        .flatMapLatest { categoryId ->
            Pager(
                config = PagingConfig(
                    pageSize = 25,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    RingtonePagingSource(api, categoryId)
                }
            ).flow
        }
        .cachedIn(viewModelScope)

    private val _currentPlayingId = MutableStateFlow<String?>(null)
    val currentPlayingId = _currentPlayingId.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()
    val favoriteIds =
        repository.getFavorites()
            .map { list ->
                list.map { it.id.toString() }.toSet()
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptySet()
            )
    var categoryName by mutableStateOf("")
        private set

    init {
        loadCategories()
    }
    fun initCategory(categoryId: Int) {
        selectedCategory.value = categoryId
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = CategoryState.Loading

            try {
                val response = api.getCategories()

                if (!response.status || response.data.isEmpty()) {
                    _uiState.value = CategoryState.Error("Category not found")
                    return@launch
                }

                val categories = response.data
                val currentId = selectedCategory.value
                 categoryName =
                    categories.find { it.id == currentId }?.name ?: "Unknow"

                _uiState.value = CategoryState.Success(
                    categories = categories,
                    selectedCategoryId = currentId,
                    categoryName = categoryName
                )


            } catch (e: Exception) {
                _uiState.value = CategoryState.Error(
                    e.message ?: "Unknown error"
                )
            }
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

    fun onPlaybackCompleted() {
        _isPlaying.value = false
    }
    fun toggleFavorite(ringtone: Ringtone) {
        viewModelScope.launch {
            repository.toggleFavorite(ringtone)
        }
    }
}
