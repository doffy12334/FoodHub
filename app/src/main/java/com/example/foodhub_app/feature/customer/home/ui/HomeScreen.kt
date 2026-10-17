package com.example.foodhub_app.feature.customer.home.ui

import com.example.foodhub_app.core.ui.icons.TablerIcons

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodhub_app.R
import com.example.foodhub_app.feature.shared.ui.OrderModeDialog
import com.example.foodhub_app.feature.shared.ui.LoginRequiredDialog

import com.example.foodhub_app.feature.customer.home.viewmodel.HomeUiState
import com.example.foodhub_app.feature.customer.home.viewmodel.HomeViewModel
import com.example.foodhub_app.feature.customer.menu.model.MenuFood

import com.example.foodhub_app.ui.theme.AppBackground
import com.example.foodhub_app.ui.theme.Brand
import com.example.foodhub_app.ui.theme.BrandDark
import com.example.foodhub_app.ui.theme.BrandSoft
import com.example.foodhub_app.ui.theme.InputBackground
import com.example.foodhub_app.ui.theme.Neutral
import com.example.foodhub_app.ui.theme.OnSurfaceVariant
import com.example.foodhub_app.ui.theme.SoftGreen
import com.example.foodhub_app.ui.theme.SuccessDark
import com.example.foodhub_app.ui.theme.SuccessSoft
import com.example.foodhub_app.ui.theme.WarmAccent
import com.example.foodhub_app.ui.theme.Warning
import com.example.foodhub_app.ui.theme.WarningDark
import java.text.NumberFormat
import java.util.Locale

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import coil.compose.AsyncImage
import com.example.foodhub_app.feature.customer.order.model.OrderType
import com.example.foodhub_app.feature.customer.home.viewmodel.HomeTableSession
import com.example.foodhub_app.ui.theme.HomeLegacyTheme
import com.example.foodhub_app.core.network.resolveMediaUrl
import androidx.compose.ui.tooling.preview.Preview
import com.example.foodhub_app.feature.customer.menu.model.MenuCategory

// Màn hình Route chính kết nối HomeViewModel và xử lý luồng sự kiện
@Composable
fun HomeRoute(
    onFoodClick: (String) -> Unit,
    onMenuClick: () -> Unit,
    onCartClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onProfileClick: () -> Unit,
    onQrClick: () -> Unit,
    onLoginClick: () -> Unit,
    onChatClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        viewModel.loadHomeData()
        onPauseOrDispose {}
    }
    HomeScreen(
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onMaxPriceChange = viewModel::selectMaxPrice,
        onCategoryClick = viewModel::selectCategory,
        onRetry = viewModel::loadHomeData,
        onFoodClick = onFoodClick,
        onMenuClick = onMenuClick,
        onCartClick = onCartClick,
        onOrdersClick = onOrdersClick,
        onProfileClick = if (state.isLoggedIn) onProfileClick else onLoginClick,
        onQrClick = onQrClick,
        onDeliveryClick = {
            if (state.isLoggedIn) {
                viewModel.selectOrderType(OrderType.DELIVERY)
                onMenuClick()
            } else onLoginClick()
        },
        onOrderTypeSelected = {
            when {
                it == OrderType.DINE_IN && state.tableSession == null -> onQrClick()
                it == OrderType.DELIVERY && !state.isLoggedIn -> onLoginClick()
                else -> viewModel.selectOrderType(it)
            }
        },
        onChatClick = onChatClick,
        onNotificationsClick = onNotificationsClick,
        onEndTableSession = viewModel::clearTableInfo,
    )
}

