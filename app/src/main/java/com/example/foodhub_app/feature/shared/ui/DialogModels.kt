package com.example.foodhub_app.feature.shared.ui

import com.example.foodhub_app.feature.customer.order.model.OrderType

enum class AdminOrderStatus(val label: String) {
    PENDING_PAYMENT("Chờ thanh toán"),
    PENDING_CONFIRMATION("Chờ xác nhận"),
    CONFIRMED("Đã xác nhận"),
    PREPARING("Đang chế biến"),
    READY("Sẵn sàng"),
    SERVED("Đã phục vụ"),
    COMPLETED("Hoàn thành"),
    CANCELLED("Đã hủy"),
    PAYMENT_FAILED("Thanh toán lỗi"),
}

enum class AdminOrderType(val label: String) {
    DINE_IN("Tại bàn"),
    TAKEAWAY("Mang về"),
    DELIVERY("Giao hàng"),
}

enum class AdminPaymentMethod(val label: String) {
    CASH("Tiền mặt"),
    VNPAY("VNPay"),
    ZALOPAY("ZaloPay"),
    MOMO("MoMo"),
}

data class AdminZaloPayment(
    val orderId: String,
    val orderCode: String,
    val paymentUrl: String,
    val qrContent: String?,
)

enum class AdminItemStatus(val label: String) {
    WAITING("Chờ làm"),
    PREPARING("Đang làm"),
    READY("Đã xong"),
    SERVED("Đã phục vụ"),
}

data class AdminOrderLine(
    val name: String,
    val quantity: Int,
    val unitPrice: Long,
    val options: List<String> = emptyList(),
    val note: String? = null,
    val status: AdminItemStatus = AdminItemStatus.WAITING,
    val id: String = "",
    val menuItemId: String = "",
    val imageUrl: String? = null,
)

data class AdminOrderReview(
    val id: String,
    val menuItemId: String,
    val customerName: String,
    val rating: Int,
    val comment: String,
    val images: List<String>,
    val createdAt: String,
)

data class AdminOrder(
    val id: String,
    val code: String,
    val type: AdminOrderType,
    val destination: String,
    val createdAt: String,
    val waitMinutes: Int,
    val items: List<AdminOrderLine>,
    val total: Long,
    val status: AdminOrderStatus,
    val paymentMethod: AdminPaymentMethod,
    val paymentStatus: String = "UNPAID",
    val paid: Boolean,
    val createdAtEpochMillis: Long = 0L,
    val subtotal: Long = 0L,
    val vatAmount: Long = 0L,
    val deliveryFee: Long = 0L,
    val serviceFee: Long = 0L,
    val discountAmount: Long = 0L,
    val reviews: List<AdminOrderReview> = emptyList(),
)

data class AdminMenuItem(
    val id: String,
    val name: String,
    val category: String,
    val price: Long,
    val salePrice: Long? = null,
    val isAvailable: Boolean,
    val isFeatured: Boolean,
    val icon: String,
    val categoryId: String = "",
    val description: String? = null,
    val imageUrl: String? = null,
    val sortOrder: Int = 0,
)

data class AdminMenuCategory(
    val id: String,
    val name: String,
    val icon: String = "restaurant",
    val sortOrder: Int = 0,
    val isActive: Boolean = true,
    val itemCount: Int = 0,
)

data class AdminMenuCategoryInput(
    val name: String,
    val icon: String,
    val sortOrder: Int,
)

data class AdminMenuInput(
    val categoryId: String,
    val name: String,
    val description: String?,
    val basePrice: Long,
    val imageUrl: String?,
    val isAvailable: Boolean,
    val isFeatured: Boolean,
    val sortOrder: Int,
)

data class AdminFlashSaleInput(val itemId: String, val discountPercent: Double, val durationHours: Int)
data class AdminVariantOptionInput(val name: String, val priceAdd: Long, val id: String? = null)
data class AdminVariantGroup(val id: String, val input: AdminVariantInput)
data class AdminVariantInput(
    val name: String,
    val multiple: Boolean,
    val required: Boolean,
    val options: List<AdminVariantOptionInput>,
    val sortOrder: Int = 0,
)

data class AdminRestaurantTable(
    val id: String,
    val name: String,
    val capacity: Int,
    val floor: String?,
    val isActive: Boolean,
    val note: String? = null,
    val status: String = if (isActive) "AVAILABLE" else "INACTIVE",
    val qrToken: String? = null,
    val currentOrderCode: String? = null,
    val currentOrderTotal: Long? = null,
    val currentOrderCreatedAt: String? = null,
)

data class CartOption(
    val id: String,
    val name: String,
    val priceAdd: Long
)

/**
 * Một dòng trong giỏ hàng.
 *
 * [id] là cart item ID dùng cho PATCH/DELETE. [menuItemId] là ID món trong menu,
 * chỉ dùng để tải lại chi tiết món; hai giá trị này không thể thay thế cho nhau.
 */
data class CartItem(
    val id: String,
    val menuItemId: String,
    val name: String,
    val imageUrl: String?,
    val quantity: Int,
    val unitPrice: Long,
    val subTotal: Long,
    val options: List<CartOption>,
    val note: String
)

data class Cart(
    val items: List<CartItem>,
    val totalAmount: Long,
    val subtotal: Long = totalAmount,
    val vatAmount: Long = 0L,
    val deliveryFee: Long = 0L,
    val serviceFee: Long = 0L,
    val discountAmount: Long = 0L,
    val voucher: AppliedVoucher? = null,
    val voucherError: String? = null,
)

data class AppliedVoucher(val id: String, val code: String, val name: String, val discountAmount: Long)
data class AccountVoucher(
    val id: String, val code: String, val name: String, val description: String,
    val discountType: String, val discountValue: Double, val minOrderAmount: Long,
    val maxDiscountAmount: Long?, val endsAt: String,
)

data class CheckoutRequest(
    val type: OrderType,
    val paymentMethod: String,
    val note: String = "",
    val recipientName: String = "",
    val phone: String = "",
    val address: String = "",
)

data class CheckoutResult(
    val orderId: String,
    val orderCode: String,
    val paymentMethod: String,
    val paymentUrl: String?,
    val qrContent: String? = null,
    val amount: Long = 0,
)

/** Body của PATCH cart item; theo docs phải có ít nhất một trường khác null. */
data class CartItemUpdate(
    val quantity: Int? = null,
    val variantOptionIds: List<String>? = null,
    val note: String? = null
)


data class AdminVoucher(
    val id: String = "",
    val code: String = "",
    val name: String = "",
    val description: String = "",
    val discountType: String = "PERCENT",
    val distribution: String = "SECRET",
    val discountValue: Double = 0.0,
    val minOrderAmount: Long = 0,
    val maxDiscountAmount: Long? = null,
    val applicableOrderTypes: List<String> = listOf("DINE_IN", "TAKEAWAY", "DELIVERY"),
    val startsAt: String = "",
    val endsAt: String = "",
    val usageLimit: Int? = null,
    val usedCount: Int = 0,
    val perCustomerLimit: Int = 1,
    val isActive: Boolean = true,
)
data class QrDialogState(
    val table: AdminRestaurantTable,
    val content: String?,
    val loading: Boolean,
    val error: String?,
)