package com.example.foodhub_app.feature.customer.cart.data

import com.example.foodhub_app.core.network.ApiResponse
import com.example.foodhub_app.feature.customer.cart.model.AddToCartRequest
import com.example.foodhub_app.feature.customer.cart.model.ApplyVoucherRequest
import com.example.foodhub_app.feature.customer.cart.model.CartSummary
import com.example.foodhub_app.feature.customer.cart.model.UpdateCartItemRequest
import com.example.foodhub_app.feature.customer.cart.model.VoucherDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CartApi {

    //  Lấy danh sách Voucher của tôi
    @GET("vouchers/my")
    suspend fun getMyVouchers(
        @Header("Authorization") token: String? = null,
        @Query("orderType") orderType: String
    ): ApiResponse<List<VoucherDto>>

    //  Áp dụng Mã giảm giá vào giỏ hàng
    @POST("cart/{type}/voucher")
    suspend fun applyVoucher(
        @Path("type") type: String,
        @Header("Authorization") token: String? = null,
        @Header("X-Table-Token") tableToken: String? = null,
        @Body request: ApplyVoucherRequest
    ): ApiResponse<CartSummary>

    //  Hủy áp dụng Mã giảm giá
    @DELETE("cart/{type}/voucher")
    suspend fun removeVoucher(
        @Path("type") type: String,
        @Header("Authorization") token: String? = null,
        @Header("X-Table-Token") tableToken: String? = null
    ): ApiResponse<CartSummary>

    //  Lấy danh sách món ăn trong giỏ hàng
    @GET("cart/{type}/items")
    suspend fun getCartItems(
        @Path("type") cartType: String,
        @Header("Authorization") token: String? = null,
        @Header("X-Table-Token") tableToken: String? = null
    ): ApiResponse<CartSummary>

    //  Thêm món ăn mới vào giỏ
    @POST("cart/{type}/items/add")
    suspend fun addToCart(
        @Path("type") cartType: String,
        @Header("Authorization") token: String? = null,
        @Header("X-Table-Token") tableToken: String? = null,
        @Body request: AddToCartRequest
    ): ApiResponse<Unit>

    // Cập nhật món ăn (Số lượng, Topping, Ghi chú)
    @PATCH("cart/{type}/items/{id}")
    suspend fun updateCartItem(
        @Path("type") cartType: String,
        @Path("id") itemId: String,
        @Header("Authorization") token: String? = null,
        @Header("X-Table-Token") tableToken: String? = null,
        @Body request: UpdateCartItemRequest
    ): ApiResponse<Unit>

    //  Xóa 1 món khỏi giỏ hàng
    @DELETE("cart/{type}/items/{id}")
    suspend fun deleteCartItem(
        @Path("type") cartType: String,
        @Path("id") itemId: String,
        @Header("Authorization") token: String? = null,
        @Header("X-Table-Token") tableToken: String? = null
    ): ApiResponse<Unit>

    //  Xóa toàn bộ giỏ hàng
    @DELETE("cart/{type}/clear")
    suspend fun clearCart(
        @Path("type") cartType: String,
        @Header("Authorization") token: String? = null,
        @Header("X-Table-Token") tableToken: String? = null
    ): ApiResponse<Unit>
}