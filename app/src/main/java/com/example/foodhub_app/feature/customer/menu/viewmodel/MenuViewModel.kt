package com.example.foodhub_app.feature.customer.menu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_app.feature.customer.menu.data.FoodRepository
import com.example.foodhub_app.feature.customer.menu.model.MenuCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Trạng thái giao diện UI
data class MenuUiState(
    val categories: List<MenuCategory> = emptyList(),
    val selectedCategory: MenuCategory? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errMessage: String? = null
)

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val repository: FoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    init {
        loadMenu()
    }

    fun loadMenu() {
        if (_uiState.value.isLoading) return

        _uiState.update {
            it.copy(isLoading = true, errMessage = null)
        }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, errMessage = null) }
            repository.getMenu()
                .onSuccess { menuList ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            categories = menuList,
                            selectedCategory = menuList.firstOrNull()
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errMessage = exception.message ?: throw IllegalStateException(
                                "Không thể tải" +
                                        " danh " +
                                        "sách thực đơn")
                        )
                    }
                }
        }
    }

    fun onCategorySelect(category: MenuCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}
