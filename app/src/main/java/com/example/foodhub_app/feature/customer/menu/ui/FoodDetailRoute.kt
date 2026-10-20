package com.example.foodhub_app.feature.customer.menu.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodhub_app.feature.customer.menu.viewmodel.FoodDetailViewModel
import com.example.foodhub_app.feature.shared.ui.FoodHubAlertDialog
import com.example.foodhub_app.ui.theme.Brand
import com.example.foodhub_app.ui.theme.BrandDark

@Composable
fun FoodDetailRoute(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onCartClick: () -> Unit = {},
    onScanQrClick: () -> Unit = {},
    viewModel: FoodDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbar.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    val food = state.food
    if (food != null) {
        FoodDetailScreen(
            food = food,
            onBackClick = onBackClick,
            onAddToCart = viewModel::addToCart,
            reviews = state.reviews,
            onCartClick = onCartClick,
            isAdding = state.isAdding,
            snackbarHost = { SnackbarHost(snackbar) }
        )
    } else {
        Scaffold { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                IconButton(onClick = onBackClick, modifier = Modifier.padding(8.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                }
                if (state.isLoading) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center), color = Brand)
                } else {
                    FoodLoadError(
                        message = state.error ?: "Không tìm thấy món ăn.",
                        onRetry = viewModel::loadFood,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    if (state.requiresLogin) {
        FoodHubAlertDialog(
            onDismissRequest = viewModel::dismissLogin,
            title = { Text("Đăng nhập") },
            text = { Text("Bạn cần đăng nhập để thêm món vào giỏ hàng.") },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissLogin(); onLoginClick() }) {
                    Text("Đăng nhập", color = Brand)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissLogin) {
                    Text("Để sau")
                }
            }
        )
    }

    if (state.requiresTableScan) {
        FoodHubAlertDialog(
            onDismissRequest = viewModel::dismissTableScan,
            title = { Text("Phiên bàn đã hết hạn") },
            text = { Text("Vui lòng quét lại mã QR trên bàn để tiếp tục gọi món.") },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissTableScan(); onScanQrClick() }) {
                    Text("Quét lại QR", color = Brand)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissTableScan) {
                    Text("Để sau")
                }
            }
        )
    }
}

@Composable
internal fun FoodLoadError(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(message)
        Spacer(Modifier.height(12.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = BrandDark)) {
            Text("Thử lại")
        }
    }
}