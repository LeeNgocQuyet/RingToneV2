package com.example.ringtonev2.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ringtonev2.data.repository.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _homeState = MutableStateFlow(HomeUiState())
    val homeState: StateFlow<HomeUiState> = _homeState

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            try {

                _homeState.update {
                    it.copy(isLoading = true)
                }

                val categoryResponse = RetrofitInstance.api.getCategories()
                val ringtoneResponse = RetrofitInstance.api.getRingtones()

                _homeState.update {
                    it.copy(
                        isLoading = false,
                        categories = categoryResponse.data,
                        ringtones = ringtoneResponse.data
                    )
                }

            } catch (e: Exception) {

                _homeState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun selectCategory(categoryId: Int?) {

        _homeState.update {
            it.copy(selectedCategoryId = categoryId)
        }

        getRingtonesByCategory(categoryId)
    }

    private fun getRingtonesByCategory(categoryId: Int?) {

        viewModelScope.launch {

            try {

                _homeState.update {
                    it.copy(isLoading = true)
                }

                val response = RetrofitInstance.api.getRingtones(
                    categoryIds = categoryId?.toString()
                )

                _homeState.update {
                    it.copy(
                        isLoading = false,
                        ringtones = response.data
                    )
                }

            } catch (e: Exception) {

                _homeState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }
}