// Composable bọc theme legacy và chuyển tiếp dữ liệu xuống HomeContent
@Composable
fun HomeScreen(
    state: HomeUiState,
    onQueryChange: (String) -> Unit,
    onCategoryClick: (String?) -> Unit,
    onRetry: () -> Unit,
    onFoodClick: (String) -> Unit,
    onMenuClick: () -> Unit,
    onCartClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onProfileClick: () -> Unit,
    onQrClick: () -> Unit,
    onMaxPriceChange: (Long?) -> Unit = {},
    onDeliveryClick: () -> Unit = {},
    onOrderTypeSelected: (OrderType) -> Unit = {},
    onChatClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onEndTableSession: () -> Unit = {},
) {
    var showLoginRequirementDialog by remember { mutableStateOf(false) }

    HomeLegacyTheme {
        HomeContent(
            state, onQueryChange, onMaxPriceChange, onCategoryClick, onRetry,
            onFoodClick, onMenuClick, onCartClick,
            onOrdersClick = {
                if (state.isGuest) showLoginRequirementDialog = true else onOrdersClick()
            },
            onProfileClick = {
                if (state.isGuest) showLoginRequirementDialog = true else onProfileClick()
            },
            onQrClick,
            onDeliveryClick = {
                if (state.isGuest) showLoginRequirementDialog = true else onDeliveryClick()
            },
            state.tableSession, state.selectedOrderType,
            onOrderTypeSelected = { type ->
                if (type == OrderType.DELIVERY && state.isGuest) {
                    showLoginRequirementDialog = true
                } else {
                    onOrderTypeSelected(type)
                }
            },
            onChatClick = {
                if (state.isGuest) showLoginRequirementDialog = true else onChatClick()
            },
            onNotificationsClick = {
                if (state.isGuest) showLoginRequirementDialog = true else onNotificationsClick()
            },
            state.unreadNotificationCount, onEndTableSession,
        )

        if (showLoginRequirementDialog) {
            LoginRequiredDialog(
                onDismiss = { showLoginRequirementDialog = false },
                onLoginClick = onProfileClick,
            )
        }
    }
}

// Bố cục chính chứa danh sách các phần giao diện trang chủ
@Composable
private fun HomeContent(
    state: HomeUiState,
    onQueryChange: (String) -> Unit,
    onMaxPriceChange: (Long?) -> Unit = {},
    onCategoryClick: (String?) -> Unit,
    onRetry: () -> Unit,
    onFoodClick: (String) -> Unit,
    onMenuClick: () -> Unit,
    onCartClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onProfileClick: () -> Unit,
    onQrClick: () -> Unit,
    onDeliveryClick: () -> Unit = {},
    tableSession: HomeTableSession? = null,
    selectedOrderType: OrderType = OrderType.TAKEAWAY,
    onOrderTypeSelected: (OrderType) -> Unit = {},
    onChatClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    notificationUnreadCount: Int = 0,
    onEndTableSession: () -> Unit = {},
) {
    Scaffold(
        containerColor = AppBackground,
        topBar = {
            HomeHeader(
                state.cartItemCount,
                onCartClick,
                onProfileClick,
                onQrClick,
                tableSession,
                selectedOrderType)
        },
        bottomBar = {
            val canChat =
                tableSession != null || (state.isLoggedIn && selectedOrderType != OrderType.DINE_IN)
            HomeBottomBar(
                onOrdersClick = onOrdersClick,
                onProfileClick = onProfileClick,
                canChat = canChat,
                onChatClick = onChatClick,
                onNotificationsClick = onNotificationsClick,
                notificationUnreadCount = notificationUnreadCount,
            )
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)) {
            if (state.isLoading && state.categories.isEmpty()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center), color = Brand)
            } else if (state.errorMessage != null && state.categories.isEmpty()) {
                HomeError(state.errorMessage, onRetry, Modifier.align(Alignment.Center))
            } else {
                val visible = state.visibleFoods
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        OrderingContext(
                            selectedType = selectedOrderType,
                            tableSession = tableSession,
                            isLoggedIn = state.isLoggedIn,
                            onSelect = onOrderTypeSelected,
                            onEndTableSession = onEndTableSession,
                        )
                    }
                    item { SearchRow(state.query, onQueryChange, state.maxPrice, onMaxPriceChange) }
                    item { DiscoveryBanner(onMenuClick) }
                    item { QuickActions(onMenuClick, onQrClick, onDeliveryClick) }
                    item {
                        SectionTitle(
                            "Danh Mục Món Ăn",
                            "Tất cả (${state.categories.sumOf { it.items.size }})",
                            onMenuClick)
                        Spacer(Modifier.height(10.dp))
                        CategoryRow(state, onCategoryClick)
                    }
                    if (state.query.isBlank() && state.selectedCategoryId == null && state.popularFoods.isNotEmpty()) {
                        item {
                            SectionTitle("Món Bán Chạy Hôm Nay", "Xem thêm", onMenuClick)
                            Spacer(Modifier.height(10.dp))
                            PopularRow(state.popularFoods, onFoodClick)
                        }
                    }
                    item {
                        val title =
                            if (state.query.isNotBlank()) "Kết Quả Tìm Kiếm" else "Gợi Ý Dành Cho Bạn"
                        Text(title, color = Neutral, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    if (visible.isEmpty()) {
                        item { Text("Không tìm thấy món phù hợp.", color = OnSurfaceVariant) }
                    } else {
                        items(visible.take(8), key = { "home:${it.id}" }) { food ->
                            RecommendationCard(food, fallbackFor(food, visible), onFoodClick)
                        }
                    }
                    item {
                        ServiceBar(
                            canChat = tableSession != null || (state.isLoggedIn && selectedOrderType != OrderType.DINE_IN),
                            isDineIn = selectedOrderType == OrderType.DINE_IN,
                            onClick = onChatClick,
                        )
                    }
                }
            }
        }
    }
}

