package com.example.foodhub_app.feature.shared.auth.viewmodel

import android.util.Patterns
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

enum class PasswordStrength(val level: Int) {
    EMPTY(0), WEAK(1), MEDIUM(2), STRONG(3)
}

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val phone: String = "",
    val acceptedTerms: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val user: UserDto? = null
) {
    val fullName: String get() = name
    val phoneNumber: String get() = phone

    val passwordStrength: PasswordStrength
        get() {
            if (password.isEmpty()) {
                return PasswordStrength.EMPTY
            }
            if (password.length < 8) {
                return PasswordStrength.WEAK
            }
            val hasLowercase = password.any { it.isLowerCase() }
            val hasUppercase = password.any { it.isUpperCase() }
            val hasNumber = password.any { it.isDigit() }
            val hasSpecialChar = password.any { !it.isLetterOrDigit() && !it.isWhitespace() }
            val characterType = listOf(
                hasLowercase, hasUppercase, hasNumber, hasSpecialChar
            ).count { it }
            return when {
                
                characterType >= 3 -> PasswordStrength.STRONG
                characterType >= 2 -> PasswordStrength.MEDIUM
                else -> PasswordStrength.WEAK
            }
        }

    val isRegistered: Boolean get() = user != null
    val userRole: String? get() = user?.role
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: RemoteAuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onChangeName(newName: String) {
        _uiState.update { it.copy(name = newName, errorMessage = null) }
    }

    fun onFullNameChange(newName: String) {
        onChangeName(newName)
    }

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, errorMessage = null) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, errorMessage = null) }
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = newConfirmPassword, errorMessage = null) }
    }

    fun onPhoneChange(newPhone: String) {
        _uiState.update { it.copy(phone = newPhone, errorMessage = null) }
    }

    fun onPhoneNumberChange(newPhone: String) {
        onPhoneChange(newPhone)
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleTerms() {
        _uiState.update { it.copy(acceptedTerms = !it.acceptedTerms) }
    }

    fun register() {
        val state = _uiState.value

        // 1. Kiểm tra trường rỗng
        if (state.email.isBlank() || state.password.isBlank()
            || state.phone.isBlank() || state.name.isBlank()
        ) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập đầy đủ thông tin") }
            return
        }

        // 2. Kiểm tra định dạng Email
        if (!Patterns.EMAIL_ADDRESS.matcher(state.email.trim())
                .matches()) {
            _uiState.update { it.copy(errorMessage = "Email không đúng định dạng") }
            return
        }

        // 3. Kiểm tra độ dài mật khẩu
        if (state.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Mật khẩu phải chứa ít nhất 6 ký tự") }
            return
        }

        // 4. Kiểm tra điều khoản
        if (!state.acceptedTerms) {
            _uiState.update { it.copy(errorMessage = "Vui lòng đồng ý với Điều khoản & Điều kiện") }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.registerAndSaveSession(
                name = state.name.trim(),
                email = state.email.trim(),
                password = state.password,
                phone = state.phone.trim()
                    .ifBlank { null }
            )
                .onSuccess { userDto ->
                    _uiState.update { it.copy(isLoading = false, user = userDto) }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Đăng ký thất bại"
                        )
                    }
                }
        }
    }
}
