package com.example.foodhub_app.feature.shared.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodhub_app.R
import com.example.foodhub_app.feature.shared.auth.viewmodel.PasswordStrength
import com.example.foodhub_app.feature.shared.auth.viewmodel.RegisterUiState
import com.example.foodhub_app.feature.shared.auth.viewmodel.RegisterViewModel
import com.example.foodhub_app.ui.theme.AppBackground
import com.example.foodhub_app.ui.theme.Brand
import com.example.foodhub_app.ui.theme.CardStroke
import com.example.foodhub_app.ui.theme.Foodhub_AppTheme
import com.example.foodhub_app.ui.theme.InputBackground
import com.example.foodhub_app.ui.theme.Neutral
import com.example.foodhub_app.ui.theme.OnSurfaceVariant
import com.example.foodhub_app.ui.theme.SuccessDark
import com.example.foodhub_app.ui.theme.Warning

private val HeadingFont = FontFamily.Default
private val BodyFont = FontFamily.Default

@Composable
fun RegisterRoute(
    onBackClick: () -> Unit,
    onRegisterSuccess: (String?) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isRegistered) {
        if (uiState.isRegistered) {
            onRegisterSuccess(uiState.userRole)
        }
    }

    RegisterScreen(
        uiState = uiState,
        onFullNameChange = viewModel::onFullNameChange,
        onPhoneNumberChange = viewModel::onPhoneNumberChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onTogglePassword = viewModel::togglePasswordVisibility,
        onToggleTerms = viewModel::toggleTerms,
        onBackClick = onBackClick,
        onRegisterClick = viewModel::register,
        onLoginClick = onLoginClick,
        modifier = modifier
    )
}

@Composable
fun RegisterScreen(
    uiState: RegisterUiState,
    onFullNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onToggleTerms: () -> Unit,
    onBackClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .background(AppBackground),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AuthHeaderBar(onBackClick = onBackClick)
        Column(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(72.dp),
                color = Color.White,
                shape = RoundedCornerShape(20.dp),
                shadowElevation = 4.dp
            ) {
                Image(
                    painter = painterResource(R.drawable.foodhub_logo),
                    contentDescription = "FoodHub Logo",
                    modifier = Modifier.padding(4.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Tạo tài khoản",
                color = Neutral,
                fontFamily = HeadingFont,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            )
            Text(
                text = "Đăng ký để trải nghiệm dịch vụ tuyệt vời cùng FoodHub",
                color = OnSurfaceVariant,
                fontFamily = BodyFont,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            RegisterFormCard(
                uiState = uiState,
                onFullNameChange = onFullNameChange,
                onPhoneNumberChange = onPhoneNumberChange,
                onEmailChange = onEmailChange,
                onPasswordChange = onPasswordChange,
                onTogglePassword = onTogglePassword,
                onToggleTerms = onToggleTerms,
                onRegisterClick = onRegisterClick
            )
            Spacer(modifier = Modifier.height(16.dp))
            AuthSwitchText(
                normalText = "Đã có tài khoản?",
                actionText = "Đăng nhập ngay",
                onClick = onLoginClick
            )
        }
    }
}

@Composable
private fun AuthHeaderBar(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Surface(
            modifier = Modifier
                .size(40.dp)
                .clickable(onClick = onBackClick),
            color = Color.White,
            shape = CircleShape,
            shadowElevation = 3.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(R.drawable.ic_auth_back),
                    contentDescription = "Quay lại",
                    tint = Neutral,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun RegisterFormCard(
    uiState: RegisterUiState,
    onFullNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onToggleTerms: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AuthRegisterTextField(
                label = "Họ và tên",
                placeholder = "Nhập họ và tên",
                value = uiState.fullName,
                onValueChange = onFullNameChange,
                iconRes = R.drawable.ic_auth_person
            )
            AuthRegisterTextField(
                label = "Số điện thoại",
                placeholder = "Nhập số điện thoại",
                value = uiState.phoneNumber,
                onValueChange = onPhoneNumberChange,
                iconRes = R.drawable.ic_profile_phone,
                keyboardType = KeyboardType.Phone
            )
            AuthRegisterTextField(
                label = "Email",
                value = uiState.email,
                onValueChange = onEmailChange,
                iconRes = R.drawable.ic_auth_email,
                placeholder = "Email đăng nhập",
                keyboardType = KeyboardType.Email
            )
            AuthRegisterTextField(
                label = "Mật khẩu",
                value = uiState.password,
                onValueChange = onPasswordChange,
                iconRes = R.drawable.ic_auth_lock,
                isPassword = true,
                placeholder = "Tối thiểu 8 ký tự",
                isPasswordVisible = uiState.isPasswordVisible,
                keyboardType = KeyboardType.Password,
                trailing = {
                    PasswordToggleButton(
                        isVisible = uiState.isPasswordVisible,
                        onToggle = onTogglePassword
                    )
                },
                helper = {
                    PasswordStrengthMeter(strength = uiState.passwordStrength)
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleTerms() },
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (uiState.acceptedTerms) Brand else Color.Transparent)
                        .border(
                            width = 1.dp,
                            color = if (uiState.acceptedTerms) Brand else CardStroke,
                            shape = RoundedCornerShape(4.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.acceptedTerms) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_auth_check_circle),
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    }
                }
                TermsText()
            }
            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = Brand,
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
            PrimaryAuthButton(
                text = if (uiState.isLoading) "Đang đăng ký..." else "Đăng ký",
                onClick = onRegisterClick,
                enabled = !uiState.isLoading
            )
        }
    }
}

