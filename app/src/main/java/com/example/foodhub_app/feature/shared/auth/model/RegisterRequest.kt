package com.example.foodhub_app.feature.shared.auth.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class RegisterRequest(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("phone") val phone: String?= null,
    @SerialName("name") val fullName: String,
    val confirmPassword : String
) {

}   