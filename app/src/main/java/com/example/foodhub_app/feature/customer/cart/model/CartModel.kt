package com.example.foodhub_app.feature.customer.cart.model

import kotlinx.serialization.Serializable

@Serializable
data class FoodCartSelection(
    val menuItemId: String,
    val quantity: Int,
    val variantOptionIds: List<String> = emptyList(),
    val note: String = ""
)
