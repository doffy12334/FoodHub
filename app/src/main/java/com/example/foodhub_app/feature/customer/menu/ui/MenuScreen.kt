package com.example.foodhub_app.feature.customer.menu.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.foodhub_app.R
import com.example.foodhub_app.feature.customer.menu.model.MenuCategory
import com.example.foodhub_app.feature.customer.menu.model.MenuFood
import com.example.foodhub_app.feature.customer.menu.viewmodel.MenuUiState
import com.example.foodhub_app.feature.customer.menu.viewmodel.MenuViewModel
import com.example.foodhub_app.ui.theme.AppBackground
import com.example.foodhub_app.ui.theme.Brand
import com.example.foodhub_app.ui.theme.CardStroke
import com.example.foodhub_app.ui.theme.Foodhub_AppTheme
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MenuRoute(
    onFoodClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: MenuViewModel = hiltViewModel(),
    onCartClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    MenuScreen(
        state = state,
        onFoodClick = onFoodClick,
        onBackClick = onBackClick,
        onCartClick = onCartClick,
        onRetryClick = viewModel::loadMenu)
}

@Composable
fun MenuScreen(
    state: MenuUiState,
    onFoodClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit = {},
    onRetryClick: () -> Unit = {}
) {
    Scaffold(containerColor = AppBackground) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(max = 840.dp)
                    .fillMaxWidth()) {
                // TopBar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại")
                    }
                    Text(
                        text = "Thực đơn",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = onCartClick) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Mở giỏ hàng")
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()) {
                    when {
                        state.isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center), color = Brand)
                        }

                        state.errMessage != null -> {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = state.errMessage,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = onRetryClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = Brand)) {
                                    Text("Tải lại")
                                }
                            }
                        }

                        state.categories.all { it.items.isEmpty() } -> {
                            Text(
                                text = "Chưa có món ăn.",
                                modifier = Modifier.align(Alignment.Center))
                        }

                        else -> {
                            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                                state.categories.forEach { category ->
                                    item(key = "category:${category.id}") {
                                        Text(
                                            text = category.name,
                                            modifier = Modifier.padding(vertical = 12.dp),
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleMedium)
                                    }
                                    items(
                                        category.items,
                                        key = { "${category.id}:${it.id}" }) { food ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable(enabled = food.available) {
                                                    onFoodClick(
                                                        food.id)
                                                }
                                                .padding(vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.size(80.dp)) {
                                                AsyncImage(
                                                    model = food.imageUrl
                                                        ?: R.drawable.ic_launcher_foreground,
                                                    contentDescription = food.name,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .alpha(if (food.available) 1f else 0.38f))
                                                if (!food.available) {
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .background(Color.Black.copy(alpha = 0.45f)),
                                                        contentAlignment = Alignment.Center) {
                                                        Text(
                                                            text = "TẠM HẾT",
                                                            color = Color.White,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(food.name, fontWeight = FontWeight.SemiBold)
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = NumberFormat.getIntegerInstance(
                                                        Locale.forLanguageTag(
                                                            "vi-VN"))
                                                        .format(food.price) + "đ", color = Brand)
                                                if (!food.available) {
                                                    Text(
                                                        text = "Món đang tạm hết hàng",
                                                        color = Brand,
                                                        style = MaterialTheme.typography.labelMedium,
                                                        fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                        HorizontalDivider(color = CardStroke)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun MenuScreenPreview() {
    Foodhub_AppTheme {
        MenuScreen(
            state = MenuUiState(
            categories = listOf(
                MenuCategory(
                    id = "cat1", name = "Món chính", items = listOf(
                        MenuFood(
                            id = "1",
                            name = "Cơm Tấm Sườn Bì Chả",
                            price = 45000,
                            available = true), MenuFood(
                            id = "2",
                            name = "Phở Bò Đặc Biệt",
                            price = 55000,
                            available = true), MenuFood(
                            id = "3",
                            name = "Bún Chả Hà Nội",
                            price = 50000,
                            available = false))), MenuCategory(
                    id = "cat2", name = "Đồ uống", items = listOf(
                        MenuFood(
                            id = "4",
                            name = "Trà Sữa Thái Xanh",
                            price = 25000,
                            available = true), MenuFood(
                            id = "5",
                            name = "Cà Phê Sữa Đá",
                            price = 20000,
                            available = true))))),
            onFoodClick = {},
            onBackClick = {},
            onCartClick = {},
            onRetryClick = {})
    }
}
