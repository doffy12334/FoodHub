package com.example.foodhub_app.feature.customer.menu.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_app.core.datastore.TokenStore
import com.example.foodhub_app.feature.customer.cart.data.CartRepository
import com.example.foodhub_app.feature.customer.menu.data.FoodRepository
import com.example.foodhub_app.feature.customer.menu.model.FoodDetail
import com.example.foodhub_app.feature.customer.menu.model.FoodReview
import com.example.foodhub_app.feature.customer.menu.model.toFoodDetail
import com.example.foodhub_app.feature.customer.menu.ui.FoodCartSelection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FoodDetailUiState(
    val isLoading: Boolean = false,
    val food: FoodDetail? = null,
    val reviews: List<FoodReview> = emptyList(),
    val error: String? = null,
    val isAdding: Boolean = false,
    val message: String? = null,
    val requiresLogin: Boolean = false,
    val requiresTableScan: Boolean = false
)

@HiltViewModel
class FoodDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val foodRepository: FoodRepository,
    private val cartRepository: CartRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val foodId: String = savedStateHandle.get<String>("foodId").orEmpty()
    private val _uiState = MutableStateFlow(FoodDetailUiState())
    val uiState: StateFlow<FoodDetailUiState> = _uiState.asStateFlow()

    init {
        loadFood()
    }

    fun loadFood() {
        if (_uiState.value.isLoading || _uiState.value.isAdding) return
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            foodRepository.getFood(foodId)
                .onSuccess { foodDto ->
                    if (foodDto != null) {
                        _uiState.update { it.copy(food = foodDto.toFoodDetail()) }
                    } else {
                        _uiState.update { it.copy(error = "Không tìm thấy món ăn") }
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message ?: "Không thể tải chi tiết món ăn") }
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun addToCart(selection: FoodCartSelection) {
        val current = _uiState.value
        if (current.isAdding || current.isLoading) return
        val food = current.food ?: return

        // 1. Kiểm tra số lượng hợp lệ & Ghi chú
        if (selection.quantity !in 1..99 || selection.note.length > 255) {
            _uiState.update { it.copy(message = "Số lượng hoặc ghi chú không hợp lệ.") }
            return
        }

        // 2. Kiểm tra các nhóm bắt buộc đã chọn tùy chọn chưa
        val hasMissingRequiredGroup = food.groups.any { group ->
            group.required && group.options.none { opt -> opt.id in selection.optionId }
        }
        if (hasMissingRequiredGroup) {
            _uiState.update { it.copy(message = "Vui lòng chọn đầy đủ các tùy chọn bắt buộc.") }
            return
        }

        _uiState.update { it.copy(isAdding = true, message = null) }

        viewModelScope.launch {
            val tableSession = tokenStore.tableSession.first()
            val user = tokenStore.getUser()

            // Xác định loại giỏ hàng
            val cartType = when {
                tableSession != null -> "DINE_IN"
                user != null -> "TAKEAWAY"
                else -> {
                    // Chưa chọn bàn & chưa đăng nhập
                    _uiState.update { it.copy(isAdding = false, requiresLogin = true) }
                    return@launch
                }
            }

            cartRepository.addToCart(cartType, selection)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isAdding = false,
                            message = "Đã thêm món vào giỏ hàng thành công!"
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isAdding = false,
                            message = error.message ?: "Không thể thêm vào giỏ hàng"
                        )
                    }
                }
        }
    }

    fun dismissLogin() { _uiState.update { it.copy(requiresLogin = false) } }
    fun dismissTableScan() { _uiState.update { it.copy(requiresTableScan = false) } }
    fun consumeMessage() { _uiState.update { it.copy(message = null) } }
}
