package com.example.foodhub_app.feature.shared.auth.data

import com.example.foodhub_app.core.datastore.TokenStore
import com.example.foodhub_app.core.network.ApiResponse
import com.example.foodhub_app.feature.shared.auth.model.AuthData
import com.example.foodhub_app.feature.shared.auth.model.LoginRequest
import com.example.foodhub_app.feature.shared.auth.model.RegisterRequest
import com.example.foodhub_app.feature.shared.auth.model.UserDto
import javax.inject.Inject

class RemoteAuthRepository @Inject constructor(
    private val authApi: AuthApi, private
    val tokenStore: TokenStore
) {

    suspend fun loginAndSaveSession(email: String, password: String): Result<UserDto> {
        return runCatching {
            //Gọi API đăng nhập -> lấy được access token và refresh token
            val loginResponse = authApi.login(LoginRequest(email, password))
            val authData = loginResponse.data ?: throw Exception("Không nhận được token")
            // Dùng access token để lấy thông tin user
            val userResponse = authApi.getUser("Bearer ${authData.accessToken}")
            val userDto = userResponse.data ?: throw Exception(
                "Không lấy được thông tin người " + "dùng")
            //Lưu access token, refresh token và thông tin user vào Datastore
            tokenStore.saveToken(
                accessToken = authData.accessToken,
                refreshToken = authData.refreshToken,
                userDto = userDto)
            // Trả về user
            userDto
        }
    }


    suspend fun registerAndSaveSession(
        email: String,
        password: String,
        name: String,
        phone: String? = null
    ): Result<UserDto> {
        return runCatching {
            //Gọi Api đăng ký , nhận về token
            val registerResponse = authApi.register(
                RegisterRequest(email, password, phone, name, confirmPassword = password ))
            val authData = registerResponse.data ?: throw Exception("Không nhận được token")
            //Dùng access token lấy thông tin user
            val userResponse = authApi.getUser("Bearer ${authData.accessToken}")
            val userDto = userResponse.data ?: throw Exception(
                "Không lấy được thông tin người dùng")
            //Lưu access token, refresh token và thông tin user vào Datastore
            tokenStore.saveToken(
                accessToken = authData.accessToken,
                refreshToken = authData.refreshToken,
                userDto = userDto)
            // Trả về user
            userDto
        }
    }
}
