package com.example.foodhub_app.feature.customer.menu.model


//Data cho man hinh detail
data class FoodOption(
    val id: String,
    val name: String,
    val priceAdd: Long
)

data class FoodOptionGroup(
    val id: String,
    val name: String,
    val multiple: Boolean,
    val required: Boolean,
    val options: List<FoodOption> = emptyList()
)

data class FoodReview(
    val id: String,
    val customerName: String,
    val rating: Int,
    val comment: String,
    val createdAt: String,
    val images: List<String> = emptyList()
)

data class FoodDetail(
    val id: String,
    val name: String,
    val description: String,
    val basePrice: Long,
    val rating: String,
    val available: Boolean = true,
    val groups: List<FoodOptionGroup> = emptyList(),
    val imageUrl: String? = null,
    val salePrice: Long? = null
)
