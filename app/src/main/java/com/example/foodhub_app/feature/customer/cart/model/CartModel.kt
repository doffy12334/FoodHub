package com.example.foodhub_app.feature.customer.cart.model

import kotlinx.serialization.Serializable

// Model gửi request thêm món vào giỏ
@Serializable
data class AddToCartRequest(
    val menuItemId: String,
    val quantity: Int,
    val variantOptionIds: List<String> = emptyList(),
    val note: String = ""
)

// Model gửi request cập nhật số lượng/topping món trong giỏ
@Serializable
data class UpdateCartItemRequest(
    val quantity: Int,
    val variantOptionIds: List<String> = emptyList(),
    val note: String = ""
)

// Model option/topping của món ăn trong giỏ
@Serializable
data class CartItemOption(
    val id: String = "",
    val name: String = "",
    val priceAdd: Long = 0L
)

// Model thông tin món ăn nằm trong giỏ
@Serializable
data class CartItem(
    val id: String = "",
    val menuItemId: String = "",
    val name: String = "",
    val basePrice: Long = 0L,
    val quantity: Int = 1,
    val imageUrl: String? = null,
    val note: String = "",
    val options: List<CartItemOption> = emptyList()
) {
    val itemTotal: Long
        get() = (basePrice + options.sumOf { it.priceAdd }) * quantity
}

// Model tổng quan giỏ hàng
@Serializable
data class CartSummary(
    val items: List<CartItem> = emptyList(),
    val subtotal: Long = 0L,
    val total: Long = 0L,
    val discountAmount: Long = 0L
)



//Model Voucher
@Serializable
data class VoucherDto(
    val id: String = "",
    val code: String = "",
    val title: String = "",
    val description: String = "",
    val discountPercent: Double = 0.0,
    val maxDiscountAmount: Long = 0L,
    val minOrderAmount: Long = 0L,
    val expiresAt: String = ""
)


@Serializable
data class ApplyVoucherRequest(
    val code: String
)