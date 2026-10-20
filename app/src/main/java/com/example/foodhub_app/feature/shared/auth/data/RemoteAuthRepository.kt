package com.example.foodhub_app.feature.shared.auth.data

import com.example.foodhub_app.core.datastore.TokenStore
import com.example.foodhub_app.feature.shared.auth.model.LoginRequest
import com.example.foodhub_app.feature.shared.auth.model.RefreshTokenRequest
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
                RegisterRequest(email, password, phone, name, confirmPassword = password))
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


    suspend fun checkSession(): Result<UserDto> {
        return runCatching {
            var accessToken = tokenStore.getAccessToken()
            val refreshToken = tokenStore.getRefreshToken()

            if (accessToken.isNullOrBlank()) {
                throw Exception("Chưa có token đăng nhập")
            }

            // 1. Thử gọi API lấy thông tin người dùng với accessToken hiện tại
            val userResponse = runCatching {
                authApi.getUser("Bearer $accessToken")
            }.getOrElse { error ->
                // 2. Nếu thất bại và có refreshToken -> Thử Refresh Token
                if (!refreshToken.isNullOrBlank()) {
                    val refreshResponse = authApi.refreshToken(RefreshTokenRequest(refreshToken))
                    val newAuthData = refreshResponse.data ?: throw error

                    // Lưu accessToken mới nhận được
                    accessToken = newAuthData.accessToken
                    val newRefreshToken = newAuthData.refreshToken ?: refreshToken
                    tokenStore.saveToken(
                        accessToken = newAuthData.accessToken,
                        refreshToken = newRefreshToken,
                        userDto = null
                    )

                    // Gọi lại API getUser bằng accessToken mới
                    authApi.getUser("Bearer $accessToken")
                } else {
                    throw error
                }
            }

            val userDto =
                userResponse.data ?: throw Exception("Không lấy được thông tin người dùng")

            // Lưu thông tin user mới nhất vào DataStore
            tokenStore.saveToken(
                accessToken = accessToken!!,
                refreshToken = tokenStore.getRefreshToken(),
                userDto = userDto
            )
            userDto
        }.recoverCatching { error ->
            // 3. Xử lý ngoại lệ Mất mạng: Nếu có cached user sẵn trong DataStore thì vẫn giữ trạng thái Logged In
            val isNetworkError = error is java.io.IOException
            val cachedUser = tokenStore.getUser()

            if (isNetworkError && cachedUser != null) {
                cachedUser
            } else {
                // 4. Lỗi xác thực/ token không dùng được -> Xoá session
                tokenStore.clearSession()
                throw error
            }
        }
    }
}
