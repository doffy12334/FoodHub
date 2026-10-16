package com.example.foodhub_app.feature.customer.menu.model

import kotlinx.serialization.Serializable

@Serializable
data class MenuCategoryDto(
    val id: String,
    val name: String,
    val icon: String?,
    val isActive: Boolean,
    val sortOrder: Int,
    val items: List<FoodDto>
)