// Thanh tiêu đề hiển thị tên app, thông tin bàn/hình thức đặt món và các nút giỏ hàng, tài khoản
@Composable
private fun HomeHeader(
    cartCount: Int,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onQrClick: () -> Unit,
    tableSession: HomeTableSession?,
    selectedType: OrderType,
) {
    Surface(color = BrandDark, contentColor = Color.White, shadowElevation = 4.dp) {
        Row(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("FoodHub", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 21.sp)
                Text(
                    tableSession?.let {
                        "${it.tableName}${
                            it.floor?.let { floor -> " · $floor" }
                                .orEmpty()
                        }"
                    }
                        ?: when (selectedType) {
                            OrderType.DELIVERY -> "Giao tận nơi"
                            OrderType.TAKEAWAY -> "Khách mang về"
                            OrderType.DINE_IN -> "Dùng tại bàn"
                        },
                    color = Color.White.copy(alpha = .78f),
                    fontSize = 10.sp,
                    maxLines = 1,
                )
            }
            HeaderButton("Quét mã QR", onQrClick) {
                Icon(TablerIcons.QrScan, null, Modifier.size(20.dp))
            }
            Spacer(Modifier.width(6.dp))
            Box {
                HeaderButton("Giỏ hàng", onCartClick) {
                    Icon(TablerIcons.ShoppingCart, null, Modifier.size(20.dp))
                }
                if (cartCount > 0) Badge(
                    Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 1.dp, y = (-1).dp),
                    containerColor = Brand
                ) {
                    Text(
                        cartCount.coerceAtMost(99)
                            .toString())
                }
            }
            Spacer(Modifier.width(6.dp))
            IconButton(onClick = onProfileClick, modifier = Modifier.size(44.dp)) {
                Image(
                    painterResource(R.drawable.avatar_user),
                    "Tài khoản",
                    Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

// Nút bấm hình tròn dùng trên thanh tiêu đề
@Composable
private fun HeaderButton(description: String, onClick: () -> Unit, icon: @Composable () -> Unit) {
    Surface(
        shape = CircleShape,
        color = Color.White.copy(alpha = .15f),
        contentColor = Color.White) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(44.dp)
                .semantics { contentDescription = description }
        ) {
            Box(Modifier.size(22.dp), contentAlignment = Alignment.Center) { icon() }
        }
    }
}

