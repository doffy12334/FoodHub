package com.example.foodhub_app.feature.shared.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_app.feature.shared.auth.data.RemoteAuthRepository
import com.example.foodhub_app.feature.shared.auth.model.UserDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val account: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val user: UserDto? = null
) {
    val isLoggedIn: Boolean get() = user != null
    val userRole: String? get() = user?.role
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: RemoteAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onAccountChange(newAccount: String) {
        _uiState.update { it.copy(account = newAccount, errorMessage = null) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun login() {
        val currentAccount = _uiState.value.account
        val currentPassword = _uiState.value.password

        if (currentAccount.isBlank() || currentPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập đầy đủ Email và Mật khẩu") }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.loginAndSaveSession(currentAccount, currentPassword)
                .onSuccess { userDto ->
                    _uiState.update { it.copy(isLoading = false, user = userDto) }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Đăng nhập thất bại"
                        )
                    }
                }
        }
    }
}
