package com.example.foodhub_app.core.network

import com.example.foodhub_app.core.datastore.TokenStore
import com.example.foodhub_app.feature.shared.auth.data.AuthApi
import com.example.foodhub_app.feature.shared.auth.model.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

// Tự động chặn khi API trả về lỗi 401 để Refresh Token và thử lại Request
@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenStore: TokenStore,
    private val authApiProvider: Provider<AuthApi> // Dùng Provider tránh vòng lặp Dependency trong Hilt
) : Authenticator {

    // Hàm được OkHttp tự động gọi mỗi khi có API trả về mã lỗi 401 Unauthorized
    override fun authenticate(route: Route?, response: Response): Request? {

        // Giới hạn chỉ thử lại tối đa 1 lần để tránh vòng lặp vô hạn nếu refresh token cũng hỏng
        if (responseCount(response) >= 2) {
            return null
        }

        // Khóa đồng bộ để chỉ có 1 request thực hiện refresh token khi nhiều API bị lỗi 401 cùng lúc
        synchronized(this) {
            return runBlocking {
                val currentAccessToken = tokenStore.getAccessToken()
                val requestAccessToken = response.request.header("Authorization")
                    ?.removePrefix("Bearer")
                    ?.trim()

                // Nếu token trong DataStore đã được refresh bởi 1 request khác -> Dùng ngay token mới thử lại
                if (currentAccessToken != null && currentAccessToken != requestAccessToken) {
                    return@runBlocking response.request.newBuilder()
                        .header("Authorization", "Bearer $currentAccessToken")
                        .build()
                }

                // Lấy refreshToken từ DataStore
                val refreshToken = tokenStore.getRefreshToken()
                if (refreshToken.isNullOrBlank()) {
                    tokenStore.clearSession()
                    return@runBlocking null
                }

                try {
                    // Gọi API refresh-token để xin cấp accessToken mới
                    val authApi = authApiProvider.get()
                    val refreshResponse = authApi.refreshToken(RefreshTokenRequest(refreshToken))
                    val newAuthData = refreshResponse.data

                    if (newAuthData != null && newAuthData.accessToken.isNotBlank()) {
                        // Lưu accessToken mới vào DataStore
                        tokenStore.saveToken(
                            accessToken = newAuthData.accessToken,
                            refreshToken = newAuthData.refreshToken ?: refreshToken,
                            userDto = tokenStore.getUser()
                        )

                        // Thử lại Request cũ bằng accessToken MỚI vừa nhận
                        response.request.newBuilder()
                            .header("Authorization", "Bearer ${newAuthData.accessToken}")
                            .build()

                    } else {
                        // Refresh token không hợp lệ -> Xóa session bắt đăng nhập lại
                        tokenStore.clearSession()
                        null
                    }
                } catch (e: Exception) {
                    // Xử lý lỗi khi gọi API refresh-token thất bại
                    tokenStore.clearSession()
                    null
                }
            }
        }
    }
}

// Đếm số lần request đã tự động thử lại
private fun responseCount(response: Response): Int {
    var result = 1
    var prior = response.priorResponse
    while (prior != null) {
        result++
        prior = prior.priorResponse
    }
    return result
}
