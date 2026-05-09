package com.example.ringtonev2.ui.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ringtonev2.data.repository.RetrofitInstance
import com.example.ringtonev2.domain.Ringtone
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _homeState = MutableStateFlow<HomeState>(HomeState.Idle)
    val homeState = _homeState.asStateFlow()

    private val ringtoneCache = mutableMapOf<Int?, List<Ringtone>>()
    private var currentPage = 1

    var isLoadingMore by mutableStateOf(false)
        private set
    private var hasMoreData = true

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            try {
                _homeState.value = HomeState.Loading
                val categoryResponse = RetrofitInstance.api.getCategories()
                if (!categoryResponse.status || categoryResponse.data.isEmpty()) {

                    _homeState.value = HomeState.Error(
                        "Category not found"
                    )

                    return@launch
                }
                val categories = categoryResponse.data
                val firstCategoryId = categories.first().id
                val ringtoneResponse = RetrofitInstance.api.getRingtones(
                    categoryIds = firstCategoryId.toString()
                )
                if (!ringtoneResponse.status) {
                    _homeState.value = HomeState.Error(
                        "Ringtone not found"
                    )
                    return@launch
                }
                val ringtones = ringtoneResponse.data
                ringtoneCache[firstCategoryId] = ringtones
                _homeState.value = HomeState.Success(
                    categories = categories,
                    ringtones = ringtones,
                    selectedCategoryId = firstCategoryId
                )

            } catch (e: Exception) {
                Log.e("HomeViewModel", "loadHomeData error", e)
                _homeState.value = HomeState.Error(
                    e.message ?: "Unknown error"
                )
            }
        }
    }

    fun selectCategory(categoryId: Int?) {
        val currentState = _homeState.value as? HomeState.Success
            ?: return

        if (currentState.selectedCategoryId == categoryId) {
            return
        }
        currentPage = 1
        hasMoreData = true

        _homeState.update {
            currentState.copy(
                selectedCategoryId = categoryId
            )
        }

        ringtoneCache[categoryId]?.let { cachedRingtones ->
            _homeState.update { state ->
                if (state is HomeState.Success) {
                    state.copy(
                        ringtones = cachedRingtones
                    )
                } else {
                    state
                }
            }

            return
        }

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getRingtones(
                    page = 1,
                    categoryIds = categoryId?.toString()
                )
                val ringtones = response.data.toMutableList()
                ringtoneCache[categoryId] = ringtones
                _homeState.update { state ->
                    if (state is HomeState.Success) {
                        state.copy(
                            ringtones = ringtones
                        )
                    } else {
                        state
                    }
                }
            } catch (e: Exception) {
                Log.e(
                    "HomeViewModel",
                    "getRingtonesByCategory error",
                    e
                )
            }
        }
    }

    fun loadMore() {
        val currentState = _homeState.value as? HomeState.Success
            ?: return

        if (isLoadingMore || !hasMoreData) {
            return
        }
        isLoadingMore = true
        viewModelScope.launch {
            try {
                val nextPage = currentPage + 1
                val response = RetrofitInstance.api.getRingtones(
                    page = nextPage,
                    categoryIds = currentState.selectedCategoryId?.toString()
                )

                val newRingtones = response.data
                if (newRingtones.isEmpty()) {
                    hasMoreData = false
                    return@launch
                }
                val currentList = currentState.ringtones.toMutableList()
                currentList.addAll(newRingtones)
                ringtoneCache[currentState.selectedCategoryId] = currentList
                currentPage = nextPage
                _homeState.update { state ->
                    if (state is HomeState.Success) {
                        state.copy(
                            ringtones = currentList
                        )
                    } else {
                        state
                    }
                }

            } catch (e: Exception) {
                Log.e(
                    "HomeViewModel",
                    "loadMore error",
                    e
                )
            } finally {
                isLoadingMore = false
            }
        }
    }
}