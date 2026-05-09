package com.example.ringtonev2.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ringtonev2.data.repository.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _homeState = MutableStateFlow<HomeState>(HomeState.Idle)
    val homeState = _homeState.asStateFlow()

    init {
        loadHomeData()
        Log.d("HomeViewModel", "init")
    }

    fun resetHomeState() {
        _homeState.value = HomeState.Idle
    }

    private fun loadHomeData() {

        viewModelScope.launch {

            try {

                _homeState.value = HomeState.Loading

                val categoryResponse = RetrofitInstance.api.getCategories()

                val ringtoneResponse = RetrofitInstance.api.getRingtones()

                val categories = categoryResponse.data
                val ringtones = ringtoneResponse.data

                Log.d("HomeViewModel", "categories = $categories")
                Log.d("HomeViewModel", "ringtones = $ringtones")

                _homeState.value = HomeState.Success(
                    categories = categories,
                    ringtones = ringtones
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

        val currentState = _homeState.value

        if (currentState !is HomeState.Success) return

        viewModelScope.launch {

            try {

                _homeState.value = HomeState.Loading

                val response = RetrofitInstance.api.getRingtones(
                    categoryIds = categoryId?.toString()
                )

                _homeState.value = currentState.copy(
                    selectedCategoryId = categoryId,
                    ringtones = response.data
                )

                Log.d(
                    "HomeViewModel",
                    "selectCategory = $categoryId"
                )

            } catch (e: Exception) {

                Log.e(
                    "HomeViewModel",
                    "getRingtonesByCategory error",
                    e
                )

                _homeState.value = HomeState.Error(
                    e.message ?: "Unknown error"
                )
            }
        }
    }
}