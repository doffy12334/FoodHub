package com.example.foodhub_app.feature.shared.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val phoneNumber: String? = null,
    val email: String? = null,
    val role: String = "CUSTOMER",
    val dateOfBirth: String? = null,
    val avatarUrl: String? = null,
    val isActive: Boolean = true,
    val isVerified: Boolean = false
)