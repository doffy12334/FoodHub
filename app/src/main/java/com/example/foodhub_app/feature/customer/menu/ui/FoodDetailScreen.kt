package com.example.foodhub_app.feature.customer.menu.ui

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.example.foodhub_app.ui.theme.Brand
import com.example.foodhub_app.ui.theme.BrandDark
import com.example.foodhub_app.ui.theme.BrandSoft
import com.example.foodhub_app.R
import com.example.foodhub_app.feature.customer.menu.model.FoodOption
import com.example.foodhub_app.feature.customer.menu.model.FoodOptionGroup
import com.example.foodhub_app.ui.theme.Foodhub_AppTheme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.foodhub_app.feature.customer.menu.model.FoodDetail
import com.example.foodhub_app.feature.customer.menu.model.FoodReview
import java.text.NumberFormat
import java.util.Locale


data class FoodCartSelection(
    val foodId: String,
    val quantity: Int,
    val optionId: List<String> = emptyList(),
    val note: String = ""
)

private fun money(value: Long) = NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN"))
    .format(value) + "đ"

@Composable
fun FoodDetailScreen(
    food: FoodDetail,
    onBackClick: () -> Unit,
    onAddToCart: (FoodCartSelection) -> Unit,
    modifier: Modifier = Modifier,
    reviews: List<FoodReview> = emptyList(),
    onCartClick: () -> Unit = {},
    isAdding: Boolean = false,
    snackbarHost: @Composable () -> Unit = {}
) {
    var quantity by rememberSaveable(food.id) { mutableIntStateOf(1) }
    var note by rememberSaveable(food.id) { mutableStateOf("") }
    var selected by rememberSaveable(food.id) { mutableStateOf(emptyList<String>()) }

    val options = food.groups.flatMap { it.options }
    val selectedIds = selected.filter { id -> options.any { it.id == id } }

    val total = ((food.salePrice ?: food.basePrice) + options.filter { it.id in selectedIds }
        .sumOf { it.priceAdd }) * quantity

    val valid = food.available && food.groups.all { group ->
        !group.required || group.options.any { it.id in selected }
    }

    val context = LocalContext.current

    Scaffold(modifier = modifier, snackbarHost = snackbarHost, bottomBar = {
        Surface(color = Color.White, shadowElevation = 10.dp) {
            Row(
                Modifier.navigationBarsPadding()
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    Modifier.height(54.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFE0DBD8)),
                        color = Color.White) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { quantity-- },
                                enabled = quantity > 1 && !isAdding) {
                                Icon(Icons.Default.Remove, "Giảm")
                            }
                            Text(
                                "$quantity",
                                Modifier.width(26.dp),
                                fontWeight = FontWeight.Bold)
                            IconButton(
                                onClick = { quantity++ },
                                enabled = quantity < 99 && !isAdding) {
                                Icon(Icons.Default.Add, "Tăng", tint = Brand)
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        onAddToCart(
                            FoodCartSelection(
                                food.id, quantity, selectedIds, note.trim()))
                    },
                    enabled = valid && !isAdding,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandDark),
                    modifier = Modifier.weight(1f)
                        .heightIn(min = 56.dp)) {
                    if (isAdding) CircularProgressIndicator(
                        Modifier.size(20.dp), strokeWidth = 2.dp)
                    else Icon(
                        Icons.Default.ShoppingCart,
                        null,
                        Modifier.size(18.dp),
                        tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (food.available) "Thêm vào giỏ" else "Tạm hết món",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp)
                    Spacer(Modifier.weight(1f))
                    Text(money(total), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }) { padding ->
        Column(
            Modifier.fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())) {
            Box(
                Modifier.fillMaxWidth()
                    .aspectRatio(16f / 10f)
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))) {
                FoodImage(food.imageUrl, food.name, Modifier.fillMaxSize())
                Box(
                    Modifier.fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent, Color.Black.copy(alpha = .48f)))))
                Row(
                    Modifier.fillMaxWidth()
                        .padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Surface(shape = CircleShape, color = Color.White.copy(alpha = .9f)) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(shape = CircleShape, color = Color.White.copy(alpha = .9f)) {
                            IconButton(onClick = onCartClick) {
                                Icon(Icons.Default.ShoppingCart, "Mở giỏ hàng")
                            }
                        }
                        Surface(shape = CircleShape, color = Color.White.copy(alpha = .9f)) {
                            IconButton(onClick = {
                                context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "${food.name} - ${money(food.basePrice)}")
                                }, "Chia sẻ món ăn"))
                            }) { Icon(Icons.Default.Share, "Chia sẻ món ăn") }
                        }
                    }
                }
            }

            Column(
                Modifier.padding(horizontal = 18.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Text(
                        food.name,
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp)
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(start = 14.dp)) {
                        Text(
                            money(food.salePrice ?: food.basePrice),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Brand)
                        if (food.salePrice != null) Text(
                            money(food.basePrice),
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.LineThrough))
                    }
                }
                Text(food.description, color = Color.DarkGray, lineHeight = 22.sp, fontSize = 14.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoChip(Icons.Default.Star, "${food.rating} điểm", Color(0xFFFFB600))
                    InfoChip(Icons.Default.Timer, "8–12 phút", Color.Gray)
                    InfoChip(
                        Icons.Default.CheckCircle,
                        if (food.available) "Đang bán" else "Tạm hết",
                        if (food.available) Brand else Color.Gray)
                }

                HorizontalDivider(
                    Modifier.padding(vertical = 8.dp), thickness = 4.dp, color = Color(0xFFF2F2F2))

                food.groups.forEach { group ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            group.name,
                            Modifier.weight(1f),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp)
                        Text(
                            if (group.required) "Bắt buộc" else "Tùy chọn", Modifier.background(
                                    if (group.required) BrandSoft else Color(0xFFF2F2F2),
                                    RoundedCornerShape(8.dp))
                                .padding(8.dp), fontSize = 11.sp, color = Color.Gray)
                    }

                    group.options.forEach { option ->
                        val checked = option.id in selected
                        val choose = {
                            selected = if (group.multiple) {
                                if (checked) selected - option.id else selected + option.id
                            } else selected.filterNot { id -> group.options.any { it.id == id } } + option.id
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (checked) BrandSoft.copy(alpha = .35f) else Color.White,
                            border = BorderStroke(1.dp, if (checked) Brand else Color(0xFFE7E2DF)),
                            modifier = Modifier.fillMaxWidth()
                                .clickable { choose() }) {
                            Row(
                                Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                if (group.multiple) Checkbox(
                                    checked,
                                    null,
                                    colors = CheckboxDefaults.colors(checkedColor = Brand))
                                else RadioButton(
                                    checked,
                                    null,
                                    colors = RadioButtonDefaults.colors(selectedColor = Brand))
                                Text(option.name, Modifier.weight(1f), fontSize = 14.sp)
                                Text(
                                    if (option.priceAdd == 0L) "Đã bao gồm" else "+${money(option.priceAdd)}",
                                    fontSize = 12.sp,
                                    color = Brand)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }

                Text("Ghi chú cho bếp", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    note,
                    { note = it.take(255) },
                    enabled = !isAdding,
                    placeholder = { Text("Ít sốt, không hành tây, v.v.") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2)
            }
        }
    }
}