@Composable
private fun AuthRegisterTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    iconRes: Int,
    placeholder: String,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    trailing: @Composable (() -> Unit)? = null,
    helper: @Composable (() -> Unit)? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            color = Neutral,
            fontFamily = BodyFont,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = OnSurfaceVariant.copy(alpha = 0.6f),
                    fontFamily = BodyFont,
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = Brand,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = trailing,
            singleLine = true,
            visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InputBackground,
                unfocusedContainerColor = InputBackground,
                focusedBorderColor = Brand,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Neutral,
                unfocusedTextColor = Neutral
            ),
            modifier = Modifier.fillMaxWidth()
        )
        helper?.invoke()
    }
}

@Composable
private fun PasswordToggleButton(isVisible: Boolean, onToggle: () -> Unit) {
    Icon(
        painter = painterResource(if (isVisible) R.drawable.ic_auth_eye_off else R.drawable.ic_auth_eye),
        contentDescription = if (isVisible) "Ẩn mật khẩu" else "Hiện mật khẩu",
        tint = OnSurfaceVariant,
        modifier = Modifier
            .size(22.dp)
            .clickable(onClick = onToggle)
    )
}

@Composable
private fun PrimaryAuthButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Brand,
            contentColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        if (!enabled) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                fontFamily = BodyFont,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun TermsText() {
    Text(
        text = "Tôi đồng ý với Điều khoản dịch vụ & Chính sách bảo mật của FoodHub",
        color = OnSurfaceVariant,
        fontFamily = BodyFont,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
}

@Composable
private fun PasswordStrengthMeter(
    strength: PasswordStrength
) {
    val activeColor = when (strength) {
        PasswordStrength.EMPTY -> CardStroke
        PasswordStrength.WEAK -> Brand
        PasswordStrength.MEDIUM -> Warning
        PasswordStrength.STRONG -> SuccessDark
    }

    val label = when (strength) {
        PasswordStrength.EMPTY -> "Chưa nhập mật khẩu"
        PasswordStrength.WEAK -> "Yếu"
        PasswordStrength.MEDIUM -> "Trung bình"
        PasswordStrength.STRONG -> "Rất mạnh"
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(3) { index ->
                val isActive = index < strength.level

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(
                            color = if (isActive) activeColor else CardStroke,
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Độ bảo mật:",
                color = OnSurfaceVariant,
                fontFamily = BodyFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                lineHeight = 14.sp
            )

            Text(
                text = label,
                color = activeColor,
                fontFamily = BodyFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                lineHeight = 14.sp
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 948)
@Composable
private fun RegisterScreenPreview() {
    Foodhub_AppTheme {
        RegisterScreen(
            uiState = RegisterUiState(),
            onFullNameChange = {},
            onPhoneNumberChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onTogglePassword = {},
            onToggleTerms = {},
            onBackClick = {},
            onRegisterClick = {},
            onLoginClick = {}
        )
    }
}
