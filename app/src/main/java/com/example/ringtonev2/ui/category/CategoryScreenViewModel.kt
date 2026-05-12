package com.example.ringtonev2.ui.category

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ringtonev2.data.remote.api.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CategoryScreenViewModel @Inject constructor(
    private val api: ApiService,
) : ViewModel() {
    private val _categoryState = MutableStateFlow<CategoryState>(CategoryState.Idle)
    val categoryState: StateFlow<CategoryState> = _categoryState


    init {
        loadCategories()
    }
    fun resetCategoryState() {
        _categoryState.value = CategoryState.Idle
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                _categoryState.value =
                    CategoryState.Loading
                val categoryResponse =
                    api.getCategories()

                _categoryState.value =
                    CategoryState.Success(
                        categoryResponse.data
                    )
            } catch (e: Exception) {
                _categoryState.value =
                    CategoryState.Error(
                        e.message ?: "Unknown error"
                    )
            }
        }
    }
}