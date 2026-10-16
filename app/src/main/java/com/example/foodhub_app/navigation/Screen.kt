package com.example.foodhub_app.navigation

import kotlinx.serialization.Serializable

// Quản lý Màn hình Type-Safe bằng Kotlin Serialization
sealed interface Screen {
    @Serializable
    data object Splash : Screen

    @Serializable
    data object Onboarding : Screen

    @Serializable
    data object Login : Screen

    @Serializable
    data object Register : Screen

    @Serializable
    data object ForgotPassword : Screen

    @Serializable
    data object QrScan : Screen

    @Serializable
    data object Home : Screen

    @Serializable
    data object Admin : Screen

    @Serializable
    data object Cart : Screen

    @Serializable
    data object Orders : Screen

    @Serializable
    data class FoodDetail(val foodId: String) : Screen

    @Serializable
    data class ResetPassword(val token: String? = null) : Screen
}
