package com.example.foodhub_app.feature.customer.menu.model

import kotlinx.serialization.Serializable

// Nhan Json mon an tu server
@Serializable
data class FoodDto(
    val id: String = "",
    val name: String = "",
    val basePrice: String = "0",
    val salePrice: String? = null,
    val salePercent: Double? = null,
    val saleEndsAt: String? = null,
    val image: String? = null,
    val totalOrder: Int = 0,
    val isBestSeller: Boolean = false,
    val avgRating: String = "5.0",
    val isAvailable: Boolean = true,
    val isFeatured: Boolean = false,
    val description: String? = null,
    val sortOrder: Int = 0,
    val variantGroups: List<FoodVariantGroupDto> = emptyList()
)

@Serializable
data class FoodVariantGroupDto(
    val id: String,
    val name: String,
    val type: String,
    val isRequired: Boolean = false,
    val options: List<FoodVariantOptionDto> = emptyList()
)

@Serializable
data class FoodVariantOptionDto(
    val id: String,
    val name: String,
    val priceAdd: String = "0",
    val isActive: Boolean = true
)
