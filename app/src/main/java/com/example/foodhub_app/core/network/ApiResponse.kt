package com.example.foodhub_app.core.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    @SerialName("message") val message: String,
    @SerialName("data") val data: T? = null
)
