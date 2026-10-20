package com.example.foodhub_app.feature.customer.menu.model

import kotlinx.serialization.Serializable

@Serializable
data class MenuCategoryDto(
    val id: String = "",
    val name: String = "",
    val icon: String? = null,
    val isActive: Boolean = true,
    val sortOrder: Int = 0,
    val items: List<FoodDto> = emptyList()
)