@Composable
private fun InfoChip(icon: ImageVector, label: String, tint: Color) {
    Surface(color = Color.White, shape = RoundedCornerShape(10.dp), border = BorderStroke(1.dp, Color(0xFFE8E3E0))) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, Modifier.size(14.dp), tint = tint)
            Spacer(Modifier.width(4.dp))
            Text(label, color = Color.DarkGray, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun FoodImage(url: String?, name: String, modifier: Modifier = Modifier) {
    if (LocalInspectionMode.current) {
        Image(
            painter = painterResource(R.drawable.food_detail_burger),
            contentDescription = name,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
        return
    }
    SubcomposeAsyncImage(
        model = url,
        contentDescription = name,
        modifier = modifier.background(Color(0xFFF2F2F2)),
        contentScale = ContentScale.Crop,
        loading = {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(Modifier.size(24.dp), color = Brand)
            }
        },
        error = {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Chưa có ảnh món", color = Color.Gray, fontSize = 12.sp)
            }
        }
    )
}

@Preview(name = "Chi tiết món ăn", showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun FoodDetailScreenPreview() {
    Foodhub_AppTheme(darkTheme = false, dynamicColor = false) {
        FoodDetailScreen(
            food = FoodDetail(
                id = "preview-burger",
                name = "Burger bò phô mai",
                description = "Bò nướng mọng nước, phô mai Cheddar, rau tươi và sốt đặc biệt trong bánh mì nướng giòn.",
                basePrice = 89000,
                salePrice = 79000,
                rating = "4.9",
                groups = listOf(
                    FoodOptionGroup(
                        id = "size",
                        name = "Chọn kích cỡ",
                        multiple = false,
                        required = true,
                        options = listOf(
                            FoodOption("single", "Tiêu chuẩn", 0),
                            FoodOption("double", "Hai lớp bò", 35000)
                        )
                    ),
                    FoodOptionGroup(
                        id = "topping",
                        name = "Topping thêm",
                        multiple = true,
                        required = false,
                        options = listOf(
                            FoodOption("cheese", "Phô mai Cheddar", 10000),
                            FoodOption("egg", "Trứng ốp la", 12000)
                        )
                    )
                )
            ),
            onBackClick = {},
            onAddToCart = {}
        )
    }
}
