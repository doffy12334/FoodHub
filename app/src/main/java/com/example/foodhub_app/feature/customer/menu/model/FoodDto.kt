package com.example.foodhub_app.feature.customer.menu.model

import kotlinx.serialization.Serializable

//Nhan Json mon an tu server
@Serializable
data class FoodDto(
    val id: String,
    val name: String,
    val basePrice: String,
    val salePrice: String?,
    val salePercent: Double?,
    val saleEndsAt: String?,
    val image: String?,
    val totalOrder: Int,
    val isBestSeller: Boolean,
    val avgRating: String,
    val isAvailable: Boolean,
    val isFeatured: Boolean,
    val description: String?,
    val sortOrder: Int
)