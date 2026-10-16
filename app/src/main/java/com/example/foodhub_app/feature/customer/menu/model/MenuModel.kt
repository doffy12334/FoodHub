package com.example.foodhub_app.feature.customer.menu.model


//Data mon an va danh muc cho UI
data class MenuFood(
    val id: String,
    val name: String,
    val price: Long,
    val imageUrl: String? = null,
    val description: String = "",
    val rating: String = "0",
    val isFeatured: Boolean = false,
    val totalOrder: Int = 0,
    val available: Boolean = true
)

data class MenuCategory(
    val id: String,
    val name: String,
    val items: List<MenuFood> = emptyList()
)
