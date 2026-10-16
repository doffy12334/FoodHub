package com.example.foodhub_app.feature.shared.auth.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
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
import com.example.foodhub_app.feature.shared.auth.viewmodel.AuthViewModel
import com.example.foodhub_app.feature.shared.auth.viewmodel.LoginUiState
import com.example.foodhub_app.ui.theme.AppBackground
import com.example.foodhub_app.ui.theme.Brand
import com.example.foodhub_app.ui.theme.CardStroke
import com.example.foodhub_app.ui.theme.InputBackground
import com.example.foodhub_app.ui.theme.InputBackgroundSoft
import com.example.foodhub_app.ui.theme.Neutral
import com.example.foodhub_app.ui.theme.OnSurfaceVariant

private val HeadingFont = FontFamily.Default
private val BodyFont = FontFamily.Default

@Composable
fun LoginRoute(
    onBackClick: () -> Unit,
    onLoginClick: (String?) -> Unit,
    onRegisterClick: () -> Unit,
    onGuestQrClick: () -> Unit,
    modifier: Modifier = Modifier,
    onForgotPasswordClick: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginClick(uiState.userRole)
        }
    }

    LoginScreen(
        uiState = uiState,
        onAccountChange = viewModel::onAccountChange,
        onPasswordChange = viewModel::onPasswordChange,
        onTogglePassword = viewModel::togglePasswordVisibility,
        onBackClick = onBackClick,
        onLoginClick = viewModel::login,
        onRegisterClick = onRegisterClick,
        onGuestQrClick = onGuestQrClick,
        onForgotPasswordClick = onForgotPasswordClick,
        modifier = modifier
    )
}
@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onAccountChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onGuestQrClick: () -> Unit,
    modifier: Modifier = Modifier,
    onForgotPasswordClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoginTopBar(onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp),
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
            Spacer(Modifier.height(18.dp))
            Text(
                text = "Đăng nhập",
                color = Neutral,
                fontFamily = HeadingFont,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
            Text(
                text = "Chào mừng bạn trở lại FoodHub",
                modifier = Modifier.padding(top = 6.dp),
                color = OnSurfaceVariant,
                fontFamily = BodyFont,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(28.dp))
            LoginForm(
                uiState = uiState,
                onAccountChange = onAccountChange,
                onPasswordChange = onPasswordChange,
                onTogglePassword = onTogglePassword,
                onLoginClick = onLoginClick,
                onForgotPasswordClick = onForgotPasswordClick
            )

            Spacer(Modifier.height(18.dp))
            AuthSwitchText(
                normalText = "Chưa có tài khoản?",
                actionText = "Đăng ký ngay",
                onClick = onRegisterClick
            )

            Spacer(Modifier.height(18.dp))
            GuestQrCard(onClick = onGuestQrClick)
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun LoginTopBar(onBackClick: () -> Unit) {
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
private fun LoginForm(
    uiState: LoginUiState,
    onAccountChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        AuthTextField(
            label = "Email",
            value = uiState.account,
            onValueChange = onAccountChange,
            iconRes = R.drawable.ic_auth_email,
            placeholder = "Nhập địa chỉ email",
            keyboardType = KeyboardType.Email
        )

        AuthTextField(
            label = "Mật khẩu",
            value = uiState.password,
            onValueChange = onPasswordChange,
            iconRes = R.drawable.ic_auth_lock,
            placeholder = "Nhập mật khẩu",
            isPassword = true,
            isPasswordVisible = uiState.isPasswordVisible,
            keyboardType = KeyboardType.Password,
            trailing = {
                PasswordToggleButton(
                    isVisible = uiState.isPasswordVisible,
                    onToggle = onTogglePassword
                )
            }
        )

        Text(
            text = "Quên mật khẩu?",
            modifier = Modifier
                .align(Alignment.End)
                .clickable(onClick = onForgotPasswordClick)
                .padding(vertical = 2.dp),
            color = Brand,
            fontFamily = BodyFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )

        uiState.errorMessage?.let { message ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFFFF1EE),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    color = Brand,
                    fontFamily = BodyFont,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }

        PrimaryAuthButton(
            text = if (uiState.isLoading) "Đang đăng nhập..." else "Đăng nhập",
            onClick = onLoginClick,
            enabled = !uiState.isLoading
        )
    }
}

@Composable
private fun AuthTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    iconRes: Int,
    placeholder: String,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    trailing: @Composable (() -> Unit)? = null
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
private fun GuestQrCard(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = InputBackgroundSoft,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, CardStroke)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(Color.White, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_onboarding_qr),
                    contentDescription = null,
                    tint = Brand,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Gọi món tại bàn",
                    color = Neutral,
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Không cần tài khoản · Quét mã QR",
                    color = OnSurfaceVariant,
                    fontFamily = BodyFont,
                    fontSize = 11.sp
                )
            }
            Text(
                text = "Quét QR",
                color = Brand,
                fontFamily = BodyFont,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
internal fun AuthSwitchText(
    normalText: String,
    actionText: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(normalText, color = OnSurfaceVariant, fontFamily = BodyFont, fontSize = 14.sp)
        Spacer(Modifier.size(4.dp))
        Text(
            text = actionText,
            modifier = Modifier.clickable(onClick = onClick),
            color = Brand,
            fontFamily = BodyFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        uiState = LoginUiState(),
        onAccountChange = {},
        onPasswordChange = {},
        onTogglePassword = {},
        onBackClick = {},
        onLoginClick = {},
        onRegisterClick = {},
        onGuestQrClick = {}
    )
}
