package com.example.foodhub_app.feature.customer.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_app.core.datastore.TokenStore
import com.example.foodhub_app.feature.customer.menu.data.FoodRepository
import com.example.foodhub_app.feature.customer.menu.model.MenuCategory
import com.example.foodhub_app.feature.customer.menu.model.MenuFood
import com.example.foodhub_app.feature.customer.order.model.OrderType
import com.example.foodhub_app.feature.shared.auth.model.UserDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject

// Model dữ liệu chứa thông tin bàn ăn khi khách quét QR
@Serializable
data class HomeTableSession(
    val tableId: String,
    val tableName: String,
    val floor: String? = null,
    val capacity: Int? = null,
    val tableToken: String = "",
    val sessionId: String? = null,
    val expiresAtEpochMillis: Long? = null,
    val isHost: Boolean = false,
)

// Trạng thái giao diện trang chủ chứa thông tin user, danh mục, món ăn và bộ lọc
data class HomeUiState(
    val user: UserDto? = null,
    val selectedOrderType: OrderType = OrderType.TAKEAWAY,
    val tableSession: HomeTableSession? = null,
    val categories: List<MenuCategory> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val query: String = "",
    val selectedCategoryId: String? = null,
    val maxPrice: Long? = null,
    val cartItemCount: Int = 0,
    val unreadNotificationCount: Int = 0,
) {
    // Trạng thái kiểm tra xem người dùng đã đăng nhập hay chưa
    val isLoggedIn get() = user != null

    // Trạng thái xác định người dùng là khách vãng lai
    val isGuest get() = !isLoggedIn

    // Lấy mã bàn ăn hiện tại
    val tableId get() = tableSession?.tableId

    // Lấy tên bàn ăn hiện tại
    val tableName get() = tableSession?.tableName

    // Lấy danh sách món ăn hiển thị sau khi áp dụng các bộ lọc tìm kiếm
    val visibleFoods: List<MenuFood>
        get() = categories
            .asSequence()
            .filter { selectedCategoryId == null || it.id == selectedCategoryId }
            .flatMap { it.items }
            .filter { query.isBlank() || it.name.contains(query.trim(), ignoreCase = true) }
            .filter { food -> maxPrice?.let { food.price <= it } ?: true }
            .sortedByDescending { food ->
                food.name.startsWith(query.trim(), ignoreCase = true) ||
                        food.name.split(" ")
                            .any { word ->
                                word.startsWith(query.trim(), ignoreCase = true)
                            }
            }
            .toList()

    // Lấy danh sách 5 món ăn nổi bật hoặc bán chạy nhất
    val popularFoods: List<MenuFood>
        get() {
            val available = categories.flatMap { it.items }
                .filter { it.available }
            val featured = available.filter { it.isFeatured }
                .sortedByDescending { it.totalOrder }
            return featured.ifEmpty { available.sortedByDescending { it.totalOrder } }
                .take(5)
        }
}

// ViewModel quản lý logic trang chủ, theo dõi session user và xử lý bộ lọc thực đơn
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tokenStore: TokenStore,
    private val foodRepository: FoodRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()
    private var loading = false

    init {
        viewModelScope.launch {
            tokenStore.user.collect { user ->
                _uiState.update { current ->
                    current.copy(
                        user = user,
                        selectedOrderType = if (user == null && current.selectedOrderType == OrderType.DELIVERY)
                            OrderType.TAKEAWAY else current.selectedOrderType,
                    )
                }
            }
        }
        viewModelScope.launch {
            tokenStore.tableSession.collect { tableSession ->
                _uiState.update { current ->
                    current.copy(
                        tableSession = tableSession,
                        selectedOrderType = if (tableSession != null) OrderType.DINE_IN else current.selectedOrderType
                    )
                }
            }
        }
        loadHomeData()
    }

    // Tải danh sách thực đơn và danh mục từ repository
    fun loadHomeData() {
        if (loading) return
        loading = true
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val categories = foodRepository.getMenu()
                    .getOrThrow()
                _uiState.update { it.copy(categories = categories) }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = error.message ?: "Không thể tải dữ liệu trang chủ")
                }
            } finally {
                loading = false
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // Cập nhật từ khóa tìm kiếm món ăn
    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    // Chọn hoặc bỏ chọn danh mục món ăn để lọc
    fun selectCategory(id: String?) {
        _uiState.update { it.copy(selectedCategoryId = id) }
    }

    // Thiết lập mức giá tối đa để lọc món ăn
    fun selectMaxPrice(price: Long?) {
        _uiState.update { it.copy(maxPrice = price) }
    }

    // Thay đổi hình thức đặt hàng (Tại bàn, Mang về, Giao tận nơi)
    fun selectOrderType(type: OrderType) {
        val current = _uiState.value
        if (type == OrderType.DINE_IN && current.tableSession == null) return
        if (type == OrderType.DELIVERY && !current.isLoggedIn) return
        _uiState.update { it.copy(selectedOrderType = type) }
    }

    // Lưu thông tin bàn và chuyển sang hình thức ăn tại bàn sau khi quét QR
    fun onQrScanned(session: HomeTableSession) {
        viewModelScope.launch {
            tokenStore.saveTableSession(session)
        }
    }

    // Xóa thông tin bàn ăn hiện tại và chuyển về hình thức mang về
    fun clearTableInfo() {
        viewModelScope.launch {
            tokenStore.saveTableSession(null)
            _uiState.update { it.copy(selectedOrderType = OrderType.TAKEAWAY) }
        }
    }
}