// Thẻ hiển thị hình thức nhận món hiện tại và dialog cho phép chọn lại
@Composable
private fun OrderingContext(
    selectedType: OrderType,
    tableSession: HomeTableSession?,
    isLoggedIn: Boolean,
    onSelect: (OrderType) -> Unit,
    onEndTableSession: () -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    val title = when (selectedType) {
        OrderType.DINE_IN -> "Ăn tại ${tableSession?.tableName ?: "bàn"}"
        OrderType.TAKEAWAY -> "Đặt món mang về"
        OrderType.DELIVERY -> "Giao tận nơi"
    }
    val detail = when (selectedType) {
        OrderType.DINE_IN -> listOfNotNull(
            tableSession?.floor,
            tableSession?.capacity?.let { "$it chỗ" }).joinToString(" · ")
            .ifBlank { "Phiên bàn đang hoạt động" }

        OrderType.TAKEAWAY -> if (isLoggedIn) "Nhận tại quầy" else "Khách vãng lai · Không cần đăng nhập"
        OrderType.DELIVERY -> "Giao đến địa chỉ của bạn"
    }
    Surface(shape = RoundedCornerShape(18.dp), color = Color.White, shadowElevation = 2.dp) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { showDialog = true }
                .padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(32.dp)
                    .background(SuccessSoft, CircleShape), contentAlignment = Alignment.Center) {
                Icon(
                    when (selectedType) {
                        OrderType.DINE_IN -> TablerIcons.Table
                        OrderType.DELIVERY -> TablerIcons.Delivery
                        OrderType.TAKEAWAY -> TablerIcons.Takeaway
                    },
                    null, Modifier.size(18.dp), tint = SuccessDark,
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(detail, color = OnSurfaceVariant, fontSize = 11.sp, maxLines = 1)
            }
            Surface(shape = CircleShape, color = SuccessSoft) {
                Text(
                    "Thay đổi",
                    Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = SuccessDark,
                    fontSize = 10.sp)
            }
        }
    }
    if (showDialog) {
        OrderModeDialog(
            selectedType = selectedType,
            tableSession = tableSession,
            isLoggedIn = isLoggedIn,
            onSelect = onSelect,
            onEndTableSession = onEndTableSession,
            onDismiss = { showDialog = false },
        )
    }
}

// Thẻ lựa chọn một hình thức nhận món trong dialog


// Ô tìm kiếm món ăn kết hợp menu lọc theo mức giá tối đa
@Composable
private fun SearchRow(
    query: String,
    onQueryChange: (String) -> Unit,
    maxPrice: Long?,
    onMaxPriceChange: (Long?) -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 52.dp),
            placeholder = {
                Text(
                    "Tìm món ngon, đồ uống, tráng miệng...",
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = { Icon(TablerIcons.Search, null, Modifier.size(20.dp)) },
            trailingIcon = {
                if (query.isNotEmpty()) IconButton(onClick = { onQueryChange("") }) {
                    Icon(TablerIcons.Close, "Xóa tìm kiếm", Modifier.size(18.dp))
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Brand,
                unfocusedBorderColor = Color.Transparent
            )
        )
        Box {
            FilledIconButton(
                onClick = { menuExpanded = true },
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (maxPrice == null) BrandDark else Brand
                )
            ) { Icon(TablerIcons.Adjustments, "Lọc theo giá", tint = Color.White) }
            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                listOf(
                    "Tất cả mức giá" to null,
                    "Tối đa 50.000đ" to 50_000L,
                    "Tối đa 100.000đ" to 100_000L,
                    "Tối đa 200.000đ" to 200_000L,
                ).forEach { (label, value) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = { onMaxPriceChange(value); menuExpanded = false },
                        leadingIcon = {
                            if (maxPrice == value) Icon(TablerIcons.Check, null)
                        },
                    )
                }
            }
        }
    }
}

