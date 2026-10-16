package com.example.foodhub_app.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.foodhub_app.feature.shared.auth.model.UserDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private const val AUTH_DATA_STORE_NAME = "auth_session"

private val Context.authDatastore by preferencesDataStore(name = AUTH_DATA_STORE_NAME)

//@Inject constructor: Hilt được phép tạo TokenStore qua constructor.
//@ApplicationContext: yêu cầu Context của ứng dụng.
//@Singleton: các nơi inject TokenStore dùng chung một instance.
@Singleton
class TokenStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val accessTokenKey = stringPreferencesKey("access_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")
    private val userKey = stringPreferencesKey("user")

    private val json = Json { ignoreUnknownKeys = true }

    val user: Flow<UserDto?> = context.authDatastore.data.map { preferences ->
        preferences[userKey]?.let { value ->
            runCatching { json.decodeFromString<UserDto>(value) }.getOrNull()
        }
    }

    val accessToken: Flow<String?> = context.authDatastore.data.map { preferences ->
        preferences[accessTokenKey]
    }
    val refreshToken: Flow<String?> = context.authDatastore.data.map { preferences ->
        preferences[refreshTokenKey]
    }

    suspend fun saveToken(
        accessToken: String, refreshToken: String?, userDto: UserDto? = null
    ) {
        context.authDatastore.edit { preferences ->
            preferences[accessTokenKey] = accessToken
            if (userDto == null) {
                preferences.remove(userKey)
            } else {
                preferences[userKey] = json.encodeToString(UserDto.serializer(), userDto)
            }

            if (refreshToken.isNullOrBlank()) {
                preferences.remove(refreshTokenKey)
            } else {
                preferences[refreshTokenKey] = refreshToken
            }
        }
    }

    suspend fun getAccessToken(): String? = accessToken.first()
    suspend fun getRefreshToken(): String? = refreshToken.first()
    suspend fun getUser(): UserDto? = user.first()

    suspend fun clearSession() {
        context.authDatastore.edit { preferences ->
            preferences.remove(accessTokenKey)
            preferences.remove(refreshTokenKey)
            preferences.remove(userKey)
        }
    }
}
