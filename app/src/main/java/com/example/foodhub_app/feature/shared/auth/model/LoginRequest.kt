package com.example.foodhub_app.feature.shared.auth.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("email") val account: String,
    val password: String
)