// Banner quảng cáo khuyến mãi thực đơn mới
@Composable
private fun DiscoveryBanner(onMenuClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = BrandDark,
        shadowElevation = 4.dp
    ) {
        Row(
            Modifier
                .heightIn(min = 156.dp)
                .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(shape = CircleShape, color = Warning) {
                    Text(
                        "THỰC ĐƠN HÔM NAY",
                        Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        color = WarningDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold)
                }
                Text(
                    "Khám phá món ngon\ntừ FoodHub",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 21.sp,
                    lineHeight = 26.sp)
                Text(
                    "Danh mục và giá được cập nhật trực tiếp từ hệ thống.",
                    color = Color.White.copy(alpha = .9f),
                    fontSize = 11.sp,
                    lineHeight = 15.sp)
                TextButton(onClick = onMenuClick, contentPadding = PaddingValues(0.dp)) {
                    Text("Xem thực đơn", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Image(
                painterResource(R.drawable.home_figma_02),
                "Burger FoodHub",
                Modifier
                    .size(108.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

// Nhóm các nút tác vụ nhanh (Quét QR Bàn, Đặt Mang Về, Giao Tận Nơi)
@Composable
private fun QuickActions(
    onMenuClick: () -> Unit,
    onQrClick: () -> Unit,
    onDeliveryClick: () -> Unit,
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        QuickAction("Quét QR\nBàn", BrandSoft, onQrClick, Modifier.weight(1f)) {
            Icon(TablerIcons.QrScan, null, tint = Brand)
        }
        QuickAction("Đặt Mang\nVề", WarmAccent, onMenuClick, Modifier.weight(1f)) {
            Icon(TablerIcons.Takeaway, null, tint = WarningDark)
        }
        QuickAction("Giao Tận\nNơi", SoftGreen, onDeliveryClick, Modifier.weight(1f)) {
            Icon(TablerIcons.Delivery, null, tint = SuccessDark)
        }
    }
}

// Nút tác vụ nhanh đơn lẻ với icon và nhãn mô tả
@Composable
private fun QuickAction(
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    Surface(
        modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp) {
        Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier
                    .size(48.dp)
                    .background(color, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center) {
                Box(Modifier.size(22.dp), contentAlignment = Alignment.Center) { icon() }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                label,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

// Thanh lướt ngang danh mục món ăn cho phép lọc theo loại
@Composable
private fun CategoryRow(state: HomeUiState, onCategoryClick: (String?) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            FilterChip(
                selected = state.selectedCategoryId == null,
                onClick = { onCategoryClick(null) },
                label = { Text("Tất cả") },
                leadingIcon = { Icon(TablerIcons.Restaurant, null, Modifier.size(16.dp)) },
                colors = homeFilterChipColors()
            )
        }
        items(state.categories, key = { it.id }) { category ->
            FilterChip(
                selected = state.selectedCategoryId == category.id,
                onClick = { onCategoryClick(category.id) },
                label = { Text(category.name, maxLines = 1) },
                colors = homeFilterChipColors()
            )
        }
    }
}

// Tiêu đề của từng phân đoạn kèm nút thao tác xem thêm
@Composable
private fun SectionTitle(title: String, action: String, onAction: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(7.dp)
                .background(Brand, CircleShape))
        Spacer(Modifier.width(5.dp))
        Text(
            title,
            Modifier.weight(1f),
            color = Neutral,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp)
        Text(
            action,
            Modifier.clickable(onClick = onAction),
            color = Brand,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold)
    }
}

// Danh sách lướt ngang hiển thị các món ăn bán chạy nhất
@Composable
private fun PopularRow(foods: List<MenuFood>, onFoodClick: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(foods, key = { "popular:${it.id}" }) { food ->
            Surface(
                modifier = Modifier
                    .width(174.dp)
                    .clickable(enabled = food.available) { onFoodClick(food.id) },
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Column {
                    Box {
                        AvailabilityImage(
                            food,
                            fallbackFor(food, foods),
                            Modifier
                                .fillMaxWidth()
                                .height(116.dp))
                        FoodBadgesOverlay(
                            food,
                            Modifier
                                .align(Alignment.TopEnd)
                                .padding(7.dp))
                    }
                    Column(
                        Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(
                            food.name,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            minLines = 2)
                        if (food.rating != "0") Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(TablerIcons.Star, null, Modifier.size(14.dp), tint = Warning)
                            Text(food.rating, fontSize = 11.sp, color = OnSurfaceVariant)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    money(food.price),
                                    color = Brand,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp)
                                if (food.salePercent != null) Text(
                                    money(food.basePrice),
                                    color = OnSurfaceVariant,
                                    fontSize = 10.sp,
                                    style = MaterialTheme.typography.bodySmall.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough),
                                )
                            }
                            if (food.available) Surface(shape = CircleShape, color = BrandSoft) {
                                Icon(
                                    TablerIcons.Add,
                                    "Xem ${food.name}",
                                    Modifier
                                        .padding(7.dp)
                                        .size(16.dp),
                                    tint = Brand)
                            } else Text(
                                "Tạm hết",
                                color = Brand,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// Thẻ hiển thị thông tin món ăn gợi ý kèm giá và trạng thái còn/hết hàng
@Composable
private fun RecommendationCard(
    food: MenuFood,
    @DrawableRes fallback: Int,
    onFoodClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = food.available) { onFoodClick(food.id) },
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box {
                AvailabilityImage(
                    food,
                    fallback,
                    Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(8.dp)))
                FoodBadgesOverlay(
                    food,
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(5.dp))
            }
            Column(
                Modifier
                    .weight(1f)
                    .height(96.dp), verticalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        food.name,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis)
                    if (food.description.isNotBlank()) Text(
                        food.description,
                        color = OnSurfaceVariant,
                        fontSize = 11.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            money(food.price),
                            color = Brand,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp)
                        if (food.salePercent != null) Text(
                            money(food.basePrice), color = OnSurfaceVariant, fontSize = 10.sp,
                            style = MaterialTheme.typography.bodySmall.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough),
                        )
                    }
                    if (food.available) Surface(shape = CircleShape, color = BrandSoft) {
                        Icon(
                            TablerIcons.Add,
                            "Xem ${food.name}",
                            Modifier
                                .padding(7.dp)
                                .size(16.dp),
                            tint = Brand)
                    } else Text(
                        "Tạm hết hàng",
                        color = Brand,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Nhãn overlay hiển thị biểu tượng giảm giá hoặc bán chạy trên ảnh món ăn
@Composable
private fun FoodBadgesOverlay(food: MenuFood, modifier: Modifier = Modifier) {
    if (food.salePercent == null && !food.isBestSeller) return
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        if (food.isBestSeller) Surface(shape = CircleShape, color = Warning) {
            Icon(
                TablerIcons.Flame,
                "Món bán chạy",
                Modifier
                    .padding(5.dp)
                    .size(14.dp),
                tint = WarningDark)
        }
        if (food.salePercent != null) Surface(shape = CircleShape, color = BrandDark) {
            Icon(
                TablerIcons.RosetteDiscount,
                "Món đang giảm giá",
                Modifier
                    .padding(5.dp)
                    .size(14.dp),
                tint = Color.White)
        }
    }
}

// Ảnh món ăn có phủ lớp mờ và nhãn 'TẠM HẾT' nếu món tạm hết hàng
@Composable
private fun AvailabilityImage(food: MenuFood, @DrawableRes fallback: Int, modifier: Modifier) {
    Box(modifier) {
        HomeFoodImage(
            food.imageUrl,
            fallback,
            Modifier
                .fillMaxSize()
                .alpha(if (food.available) 1f else .42f))
        if (!food.available) {
            Surface(
                modifier = Modifier.align(Alignment.Center),
                color = Color.Black.copy(alpha = .72f),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    "TẠM HẾT",
                    Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Tải ảnh món ăn từ URL bằng AsyncImage với ảnh mặc định fallback
@Composable
private fun HomeFoodImage(url: String?, @DrawableRes fallback: Int, modifier: Modifier) {
    AsyncImage(
        model = resolveMediaUrl(url)?.let {
            if (it.startsWith("/")) "https://foodhub-8lv1.onrender.com$it" else it
        },
        placeholder = painterResource(fallback),
        error = painterResource(fallback),
        fallback = painterResource(fallback),
        contentDescription = null,
        modifier = modifier.background(InputBackground),
        contentScale = ContentScale.Crop,
    )
}

// Thanh trợ giúp và nhắn tin trực tiếp với nhân viên nhà hàng
@Composable
private fun ServiceBar(canChat: Boolean, isDineIn: Boolean, onClick: () -> Unit) {
    Surface(shape = RoundedCornerShape(12.dp), color = InputBackground) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(32.dp)
                    .background(WarmAccent, CircleShape), contentAlignment = Alignment.Center) {
                Icon(TablerIcons.ChefHat, null, Modifier.size(18.dp), tint = WarningDark)
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    if (isDineIn) "Cần trợ giúp tại bàn?" else "Cần hỗ trợ đơn hàng?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp)
                Text(
                    when {
                        canChat -> "Nhắn tin trực tiếp với nhân viên"
                        isDineIn -> "Quét QR bàn để mở kênh hỗ trợ"
                        else -> "Đăng nhập để trò chuyện với nhà hàng"
                    },
                    color = OnSurfaceVariant,
                    fontSize = 10.sp,
                )
            }
            Button(
                onClick = onClick,
                enabled = canChat,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Warning,
                    contentColor = WarningDark),
            ) {
                Text(
                    if (isDineIn) "Gọi phục vụ" else "Nhắn tin",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Thanh điều hướng dưới cùng chứa các tab chính của ứng dụng
@Composable
private fun HomeBottomBar(
    onOrdersClick: () -> Unit,
    onProfileClick: () -> Unit,
    canChat: Boolean,
    onChatClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    notificationUnreadCount: Int,
) {
    Surface(color = AppBackground.copy(alpha = .97f), shadowElevation = 8.dp) {
        Row(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp)
                .padding(horizontal = 8.dp)) {
            BottomDestination(TablerIcons.Home, "Trang chủ", true, {})
            BottomDestination(TablerIcons.Receipt, "Đơn hàng", false, onOrdersClick)
            if (canChat) {
                BottomDestination(TablerIcons.ChatBubble, "Tin nhắn", false, onChatClick)
            }
            BottomDestination(
                TablerIcons.Bell,
                "Thông báo",
                false,
                onNotificationsClick,
                notificationUnreadCount,
            )
            BottomDestination(TablerIcons.User, "Cá nhân", false, onProfileClick)
        }
    }
}

// Một mục tab đơn lẻ trên thanh điều hướng dưới cùng
@Composable
private fun RowScope.BottomDestination(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    badgeCount: Int = 0,
) {
    Column(
        Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (badgeCount > 0) {
            BadgedBox(badge = { Badge { Text(if (badgeCount > 99) "99+" else badgeCount.toString()) } }) {
                Icon(
                    icon,
                    label,
                    Modifier.size(20.dp),
                    tint = if (selected) Brand else OnSurfaceVariant)
            }
        } else {
            Icon(
                icon,
                label,
                Modifier.size(20.dp),
                tint = if (selected) Brand else OnSurfaceVariant)
        }
        Spacer(Modifier.height(2.dp))
        Text(
            label,
            color = if (selected) Brand else OnSurfaceVariant,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold)
    }
}

// Màn hình thông báo lỗi và nút thử lại khi tải dữ liệu thất bại
@Composable
private fun HomeError(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(message)
        Spacer(Modifier.height(12.dp))
        Button(onClick = onRetry) { Text("Thử lại") }
    }
}

// Lấy ảnh mặc định thay thế theo chỉ số của món ăn
@DrawableRes
private fun fallbackFor(food: MenuFood, foods: List<MenuFood>): Int {
    val index = foods.indexOfFirst { it.id == food.id }
        .coerceAtLeast(0)
    return listOf(
        R.drawable.home_figma_01,
        R.drawable.home_figma_04,
        R.drawable.home_figma_05,
        R.drawable.home_figma_06,
        R.drawable.home_figma_07
    )[index % 5]
}

// Định dạng số tiền thành chuỗi theo chuẩn tiếng Việt (VNĐ)
private fun money(value: Long): String =
    NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN"))
        .format(value) + "đ"

// Cấu hình bảng màu cho FilterChip danh mục
@Composable
private fun homeFilterChipColors() = FilterChipDefaults.filterChipColors(
    containerColor = Color.White,
    labelColor = Neutral,
    selectedContainerColor = Brand,
    selectedLabelColor = Color.White,
    selectedLeadingIconColor = Color.White
)

// Preview xem trước giao diện HomeScreen trong Android Studio
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        state = HomeUiState(
            isLoading = false,
            categories = listOf(
                MenuCategory(
                    "main", "Món chính", listOf(
                        MenuFood(
                            "burger",
                            "Truffle Smash Burger",
                            145_000,
                            rating = "4.9",
                            isFeatured = true),
                        MenuFood("pizza", "Pizza Hải Sản Lò Củi", 185_000, rating = "4.8"),
                    ))),
        ),
        onQueryChange = {}, onCategoryClick = {}, onRetry = {}, onFoodClick = {},
        onMenuClick = {}, onCartClick = {}, onOrdersClick = {}, onProfileClick = {}, onQrClick = {},
    )
}
