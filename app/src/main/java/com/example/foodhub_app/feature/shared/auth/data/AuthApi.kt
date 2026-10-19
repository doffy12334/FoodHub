package com.example.foodhub_app.feature.shared.auth.data

import com.example.foodhub_app.core.network.ApiResponse
import com.example.foodhub_app.feature.shared.auth.model.AuthData
import com.example.foodhub_app.feature.shared.auth.model.LoginRequest
import com.example.foodhub_app.feature.shared.auth.model.RefreshTokenRequest
import com.example.foodhub_app.feature.shared.auth.model.RegisterRequest
import com.example.foodhub_app.feature.shared.auth.model.UserDto
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @POST("user/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthData>

    @POST("user/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<AuthData>

    @GET("user/me")
    suspend fun getUser(
        @Header("Authorization") token: String
    ): ApiResponse<UserDto>

    @POST("user/refresh-token")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): ApiResponse<AuthData>
}
