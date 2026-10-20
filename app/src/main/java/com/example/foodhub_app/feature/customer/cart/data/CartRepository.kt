package com.example.foodhub_app.feature.customer.cart.data

import com.example.foodhub_app.core.datastore.TokenStore
import com.example.foodhub_app.feature.customer.cart.model.AddToCartRequest
import com.example.foodhub_app.feature.customer.cart.model.ApplyVoucherRequest
import com.example.foodhub_app.feature.customer.cart.model.CartSummary
import com.example.foodhub_app.feature.customer.cart.model.UpdateCartItemRequest
import com.example.foodhub_app.feature.customer.cart.model.VoucherDto
import com.example.foodhub_app.feature.customer.menu.ui.FoodCartSelection
import com.example.foodhub_app.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CartRepository @Inject constructor(
    private val cartApi: CartApi,
    private val tokenStore: TokenStore
) {

    // Gọi API thêm món ăn vào giỏ hàng
    suspend fun addToCart(
        cartType: String,
        selection: FoodCartSelection
    ): Result<Unit> = withContext(
        Dispatchers.IO
    ) {
        runCatching {
            val bearerToken = getBearerToken()
            val tableToken = getTableToken()
            val request = AddToCartRequest(
                menuItemId = selection.foodId,
                quantity = selection.quantity,
                variantOptionIds = selection.optionId,
                note = selection.note
            )

            cartApi.addToCart(
                cartType = cartType,
                token = bearerToken,
                tableToken = tableToken,
                request = request
            )
            Unit
        }
    }


    // Gọi API lấy danh sách món ăn trong giỏ
    suspend fun getCartItems(cartType: String): Result<CartSummary> = withContext(Dispatchers.IO) {
        runCatching {
            val response = cartApi.getCartItems(
                cartType = cartType,
                token = getBearerToken(),
                tableToken = getTableToken()
            )
            response.data ?: CartSummary()
        }
    }

    // Cập nhât số lượng , topping hoặc ghi chú của 1 món ăn trong giỏ
    suspend fun updateCartItem(
        cartType: String,
        itemId: String,
        quantity: Int,
        optionId: List<String> = emptyList(),
        note: String = ""
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val request = UpdateCartItemRequest(
                quantity = quantity,
                variantOptionIds = optionId,
                note = note
            )
            cartApi.updateCartItem(
                cartType = cartType,
                itemId = itemId,
                token = getBearerToken(),
                tableToken = getTableToken(),
                request = request
            )
            Unit
        }
    }


    // Xoá 1 món khỏi giỏ hàng
    suspend fun deleteCartItem(cartType: String, itemId: String):
            Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            cartApi.deleteCartItem(
                cartType = cartType,
                itemId = itemId,
                token = getBearerToken(),
                tableToken = getTableToken()
            )
            Unit
        }
    }

    // Xoá Sạch toàn bộ giỏ hàng
    suspend fun clearCart(cartType: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            cartApi.clearCart(
                cartType = cartType,
                token = getBearerToken(),
                tableToken = getTableToken()
            )
            Unit
        }
    }

    // Áp dụng voucher
    suspend fun appVoucher(cartType: String, code: String): Result<CartSummary> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = cartApi.applyVoucher(
                    type = cartType,
                    token = getBearerToken(),
                    tableToken = getTableToken(),
                    request = ApplyVoucherRequest(code)
                )
                response.data ?: CartSummary()
            }
        }

    // Huỷ voucher
    suspend fun removeVoucher(cartType: String): Result<CartSummary> = withContext(Dispatchers.IO) {
        runCatching {
            val response = cartApi.removeVoucher(
                type = cartType,
                token = getBearerToken(),
                tableToken = getTableToken()
            )
            response.data ?: CartSummary()
        }
    }

    // Lấy danh sách voucher
    suspend fun getVoucherList(cartType: String): Result<List<VoucherDto>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = cartApi.getMyVouchers(
                    token = getBearerToken(),
                    orderType = cartType
                )
                response.data ?: emptyList()
            }
        }


    // Hàm phụ trợ lấy Bearer token
    private suspend fun getBearerToken(): String? {
        val accessToken = tokenStore.getAccessToken()
        return if (!accessToken.isNullOrBlank()) {
            "Bearer $accessToken"
        } else {
            null
        }
    }

    // Hàm phụ trợ lấy Table token
    private suspend fun getTableToken(): String? {
    return tokenStore.tableSession.first()?.tableToken
    }

}