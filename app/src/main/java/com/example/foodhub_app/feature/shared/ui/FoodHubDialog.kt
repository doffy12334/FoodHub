package com.example.foodhub_app.feature.shared.ui

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.createBitmap
import com.example.foodhub_app.core.ui.icons.foodCategoryIconKey
import com.example.foodhub_app.core.ui.icons.foodCategoryIconOptions
import com.example.foodhub_app.core.ui.icons.TablerIcons
import com.example.foodhub_app.feature.customer.home.viewmodel.HomeTableSession
import com.example.foodhub_app.feature.customer.menu.model.FoodDetail
import com.example.foodhub_app.feature.customer.order.model.OrderType
import com.example.foodhub_app.ui.theme.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import java.text.NumberFormat
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.EnumMap
import java.util.Locale
import java.util.TimeZone

// Dialog cơ sở thiết kế theo phong cách FoodHub với thanh tiêu đề BrandDark và nút đóng
@Composable
fun FoodHubDialog(
    onDismissRequest: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
    dismissEnabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
    actions: @Composable RowScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = { if (dismissEnabled) onDismissRequest() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(.92f)
                .heightIn(max = 760.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            tonalElevation = 8.dp,
            shadowElevation = 18.dp,
        ) {
            Column(Modifier.fillMaxWidth()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(BrandDark)
                        .padding(start = 20.dp, end = 10.dp, top = 13.dp, bottom = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        eyebrow?.let {
                            Text(
                                it.uppercase(),
                                color = Color.White.copy(alpha = .78f),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp)
                            Spacer(Modifier.size(4.dp))
                        }
                        Text(
                            title,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold)
                    }
                    Spacer(Modifier.width(10.dp))
                    Surface(shape = CircleShape, color = Color.White.copy(alpha = .14f)) {
                        IconButton(
                            onClick = onDismissRequest,
                            enabled = dismissEnabled,
                            modifier = Modifier.size(36.dp)) {
                            Icon(
                                TablerIcons.Close,
                                "Đóng",
                                tint = Color.White,
                                modifier = Modifier.size(19.dp))
                        }
                    }
                }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp), content = content)
                HorizontalDivider(color = Color(0xFFECE8E5))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    content = actions,
                )
            }
        }
    }
}

// Alert Dialog cơ sở dạng bảng thông báo xác nhận với tiêu đề, nội dung và các nút bấm
@Composable
fun FoodHubAlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    text: (@Composable () -> Unit)? = null,
    shape: Shape = RoundedCornerShape(28.dp),
    containerColor: Color = Color.White,
    iconContentColor: Color = Color.White,
    titleContentColor: Color = Color.White,
    textContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    tonalElevation: Dp = 8.dp,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
) {
    Dialog(onDismissRequest = onDismissRequest, properties = properties) {
        Surface(
            modifier = modifier
                .fillMaxWidth(.92f)
                .heightIn(max = 760.dp),
            shape = shape,
            color = containerColor,
            tonalElevation = tonalElevation,
            shadowElevation = 18.dp,
        ) {
            Column(Modifier.fillMaxWidth()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(BrandDark)
                        .padding(start = 20.dp, end = 10.dp, top = 13.dp, bottom = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    icon?.let {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = .14f),
                            contentColor = iconContentColor) {
                            Column(Modifier.padding(8.dp), content = { it() })
                        }
                        Spacer(Modifier.width(10.dp))
                    }
                    title?.let {
                        Surface(
                            Modifier.weight(1f),
                            color = Color.Transparent,
                            contentColor = titleContentColor) { it() }
                    } ?: Spacer(Modifier.weight(1f))
                    IconButton(onClick = onDismissRequest, modifier = Modifier.size(36.dp)) {
                        Icon(
                            TablerIcons.Close,
                            "Đóng",
                            tint = Color.White,
                            modifier = Modifier.size(19.dp))
                    }
                }
                text?.let {
                    Surface(
                        Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        color = Color.Transparent,
                        contentColor = textContentColor,
                    ) { it() }
                }
                HorizontalDivider(color = Color(0xFFECE8E5))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    dismissButton?.let {
                        it()
                        Spacer(Modifier.weight(1f))
                    }
                    confirmButton()
                }
            }
        }
    }
}

// Dialog xác nhận thông tin giao hàng, phương thức thanh toán và đặt hàng
@Composable
fun CheckoutDialog(
    cart: Cart,
    type: OrderType,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (CheckoutRequest) -> Unit,
) {
    var paymentMethod by rememberSaveable { mutableStateOf("CASH") }
    var note by rememberSaveable { mutableStateOf("") }
    var recipientName by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    val deliveryValid = type != OrderType.DELIVERY ||
            (recipientName.isNotBlank() && phone.isNotBlank() && address.isNotBlank())
    Dialog(onDismissRequest = { if (!isSubmitting) onDismiss() }) {
        Surface(shape = RoundedCornerShape(22.dp), color = CeramicSurface, tonalElevation = 8.dp) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = 720.dp)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 18.dp, end = 8.dp, top = 12.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            type.label()
                                .uppercase(),
                            color = Brand,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold)
                        Text("Xác nhận và đặt món", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(
                        onClick = onDismiss,
                        enabled = !isSubmitting) { Icon(TablerIcons.Close, "Đóng") }
                }
                HorizontalDivider(color = CardStroke)
                Column(
                    Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    if (type == OrderType.DELIVERY) {
                        Text(
                            "ĐỊA CHỈ GIAO HÀNG",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceVariant)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                recipientName,
                                { recipientName = it },
                                Modifier.weight(1f),
                                label = { Text("Người nhận *") },
                                singleLine = true)
                            OutlinedTextField(
                                phone,
                                {
                                    phone = it.filter(Char::isDigit)
                                        .take(11)
                                },
                                Modifier.weight(1f),
                                label = { Text("Số điện thoại *") },
                                singleLine = true)
                        }
                        OutlinedTextField(
                            address,
                            { address = it },
                            Modifier.fillMaxWidth(),
                            label = { Text("Địa chỉ chi tiết *") },
                            minLines = 2)
                    } else {
                        Surface(
                            color = Brand.copy(alpha = .07f),
                            shape = RoundedCornerShape(12.dp)) {
                            Text(
                                if (type == OrderType.DINE_IN) "Đơn sẽ được phục vụ tại bàn đang kết nối."
                                else "Nhận món tại quầy bằng mã nhận món sau khi đặt.",
                                Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                color = OnSurfaceVariant, fontSize = 12.sp,
                            )
                        }
                    }
                    Text(
                        "PHƯƠNG THỨC THANH TOÁN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceVariant)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PaymentOption(
                            "CASH",
                            "Tiền mặt",
                            "Thanh toán khi nhận món",
                            TablerIcons.Payment,
                            paymentMethod) { paymentMethod = it }
                        PaymentOption(
                            "ZALOPAY",
                            "Ví ZaloPay",
                            "QR realtime · tự động xác nhận",
                            TablerIcons.AccountBalanceWallet,
                            paymentMethod) { paymentMethod = it }
                        PaymentOption(
                            "VNPAY",
                            "Cổng VNPay",
                            "Thẻ ngân hàng hoặc ứng dụng hỗ trợ",
                            TablerIcons.AccountBalanceWallet,
                            paymentMethod) { paymentMethod = it }
                    }
                    OutlinedTextField(
                        note,
                        { note = it.take(255) },
                        Modifier.fillMaxWidth(),
                        label = { Text("Ghi chú cho nhà hàng") },
                        minLines = 2)
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardStroke)) {
                        Column(
                            Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(Modifier.fillMaxWidth()) {
                                Text(
                                    "Giá món (${cart.items.sumOf { it.quantity }} món)",
                                    Modifier.weight(1f),
                                    color = OnSurfaceVariant,
                                    fontSize = 12.sp)
                                Text(money(cart.subtotal), fontSize = 12.sp)
                            }
                            if (cart.vatAmount > 0) CheckoutPriceRow("Thuế GTGT", cart.vatAmount)
                            if (cart.deliveryFee > 0) CheckoutPriceRow(
                                "Phí giao hàng",
                                cart.deliveryFee)
                            if (cart.serviceFee > 0) CheckoutPriceRow(
                                "Phí dịch vụ",
                                cart.serviceFee)
                            if (cart.discountAmount > 0) CheckoutPriceRow(
                                "Giảm giá",
                                -cart.discountAmount,
                                Color(0xFF00875A))
                            HorizontalDivider(color = CardStroke)
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Tổng thanh toán",
                                    Modifier.weight(1f),
                                    fontWeight = FontWeight.Bold)
                                Column(horizontalAlignment = Alignment.End) {
                                    if (cart.discountAmount > 0) Text(
                                        money(cart.totalAmount + cart.discountAmount),
                                        color = OnSurfaceVariant,
                                        fontSize = 11.sp,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough),
                                    )
                                    Text(
                                        money(cart.totalAmount),
                                        color = Brand,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 19.sp)
                                }
                            }
                        }
                    }
                }
                Button(
                    onClick = {
                        onSubmit(
                            CheckoutRequest(
                                type,
                                paymentMethod,
                                note,
                                recipientName,
                                phone,
                                address))
                    },
                    enabled = deliveryValid && !isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandDark),
                ) {
                    if (isSubmitting) CircularProgressIndicator(
                        Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp)
                    else Icon(TablerIcons.CheckCircle, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (isSubmitting) "Đang đặt món…" else "Hoàn tất đặt đơn",
                        fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Dialog chỉnh sửa tùy chọn món ăn và số lượng trong giỏ hàng
@Composable
fun CartEditDialog(
    item: CartItem,
    food: FoodDetail,
    onDismiss: () -> Unit,
    onSave: (CartItem, Int, List<String>, String) -> Unit
) {
    var quantity by rememberSaveable(item.id) { mutableIntStateOf(item.quantity) }
    var selected by rememberSaveable(item.id) { mutableStateOf(item.options.map { it.id }) }
    var note by rememberSaveable(item.id) { mutableStateOf(item.note) }
    val valid =
        food.groups.all { group -> !group.required || group.options.any { it.id in selected } }
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(8.dp), color = CeramicSurface) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = 620.dp)
                    .padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Sửa ${item.name}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge)
                Column(
                    Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())) {
                    food.groups.forEach { group ->
                        Text(
                            group.name,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 8.dp))
                        group.options.forEach { option ->
                            val checked = option.id in selected
                            val choose = {
                                selected = if (group.multiple) {
                                    if (checked) selected - option.id else selected + option.id
                                } else selected.filterNot { id -> group.options.any { it.id == id } } + option.id
                            }
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .then(
                                        if (group.multiple) Modifier.toggleable(
                                            checked,
                                            role = Role.Checkbox) { choose() }
                                        else Modifier.selectable(
                                            checked,
                                            role = Role.RadioButton) { choose() }
                                    )
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (group.multiple) Checkbox(
                                    checked,
                                    null) else RadioButton(checked, null)
                                Text(option.name, Modifier.weight(1f))
                                if (option.priceAdd > 0) Text(
                                    "+${money(option.priceAdd)}",
                                    color = Brand)
                            }
                        }
                    }
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it.take(255) },
                        label = { Text("Ghi chú") },
                        supportingText = { Text("${note.length}/255") },
                        minLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        enabled = quantity > 1,
                        modifier = Modifier.semantics { contentDescription = "Giảm số lượng" }
                    ) {
                        Text("−", fontSize = 24.sp)
                    }
                    Text(
                        "$quantity",
                        Modifier.width(32.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    IconButton(
                        onClick = { if (quantity < 99) quantity++ },
                        enabled = quantity < 99) {
                        Icon(TablerIcons.Add, "Tăng số lượng")
                    }
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = onDismiss) { Text("Hủy") }
                    Button(
                        onClick = { onSave(item, quantity, selected, note) },
                        enabled = valid) { Text("Lưu") }
                }
            }
        }
    }
}

// Dòng hiển thị chi tiết một khoản tiền (giá món, thuế, phí giao hàng...) trong hóa đơn
@Composable
fun CheckoutPriceRow(label: String, amount: Long, color: Color = OnSurfaceVariant) {
    Row(Modifier.fillMaxWidth()) {
        Text(label, Modifier.weight(1f), color = OnSurfaceVariant, fontSize = 12.sp)
        Text(money(amount), color = color, fontSize = 12.sp)
    }
}

// Thẻ lựa chọn một phương thức thanh toán (Tiền mặt, ZaloPay, VNPay)
@Composable
fun PaymentOption(
    value: String,
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: String,
    onSelect: (String) -> Unit,
) {
    val active = value == selected
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(value) },
        color = if (active) Brand.copy(alpha = .06f) else Color.White,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            if (active) 1.5.dp else 1.dp,
            if (active) Brand else CardStroke),
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = if (active) Brand.copy(alpha = .12f) else AppBackground,
                shape = RoundedCornerShape(9.dp)) {
                Icon(
                    icon,
                    null,
                    Modifier
                        .padding(9.dp)
                        .size(18.dp),
                    tint = if (active) Brand else OnSurfaceVariant)
            }
            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text(subtitle, color = OnSurfaceVariant, fontSize = 10.sp)
            }
            RadioButton(selected = active, onClick = { onSelect(value) })
        }
    }
}

// Dialog cho phép khách hàng chọn số sao, viết bình luận và tải ảnh đánh giá món ăn
@Composable
fun ReviewDialog(
    itemName: String,
    busy: Boolean,
    uploading: Boolean,
    onDismiss: () -> Unit,
    onUploadImage: (Uri, (String) -> Unit) -> Unit,
    onSubmit: (Int, String, List<String>) -> Unit,
) {
    var rating by rememberSaveable { mutableIntStateOf(5) }
    var comment by rememberSaveable { mutableStateOf("") }
    var imageLink by rememberSaveable { mutableStateOf("") }
    val imageUrls = remember { mutableStateListOf<String>() }
    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null && imageUrls.size < 5) {
                onUploadImage(uri) { uploadedUrl ->
                    if (uploadedUrl !in imageUrls) imageUrls.add(
                        uploadedUrl)
                }
            }
        }
    val isBusy = busy || uploading
    FoodHubDialog(
        onDismissRequest = { if (!isBusy) onDismiss() },
        title = "Đánh giá $itemName",
        eyebrow = "Trải nghiệm món ăn",
        dismissEnabled = !isBusy,
        content = {
            Column(
                Modifier
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    (1..5).forEach { value ->
                        Icon(
                            if (value <= rating) TablerIcons.StarFilled else TablerIcons.Star,
                            contentDescription = "$value sao",
                            modifier = Modifier
                                .size(36.dp)
                                .clickable(enabled = !isBusy) { rating = value }
                                .padding(4.dp),
                            tint = if (value <= rating) Warning else OnSurfaceVariant.copy(alpha = .45f),
                        )
                    }
                }
                OutlinedTextField(
                    comment,
                    { comment = it.take(1000) },
                    enabled = !isBusy,
                    label = { Text("Nhận xét") },
                    minLines = 3)
                Text("Ảnh đánh giá (${imageUrls.size}/5)", fontWeight = FontWeight.SemiBold)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        imageLink,
                        { imageLink = it },
                        Modifier.weight(1f),
                        label = { Text("Link ảnh") },
                        singleLine = true,
                        enabled = !isBusy && imageUrls.size < 5,
                    )
                    OutlinedButton(
                        onClick = {
                            val link = imageLink.trim()
                            if (link.startsWith("http") && link !in imageUrls && imageUrls.size < 5) imageUrls.add(
                                link)
                            imageLink = ""
                        },
                        enabled = !isBusy && imageUrls.size < 5 && imageLink.trim()
                            .startsWith("http"),
                    ) { Text("Thêm") }
                }
                OutlinedButton(
                    onClick = { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    enabled = !isBusy && imageUrls.size < 5,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (uploading) {
                        CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(if (uploading) "Đang tải ảnh..." else "Chọn ảnh từ điện thoại")
                }
                imageUrls.forEachIndexed { index, url ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "${index + 1}. $url",
                            Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 12.sp)
                        IconButton(onClick = { imageUrls.remove(url) }, enabled = !isBusy) {
                            Icon(TablerIcons.Close, "Xóa ảnh")
                        }
                    }
                }
            }
        },
        actions = {
            OutlinedButton(
                enabled = !isBusy,
                onClick = onDismiss,
                modifier = Modifier.weight(1f)) { Text("Hủy") }
            Spacer(Modifier.width(10.dp))
            Button(
                enabled = !isBusy,
                onClick = { onSubmit(rating, comment, imageUrls.toList()) },
                modifier = Modifier.weight(1f)) { Text(if (busy) "Đang gửi…" else "Gửi đánh giá") }
        },
    )
}

// Dialog xác nhận hủy đơn hàng và nhập lý do hủy
@Composable
fun CancelOrderDialog(
    orderCode: String,
    busy: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var reason by rememberSaveable(orderCode) { mutableStateOf("") }
    FoodHubDialog(
        onDismissRequest = onDismiss,
        title = "Hủy đơn #${orderCode.removePrefix("#")}",
        eyebrow = "Xác nhận thao tác",
        dismissEnabled = !busy,
        content = {
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it.take(255) },
                label = { Text("Lý do hủy") },
                supportingText = { Text("${reason.length}/255") },
                minLines = 3,
                enabled = !busy,
                modifier = Modifier.fillMaxWidth()
            )
        },
        actions = {
            OutlinedButton(
                onClick = onDismiss,
                enabled = !busy,
                modifier = Modifier.weight(1f)) { Text("Quay lại") }
            Spacer(Modifier.width(10.dp))
            Button(
                onClick = { onConfirm(reason.trim()) },
                enabled = reason.isNotBlank() && !busy,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = BrandDark)) {
                Text("Xác nhận hủy")
            }
        },
    )
}

// Dialog quản trị viên dùng để thêm mới hoặc chỉnh sửa thông tin món ăn
@Composable
fun AdminMenuEditorDialog(
    item: AdminMenuItem?,
    categories: List<AdminMenuCategory>,
    isSaving: Boolean,
    isUploading: Boolean,
    onUploadImage: (Uri, (String) -> Unit) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: (AdminMenuInput) -> Unit,
) {
    var name by remember(item?.id) { mutableStateOf(item?.name.orEmpty()) }
    var description by remember(item?.id) { mutableStateOf(item?.description.orEmpty()) }
    var price by remember(item?.id) {
        mutableStateOf(
            item?.price?.toString()
                .orEmpty())
    }
    var imageUrl by remember(item?.id) { mutableStateOf(item?.imageUrl.orEmpty()) }
    var sortOrder by remember(item?.id) { mutableStateOf(item?.sortOrder?.toString() ?: "0") }
    var categoryId by remember(item?.id, categories) {
        mutableStateOf(
            item?.categoryId?.takeIf(String::isNotBlank) ?: categories.firstOrNull()?.id.orEmpty())
    }
    var categoryMenuOpen by remember { mutableStateOf(false) }
    var isAvailable by remember(item?.id) { mutableStateOf(item?.isAvailable ?: true) }
    var isFeatured by remember(item?.id) { mutableStateOf(item?.isFeatured ?: true) }
    val selectedCategory = categories.firstOrNull { it.id == categoryId }
    val parsedPrice = price.toLongOrNull()
    val parsedSortOrder = sortOrder.toIntOrNull()
    val isValid =
        name.trim().length >= 2 && categoryId.isNotBlank() && parsedPrice != null && parsedPrice > 0 && parsedSortOrder != null
    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { onUploadImage(it) { uploadedUrl -> imageUrl = uploadedUrl } }
        }

    FoodHubAlertDialog(
        onDismissRequest = { if (!isSaving && !isUploading) onDismiss() },
        title = { Text(if (item == null) "Thêm món ăn" else "Chỉnh sửa món ăn") },
        text = {
            Column(
                Modifier
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedTextField(
                    name,
                    { name = it },
                    label = { Text("Tên món") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                Box {
                    OutlinedButton(
                        onClick = { categoryMenuOpen = true },
                        enabled = categories.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text(selectedCategory?.name ?: "Chọn danh mục") }
                    DropdownMenu(categoryMenuOpen, { categoryMenuOpen = false }) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    categoryId = category.id
                                    categoryMenuOpen = false
                                },
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it.filter(Char::isDigit) },
                    label = { Text("Giá bán") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { if (it.length <= 500) description = it },
                    label = { Text("Mô tả") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    imageUrl,
                    { imageUrl = it },
                    label = { Text("URL ảnh") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                OutlinedButton(
                    onClick = { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    enabled = !isUploading,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (isUploading) CircularProgressIndicator(
                        Modifier.size(18.dp),
                        strokeWidth = 2.dp)
                    Text(if (isUploading) "Đang tải ảnh..." else "Chọn ảnh từ thiết bị")
                }
                OutlinedTextField(
                    value = sortOrder,
                    onValueChange = { sortOrder = it.filter(Char::isDigit) },
                    label = { Text("Thứ tự hiển thị") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Đang bán", Modifier.weight(1f))
                    Switch(isAvailable, { isAvailable = it })
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Món nổi bật", Modifier.weight(1f))
                    Switch(isFeatured, { isFeatured = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(
                        AdminMenuInput(
                            categoryId = categoryId,
                            name = name.trim(),
                            description = description.trim()
                                .takeIf(String::isNotBlank),
                            basePrice = parsedPrice ?: 0,
                            imageUrl = imageUrl.trim()
                                .takeIf(String::isNotBlank),
                            isAvailable = isAvailable,
                            isFeatured = isFeatured,
                            sortOrder = parsedSortOrder ?: 0,
                        )
                    )
                },
                enabled = isValid && !isSaving && !isUploading,
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                }
                Text(if (item == null) "Thêm món" else "Lưu")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSaving && !isUploading) { Text("Hủy") }
        },
    )
}

// Dialog quản trị viên dùng để thêm mới hoặc chỉnh sửa danh mục món ăn
@Composable
fun AdminCategoryEditorDialog(
    category: AdminMenuCategory?,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (AdminMenuCategoryInput) -> Unit,
) {
    var name by remember(category?.id) { mutableStateOf(category?.name.orEmpty()) }
    var icon by remember(category?.id) {
        mutableStateOf(foodCategoryIconKey(category?.icon.orEmpty(), category?.name.orEmpty()))
    }
    var sortOrder by remember(category?.id) {
        mutableStateOf(
            category?.sortOrder?.toString() ?: "0")
    }
    val parsedSortOrder = sortOrder.toIntOrNull()

    FoodHubAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (category == null) "Thêm danh mục" else "Sửa danh mục") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    name,
                    { name = it },
                    label = { Text("Tên danh mục") },
                    singleLine = true)
                Text("Biểu tượng", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    foodCategoryIconOptions.forEach { option ->
                        Column(
                            Modifier
                                .background(
                                    if (icon == option.key) AdminPrimarySoft else AdminSurface,
                                    RoundedCornerShape(10.dp),
                                )
                                .clickable { icon = option.key }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Icon(
                                option.icon,
                                option.label,
                                Modifier.size(22.dp),
                                tint = AdminPrimary)
                            Text(option.label, fontSize = 10.sp, color = AdminMuted)
                        }
                    }
                }
                OutlinedTextField(
                    sortOrder,
                    { sortOrder = it.filter(Char::isDigit) },
                    label = { Text("Thứ tự hiển thị") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(
                        AdminMenuCategoryInput(
                            name.trim(),
                            icon.trim(),
                            parsedSortOrder ?: 0))
                },
                enabled = !isSaving && name.isNotBlank() && icon.isNotBlank() && parsedSortOrder != null,
            ) { Text(if (category == null) "Thêm" else "Lưu") }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !isSaving) { Text("Hủy") } },
    )
}

// Dialog quản trị viên dùng để tạo chương trình giảm giá Flash Sale
@Composable
fun AdminFlashSaleDialog(
    items: List<AdminMenuItem>,
    initialItemId: String?,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (AdminFlashSaleInput) -> Unit,
) {
    var selectedId by remember(initialItemId, items) {
        mutableStateOf(initialItemId?.takeIf { id -> items.any { it.id == id } }
            ?: items.firstOrNull()?.id.orEmpty())
    }
    var discount by remember { mutableStateOf("10") }
    var hours by remember { mutableStateOf("24") }
    var menuOpen by remember { mutableStateOf(false) }
    val selected = items.firstOrNull { it.id == selectedId }
    val parsedDiscount = discount.toDoubleOrNull()
    val parsedHours = hours.toIntOrNull()
    FoodHubAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tạo flash sale") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Box {
                    OutlinedButton(
                        onClick = { menuOpen = true },
                        modifier = Modifier.fillMaxWidth()) {
                        Text(selected?.name ?: "Chọn món", Modifier.weight(1f))
                    }
                    DropdownMenu(menuOpen, { menuOpen = false }) {
                        items.forEach { item ->
                            DropdownMenuItem(
                                { Text(item.name) },
                                onClick = { selectedId = item.id; menuOpen = false })
                        }
                    }
                }
                OutlinedTextField(
                    discount,
                    { discount = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text("Phần trăm giảm") })
                OutlinedTextField(
                    hours,
                    { hours = it.filter(Char::isDigit) },
                    label = { Text("Thời lượng (giờ)") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(
                        AdminFlashSaleInput(
                            selectedId,
                            parsedDiscount ?: 0.0,
                            parsedHours ?: 0))
                },
                enabled = !isSaving && selectedId.isNotBlank() && parsedDiscount != null && parsedDiscount in 0.01..100.0 && parsedHours != null && parsedHours > 0,
            ) { Text("Tạo") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } },
    )
}

// Dialog quản trị viên dùng để tạo hoặc sửa nhóm tùy chọn cho món ăn
@Composable
fun AdminVariantEditorDialog(
    initial: AdminVariantInput?,
    saving: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onSubmit: (AdminVariantInput) -> Unit
) {
    var name by remember(initial) { mutableStateOf(initial?.name.orEmpty()) }
    var multiple by remember(initial) { mutableStateOf(initial?.multiple ?: false) }
    var required by remember(initial) { mutableStateOf(initial?.required ?: true) }
    var options by remember(initial) {
        mutableStateOf(initial?.options?.map {
            OptionDraft(
                it.id,
                it.name,
                it.priceAdd.toString())
        } ?: listOf(OptionDraft(null, "", "0")))
    }
    val valid = name.isNotBlank() && options.isNotEmpty() && options.all {
        it.name.isNotBlank() && (it.price.toLongOrNull() ?: -1) >= 0
    }
    FoodHubAlertDialog(
        onDismissRequest = { if (!saving) onDismiss() },
        title = { Text(if (initial == null) "Thêm nhóm" else "Sửa nhóm tùy chọn") },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                OutlinedTextField(
                    name,
                    { name = it },
                    label = { Text("Tên nhóm") },
                    enabled = !saving)
                Row {
                    Text("Chọn nhiều", Modifier.weight(1f)); Switch(
                    multiple,
                    { multiple = it },
                    enabled = !saving)
                }
                Row {
                    Text("Bắt buộc", Modifier.weight(1f)); Switch(
                    required,
                    { required = it },
                    enabled = !saving)
                }
                options.forEachIndexed { index, option ->
                    OutlinedTextField(
                        option.name,
                        { value ->
                            options =
                                options.mapIndexed { i, row -> if (i == index) row.copy(name = value) else row }
                        },
                        label = { Text("Tên tùy chọn ${index + 1}") },
                        enabled = !saving)
                    OutlinedTextField(
                        option.price,
                        { value ->
                            options =
                                options.mapIndexed { i, row -> if (i == index) row.copy(price = value) else row }
                        },
                        label = { Text("Giá cộng (đ)") },
                        enabled = !saving)
                }
                TextButton(
                    { options = options + OptionDraft(null, "", "0") },
                    enabled = !saving) { Text("Thêm tùy chọn") }
            }
        },
        confirmButton = {
            Button({
                onSubmit(
                    AdminVariantInput(
                        name.trim(),
                        multiple,
                        required,
                        options.map {
                            AdminVariantOptionInput(
                                it.name.trim(),
                                it.price.toLong(),
                                it.id)
                        },
                        initial?.sortOrder ?: 0))
            }, enabled = valid && !saving) { Text(if (saving) "Đang lưu…" else "Lưu") }
        },
        dismissButton = { TextButton(onDismiss, enabled = !saving) { Text("Hủy") } })
}

// Dialog quản trị viên dùng để tạo bàn ăn mới
@Composable
fun AdminCreateTableDialog(
    onDismiss: () -> Unit,
    onCreate: (String, Int, String?, String?) -> Unit,
    isSubmitting: Boolean,
    errorMessage: String?,
) {
    var name by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("2") }
    var floor by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val capacityValue = capacity.toIntOrNull()
    FoodHubAlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        title = { Text("Tạo bàn mới") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                errorMessage?.let {
                    Text(it, color = AdminRed, fontSize = 12.sp)
                }
                OutlinedTextField(
                    name,
                    { name = it },
                    Modifier.fillMaxWidth(),
                    label = { Text("Tên bàn") },
                    singleLine = true)
                OutlinedTextField(
                    capacity,
                    {
                        capacity = it.filter(Char::isDigit)
                            .take(2)
                    },
                    Modifier.fillMaxWidth(),
                    label = { Text("Số chỗ") },
                    singleLine = true)
                OutlinedTextField(
                    floor,
                    { floor = it },
                    Modifier.fillMaxWidth(),
                    label = { Text("Khu vực / tầng") },
                    singleLine = true)
                OutlinedTextField(
                    note,
                    { note = it },
                    Modifier.fillMaxWidth(),
                    label = { Text("Ghi chú") },
                    maxLines = 2)
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(name, capacityValue ?: 2, floor, note) },
                enabled = !isSubmitting && name.isNotBlank() && capacityValue != null && capacityValue > 0,
            ) {
                if (isSubmitting) CircularProgressIndicator(
                    Modifier.size(18.dp),
                    strokeWidth = 2.dp) else Text("Tạo bàn")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSubmitting) { Text("Hủy") }
        },
    )
}

// Dialog quản trị viên dùng để hiển thị và kiểm tra mã QR bàn ăn
@Composable
fun AdminQrDialog(state: QrDialogState, onDismiss: () -> Unit, onRetry: () -> Unit) {
    FoodHubAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("QR ${state.table.name}") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                when {
                    state.loading -> CircularProgressIndicator()
                    state.error != null -> {
                        Icon(TablerIcons.QrCode, null, Modifier.size(48.dp), tint = AdminRed)
                        Text(state.error, color = AdminRed, textAlign = TextAlign.Center)
                        OutlinedButton(onClick = onRetry) { Text("Tải lại QR") }
                    }

                    state.content != null -> {
                        val bitmap = remember(state.content) {
                            runCatching { createQrBitmap(state.content) }.getOrNull()
                        }
                        if (bitmap != null) {
                            Image(
                                bitmap.asImageBitmap(),
                                "Mã QR của ${state.table.name}",
                                Modifier.size(240.dp))
                        } else {
                            Text("Không thể tạo mã QR. Vui lòng tải lại.", color = AdminRed)
                            OutlinedButton(onClick = onRetry) { Text("Tải lại QR") }
                        }
                        HorizontalDivider()
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(TablerIcons.Armchair, null, tint = AdminPrimary)
                            Spacer(Modifier.width(8.dp))
                            Text("${state.table.capacity} chỗ • ${state.table.floor ?: "Chưa đặt khu vực"}")
                        }
                        Text(
                            "Mã QR xác thực ${state.table.name}",
                            color = AdminMuted,
                            fontSize = 12.sp)
                        Text(
                            "Dùng màn quét QR của khách hàng để kiểm thử.",
                            color = AdminGreen,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center)
                    }
                }
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("Đóng") } },
    )
}

// Dialog quản trị viên dùng để tạo mới hoặc chỉnh sửa mã giảm giá (Voucher)
@Composable
fun AdminVoucherEditorDialog(
    initial: AdminVoucher,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (AdminVoucher) -> Unit
) {
    var value by remember(initial) { mutableStateOf(initial) }
    var discount by remember(initial) { mutableStateOf(initial.discountValue.clean()) }
    var minimum by remember(initial) { mutableStateOf(initial.minOrderAmount.toString()) }
    var maximum by remember(initial) {
        mutableStateOf(
            initial.maxDiscountAmount?.toString()
                .orEmpty())
    }
    var usage by remember(initial) {
        mutableStateOf(
            initial.usageLimit?.toString()
                .orEmpty())
    }
    var perCustomer by remember(initial) { mutableStateOf(initial.perCustomerLimit.toString()) }
    var startDate by remember(initial) { mutableStateOf(initial.startsAt.take(10)) }
    var endDate by remember(initial) { mutableStateOf(initial.endsAt.take(10)) }

    val parsedDiscount = discount.toDoubleOrNull()
        ?.takeIf { it.isFinite() && it > 0 }
    val parsedMinimum = minimum.toLongOrNull()
        ?.takeIf { it >= 0 }
    val parsedMaximum = maximum.toLongOrNull()
        ?.takeIf { it > 0 }
    val parsedUsage = usage.toIntOrNull()
        ?.takeIf { it > 0 }
    val parsedPerCustomer = perCustomer.toIntOrNull()
        ?.takeIf { it > 0 }

    val parsedStart = parseVoucherDate(startDate)
    val parsedEnd = parseVoucherDate(endDate)
    val valid = value.code.trim().length >= 3 && value.name.trim().length >= 2 &&
            parsedDiscount != null &&
            (value.discountType != "PERCENT" || parsedDiscount <= 100) &&
            parsedMinimum != null &&
            (value.discountType != "PERCENT" || maximum.isBlank() || parsedMaximum != null) &&
            (usage.isBlank() || parsedUsage != null) && parsedPerCustomer != null &&
            parsedStart != null && parsedEnd != null && !parsedEnd.before(parsedStart) &&
            value.applicableOrderTypes.isNotEmpty()
    FoodHubAlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = { Text(if (initial.id.isBlank()) "Tạo voucher" else "Chỉnh sửa voucher") },
        text = {
            Column(
                Modifier
                    .heightIn(max = 560.dp)
                    .verticalScroll(androidx.compose.foundation.rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                VoucherField("Mã voucher", value.code) {
                    value = value.copy(
                        code = it.uppercase()
                            .filterNot(Char::isWhitespace)
                            .take(30))
                }
                VoucherField("Tên chương trình", value.name) { value = value.copy(name = it) }
                VoucherField("Mô tả", value.description) { value = value.copy(description = it) }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        value.discountType == "PERCENT",
                        { value = value.copy(discountType = "PERCENT") },
                        { Text("Giảm %") })
                    FilterChip(
                        value.discountType == "FIXED_AMOUNT",
                        { value = value.copy(discountType = "FIXED_AMOUNT") },
                        { Text("Giảm tiền") })
                }
                VoucherField(
                    if (value.discountType == "PERCENT") "Phần trăm giảm" else "Số tiền giảm",
                    discount,
                    keyboard = KeyboardType.Decimal) { discount = it.numeric(true) }
                VoucherField("Giá trị đơn tối thiểu", minimum, keyboard = KeyboardType.Number) {
                    minimum = it.numeric()
                }
                if (value.discountType == "PERCENT") VoucherField(
                    "Giảm tối đa (để trống nếu không giới hạn)",
                    maximum,
                    keyboard = KeyboardType.Number) { maximum = it.numeric() }
                Surface(color = AdminPrimarySoft, shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = value.distribution == "ACCOUNT",
                                onCheckedChange = { checked ->
                                    value =
                                        value.copy(distribution = if (checked) "ACCOUNT" else "SECRET")
                                },
                            )
                            Column {
                                Text(
                                    "Phát cho toàn bộ khách hàng",
                                    fontWeight = FontWeight.SemiBold)
                                Text(
                                    "Voucher xuất hiện trong tài khoản của mọi CUSTOMER.",
                                    color = AdminMuted,
                                    fontSize = 12.sp)
                            }
                        }
                        if (value.distribution == "SECRET") {
                            Text(
                                "Đang là voucher bí mật: không hiện trong tài khoản, chỉ người biết mã mới sử dụng được.",
                                color = AdminMuted,
                                fontSize = 12.sp)
                        }
                    }
                }
                Text("Áp dụng cho", fontWeight = FontWeight.SemiBold)
                listOf("DINE_IN", "TAKEAWAY", "DELIVERY").forEach { type ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            type in value.applicableOrderTypes,
                            { checked ->
                                value =
                                    value.copy(applicableOrderTypes = if (checked) value.applicableOrderTypes + type else value.applicableOrderTypes - type)
                            })
                        Text(type.typeLabel())
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VoucherField("Bắt đầu", startDate, modifier = Modifier.weight(1f)) {
                        startDate = it.take(10)
                    }
                    VoucherField("Kết thúc", endDate, modifier = Modifier.weight(1f)) {
                        endDate = it.take(10)
                    }
                }
                VoucherField(
                    "Tổng lượt (trống = không giới hạn)",
                    usage,
                    keyboard = KeyboardType.Number) { usage = it.numeric() }
                VoucherField("Lượt mỗi khách", perCustomer, keyboard = KeyboardType.Number) {
                    perCustomer = it.numeric()
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        value.isActive,
                        {
                            value = value.copy(isActive = it)
                        }); Spacer(Modifier.width(8.dp)); Text("Kích hoạt voucher")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    value.copy(
                        discountValue = parsedDiscount ?: return@Button,
                        minOrderAmount = parsedMinimum ?: return@Button,
                        maxDiscountAmount = if (value.discountType == "PERCENT") parsedMaximum else null,
                        usageLimit = parsedUsage,
                        perCustomerLimit = parsedPerCustomer ?: return@Button,
                        startsAt = voucherTimestamp(parsedStart ?: return@Button),
                        endsAt = voucherTimestamp(parsedEnd ?: return@Button, endOfDay = true),
                    ))
            }, enabled = valid && !isSaving) {
                if (isSaving) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                else Text("Lưu")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !isSaving) { Text("Hủy") } },
    )
}

// Ô nhập liệu OutlinedTextField dùng trong form chỉnh sửa Voucher
@Composable
fun VoucherField(
    label: String,
    text: String,
    modifier: Modifier = Modifier,
    keyboard: KeyboardType = KeyboardType.Text,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        text,
        onChange,
        modifier.fillMaxWidth(),
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        singleLine = true)
}

// Dialog hiển thị mã QR thanh toán ZaloPay và nút mở ứng dụng
@Composable
fun ZaloPayLaunchDialog(
    paymentUrl: String,
    qrContent: String?,
    orderCode: String? = null,
    amount: Long? = null,
    isChecking: Boolean = false,
    onOpenOnThisDevice: () -> Unit,
    onDismiss: () -> Unit,
) {
    val content = qrContent?.takeIf(String::isNotBlank) ?: paymentUrl
    val bitmap = remember(content) { runCatching { content.toQrBitmap(720) }.getOrNull() }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(22.dp), tonalElevation = 8.dp, color = Color.White) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0878C9))
                        .padding(horizontal = 14.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "ZaloPay",
                        color = Color(0xFF0878C9),
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(4.dp))
                            .padding(horizontal = 7.dp, vertical = 4.dp))
                    Text(
                        "Cổng thanh toán điện tử",
                        Modifier
                            .padding(start = 8.dp)
                            .weight(1f),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold)
                    IconButton(onClick = onDismiss, Modifier.size(32.dp)) {
                        Icon(
                            TablerIcons.Close,
                            "Đóng",
                            tint = Color.White)
                    }
                }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    orderCode?.takeIf(String::isNotBlank)
                        ?.let {
                            Text(
                                "ĐƠN HÀNG: #${it.removePrefix("#")}",
                                color = Color.Gray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold)
                        }
                    amount?.let {
                        Text(
                            NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN"))
                                .format(it) + "đ", fontSize = 23.sp, fontWeight = FontWeight.Black)
                    }
                    bitmap?.let {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                Color(0xFFE2E2E2)),
                            color = Color.White) {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "Mã QR thanh toán ZaloPay",
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(220.dp))
                        }
                    }
                    Text(
                        "Quét bằng ứng dụng ngân hàng hoặc Ví ZaloPay",
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        fontSize = 12.sp)
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFEDF6FF), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (isChecking) CircularProgressIndicator(
                            Modifier.size(17.dp),
                            strokeWidth = 2.dp,
                            color = Color(0xFF0878C9))
                        else Icon(
                            TablerIcons.ShieldCheck,
                            null,
                            Modifier.size(17.dp),
                            tint = Color(0xFF0878C9))
                        Text(
                            if (isChecking) "Đang tự động kiểm tra thanh toán…" else "Thanh toán được xác nhận tự động",
                            Modifier.padding(start = 8.dp),
                            color = Color(0xFF075A99),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = onOpenOnThisDevice,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0878C9)),
                    ) { Text("Mở ZaloPay trên thiết bị này", fontWeight = FontWeight.Bold) }
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp)) {
                        Text(
                            "Theo dõi trong Đơn hàng",
                            color = Brand)
                    }
                }
            }
        }
    }
}

// Dialog thông báo yêu cầu đăng nhập tài khoản để tiếp tục
@Composable
fun LoginRequiredDialog(
    onDismiss: () -> Unit,
    onLoginClick: () -> Unit,
    message: String = "Bạn cần đăng nhập tài khoản để thực hiện chức năng này.",
    title: String = "Yêu cầu đăng nhập",
) {
    FoodHubAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = { Text(message) },
        confirmButton = {
            Button(
                onClick = { onDismiss(); onLoginClick() },
                colors = ButtonDefaults.buttonColors(containerColor = Brand)) { Text("Đăng nhập") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Để sau") } },
    )
}

// Dialog thông báo yêu cầu đăng nhập khi khách bấm thêm món vào giỏ hàng
@Composable
fun AddToCartLoginDialog(onDismiss: () -> Unit, onLoginClick: () -> Unit) {
    LoginRequiredDialog(
        onDismiss, onLoginClick,
        "Bạn cần đăng nhập để thêm món vào giỏ TAKEAWAY hoặc DELIVERY.", "Đăng nhập")
}

// Dialog thông báo yêu cầu đăng nhập khi khách truy cập giỏ hàng
@Composable
fun CartLoginDialog(onDismiss: () -> Unit, onLoginClick: () -> Unit) {
    LoginRequiredDialog(
        onDismiss, onLoginClick,
        "Bạn cần đăng nhập để xem và chỉnh sửa giỏ hàng.", "Đăng nhập")
}

// Dialog thông báo phiên đăng nhập hết hạn khi xem thông báo
@Composable
fun NotificationLoginDialog(onDismiss: () -> Unit, onLoginClick: () -> Unit) {
    LoginRequiredDialog(
        onDismiss, onLoginClick,
        "Phiên đăng nhập của bạn đã hết hạn. Vui lòng đăng nhập lại.", "Đăng nhập")
}

// Dialog thông báo yêu cầu đăng nhập khi xem danh sách đơn hàng
@Composable
fun OrdersLoginDialog(onDismiss: () -> Unit, onLoginClick: () -> Unit) {
    FoodHubDialog(
        onDismissRequest = onDismiss,
        title = "Đăng nhập để tiếp tục",
        eyebrow = "Tài khoản FoodHub",
        content = {
            Text(
                "Bạn cần đăng nhập để xem và theo dõi đơn hàng của mình.",
                color = OnSurfaceVariant)
        },
        actions = {
            OutlinedButton(onClick = onDismiss, Modifier.weight(1f)) { Text("Để sau") }
            Spacer(Modifier.width(10.dp))
            Button(
                onClick = { onDismiss(); onLoginClick() }, Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = BrandDark)) { Text("Đăng nhập") }
        },
    )
}

// Dialog thông báo phiên bàn hết hạn và yêu cầu quét lại mã QR
@Composable
fun TableSessionExpiredDialog(onDismiss: () -> Unit, onScanQrClick: () -> Unit) {
    FoodHubAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Phiên bàn đã hết hạn") },
        text = { Text("Vui lòng quét lại mã QR trên bàn để tiếp tục gọi món.") },
        confirmButton = { TextButton(onClick = { onDismiss(); onScanQrClick() }) { Text("Quét lại QR") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Để sau") } },
    )
}

// Dialog thông báo lỗi khi không thể truy cập vào bàn ăn
@Composable
fun ScanTableErrorDialog(message: String, onRetry: () -> Unit) {
    FoodHubAlertDialog(
        onDismissRequest = onRetry,
        title = { Text("Không thể vào bàn", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        text = { Text(message) },
        confirmButton = { TextButton(onClick = onRetry) { Text("Quét lại") } },
    )
}

// Dialog xác nhận thao tác chung với hai nút đồng ý và hủy
@Composable
fun ConfirmActionDialog(
    title: String,
    message: String,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    dismissLabel: String = "Hủy",
    busy: Boolean = false,
    error: String? = null,
    destructive: Boolean = false,
) {
    FoodHubAlertDialog(
        onDismissRequest = { if (!busy) onDismiss() },
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(message)
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm, enabled = !busy,
                colors = ButtonDefaults.buttonColors(containerColor = if (destructive) AdminRed else BrandDark)) {
                if (busy) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                else Text(confirmLabel)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !busy) { Text(dismissLabel) } },
    )
}

// Dialog xác nhận xóa một món ăn khỏi giỏ hàng
@Composable
fun DeleteCartItemDialog(
    itemName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    busy: Boolean = false
) {
    ConfirmActionDialog(
        "Xóa món",
        "Xóa $itemName khỏi giỏ hàng?",
        "Xóa",
        onDismiss,
        onConfirm,
        busy = busy,
        destructive = true)
}

// Dialog xác nhận xóa toàn bộ các món ăn trong giỏ hàng
@Composable
fun ClearCartDialog(onDismiss: () -> Unit, onConfirm: () -> Unit, busy: Boolean = false) {
    ConfirmActionDialog(
        "Xóa toàn bộ giỏ hàng",
        "Thao tác này sẽ xóa tất cả món trong giỏ.",
        "Xóa tất cả",
        onDismiss,
        onConfirm,
        busy = busy,
        destructive = true)
}

// Dialog hiển thị tiến trình đang tải tùy chọn món ăn
@Composable
fun LoadingCartOptionsDialog(onDismiss: () -> Unit) {
    FoodHubAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Đang tải tùy chọn") },
        text = { CircularProgressIndicator() },
        confirmButton = {})
}

// Dialog xác nhận kết thúc phiên dùng bàn ăn
@Composable
fun EndTableSessionDialog(
    tableName: String, busy: Boolean, error: String?,
    onDismiss: () -> Unit, onConfirm: () -> Unit,
) {
    FoodHubDialog(
        onDismissRequest = onDismiss,
        title = "Kết thúc phiên $tableName?",
        eyebrow = "Phiên QR tại bàn",
        dismissEnabled = !busy,
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Giỏ hàng dùng chung và quyền truy cập của mọi người tại bàn sẽ được đóng. Lịch sử đơn vẫn được giữ lại.")
                error?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
            }
        },
        actions = {
            TextButton(onClick = onDismiss, enabled = !busy) { Text("Hủy") }
            Button(onClick = onConfirm, enabled = !busy) {
                if (busy) CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                else Text("Kết thúc phiên")
            }
        },
    )
}

// Dialog thông báo phiên bàn đã kết thúc và hướng dẫn quay về trang chủ
@Composable
fun TableSessionEndedDialog(onHomeClick: () -> Unit) {
    FoodHubDialog(
        onDismissRequest = onHomeClick,
        title = "Phiên bàn đã kết thúc",
        eyebrow = "Phiên phục vụ",
        content = { Text("Bạn không còn quyền chat hoặc đặt món trong phiên này. Quét lại QR khi bắt đầu lượt dùng bàn mới.") },
        actions = {
            TextButton(
                onClick = onHomeClick,
                modifier = Modifier.fillMaxWidth()) { Text("Về trang chủ") }
        },
    )
}

// Dialog thông báo đã gửi yêu cầu hỗ trợ tới nhân viên thành công
@Composable
fun SupportRequestSentDialog(tableName: String?, onDismiss: () -> Unit) {
    FoodHubDialog(
        onDismissRequest = onDismiss, title = "Đã gửi yêu cầu", eyebrow = "Hỗ trợ FoodHub",
        content = { Text(if (tableName != null) "Yêu cầu hỗ trợ đã được gửi tới nhân viên phục vụ $tableName." else "Yêu cầu hỗ trợ đơn hàng đã được gửi tới FoodHub.") },
        actions = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()) { Text("Đóng") }
        },
    )
}

// Dialog hiển thị thông tin phiên trò chuyện và nút rời/kết thúc phiên
@Composable
fun ChatSessionDialog(
    title: String, detail: String, isHost: Boolean, hasTableSession: Boolean,
    busy: Boolean, error: String?, onDismiss: () -> Unit, onLeaveSession: () -> Unit,
) {
    FoodHubDialog(
        onDismissRequest = onDismiss, title = title, eyebrow = "Thông tin phiên",
        dismissEnabled = !busy,
        content = {
            Text(detail)
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        },
        actions = {
            if (hasTableSession) TextButton(
                onClick = onLeaveSession, enabled = !busy, modifier = Modifier.weight(1f)
            ) {
                Text(
                    if (busy) "Đang xử lý..." else if (isHost) "Kết thúc phiên bàn" else "Rời phiên bàn",
                    color = MaterialTheme.colorScheme.error)
            }
            TextButton(onClick = onDismiss, modifier = Modifier.weight(1f), enabled = !busy) {
                Text(
                    "Đóng")
            }
        },
    )
}

// Dialog quản trị viên dùng để quản lý danh sách các nhóm tùy chọn của món ăn
@Composable
fun AdminVariantManager(
    itemName: String,
    groups: List<AdminVariantGroup>,
    loading: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (AdminVariantGroup) -> Unit,
) {
    FoodHubAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tùy chọn: $itemName") },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (loading) CircularProgressIndicator()
                error?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error); TextButton(onClick = onRetry) {
                    Text(
                        "Thử lại")
                }
                }
                if (!loading && error == null && groups.isEmpty()) Text("Món chưa có nhóm tùy chọn")
                groups.forEach { group -> OutlinedButton(onClick = { onEdit(group) }) { Text("Sửa ${group.input.name}") } }
                Button(onClick = onAdd, enabled = !loading && error == null) { Text("Thêm nhóm") }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Đóng") } })
}

// Dialog quản trị viên xác nhận tạo lại mã QR mới cho bàn ăn
@Composable
fun AdminRegenerateQrDialog(
    tableName: String,
    busy: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    ConfirmActionDialog(
        "Tạo lại QR $tableName?",
        "Mã QR cũ sẽ không còn dùng được. Bạn cần in hoặc dán lại mã QR mới tại bàn.",
        "Tạo QR mới",
        onDismiss,
        onConfirm,
        busy = busy,
        error = error)
}

// Dialog quản trị viên xác nhận xóa một bàn ăn khỏi hệ thống
@Composable
fun AdminDeleteTableDialog(
    tableName: String,
    busy: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    ConfirmActionDialog(
        "Xóa $tableName?",
        "Bàn sẽ bị xóa khỏi sơ đồ và mã QR hiện tại không còn sử dụng được. Lịch sử đơn hàng vẫn được giữ lại.",
        "Xóa",
        onDismiss,
        onConfirm,
        busy = busy,
        error = error,
        destructive = true)
}

// Dialog quản trị viên xác nhận xóa món ăn khỏi menu
@Composable
fun AdminDeleteMenuItemDialog(itemName: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    ConfirmActionDialog(
        "Xóa món ăn?", "$itemName sẽ bị xóa hoặc ẩn nếu đang có trong đơn hàng.",
        "Xóa", onDismiss, onConfirm, destructive = true)
}

// Dialog quản trị viên xác nhận xóa danh mục món ăn
@Composable
fun AdminDeleteCategoryDialog(
    category: AdminMenuCategory,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    ConfirmActionDialog(
        "Xóa danh mục?",
        if (category.itemCount > 0) "${category.name} đang có ${category.itemCount} món. Hãy chuyển hoặc xóa các món trước."
        else "Danh mục ${category.name} sẽ bị xóa khỏi thực đơn.",
        "Xóa", onDismiss, onConfirm, destructive = true)
}

// Dialog quản trị viên xác nhận xóa mã giảm giá
@Composable
fun AdminDeleteVoucherDialog(
    code: String,
    busy: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    ConfirmActionDialog(
        "Xóa voucher $code?",
        "Voucher đã có lịch sử sử dụng sẽ không thể xóa; bạn có thể tắt voucher thay thế.",
        "Xóa",
        onDismiss,
        onConfirm,
        busy = busy,
        destructive = true)
}

// Dialog quản trị viên xác nhận xóa một đánh giá của khách hàng
@Composable
fun AdminDeleteReviewDialog(
    customerName: String,
    itemName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    FoodHubAlertDialog(
        onDismissRequest = onDismiss, icon = { Icon(TablerIcons.Delete, null, tint = AdminRed) },
        title = { Text("Xóa đánh giá?") },
        text = { Text("Đánh giá của $customerName cho món $itemName sẽ bị xóa khỏi hệ thống.") },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Xóa", color = AdminRed) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Giữ lại") } },
    )
}

// Dialog quản trị viên xác nhận xóa toàn bộ cuộc trò chuyện
@Composable
fun AdminDeleteConversationDialog(
    customerName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    FoodHubDialog(
        onDismissRequest = onDismiss,
        title = "Xóa toàn bộ cuộc trò chuyện?",
        eyebrow = "Quản trị hội thoại",
        content = { Text("Toàn bộ lịch sử tin nhắn với $customerName sẽ bị xóa vĩnh viễn. Không thể xóa riêng từng tin nhắn.") },
        actions = {
            TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Giữ lại") }
            TextButton(
                onClick = onConfirm,
                modifier = Modifier.weight(1f)) { Text("Xóa cuộc trò chuyện", color = AdminRed) }
        },
    )
}

// Dialog quản trị viên xác nhận tiếp nhận hoặc nhập lý do từ chối đơn hàng
@Composable
fun AdminOrderActionDialog(
    order: AdminOrder, approve: Boolean, busy: Boolean,
    onDismiss: () -> Unit, onConfirm: () -> Unit, onReject: (String) -> Unit,
) {
    var rejectReason by rememberSaveable(order.id) { mutableStateOf("") }
    FoodHubDialog(
        onDismissRequest = onDismiss, dismissEnabled = !busy,
        title = if (approve) "Xác nhận đơn ${order.code}?" else "Từ chối đơn ${order.code}?",
        eyebrow = "Xử lý đơn hàng",
        content = {
            if (approve) Text(
                if (order.paymentMethod == AdminPaymentMethod.CASH && !order.paid)
                    "Xác nhận tiếp nhận đơn. Tiền mặt sẽ được ghi nhận sau khi món đã phục vụ cho khách."
                else "Đơn sẽ được chuyển sang trạng thái đã xác nhận."
            ) else OutlinedTextField(
                rejectReason, { rejectReason = it.take(255) },
                label = { Text("Lý do từ chối") }, minLines = 2, enabled = !busy)
        },
        actions = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                enabled = !busy) { Text("Đóng") }
            Button(
                onClick = { if (approve) onConfirm() else onReject(rejectReason.trim()) },
                modifier = Modifier.weight(1f),
                enabled = !busy && (approve || rejectReason.isNotBlank())
            ) { Text(if (approve) "Xác nhận" else "Từ chối") }
        },
    )
}

// Dialog hiển thị chi tiết thông tin thanh toán của đơn hàng
@Composable
fun PaymentDetailDialog(
    loading: Boolean, error: String?, method: String?, status: String?,
    amount: Long?, paidAt: String?, onDismiss: () -> Unit, onRetry: () -> Unit,
) {
    FoodHubAlertDialog(
        onDismissRequest = onDismiss, title = { Text("Chi tiết thanh toán") },
        text = {
            Column {
                if (loading) CircularProgressIndicator()
                error?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error); TextButton(onClick = onRetry) {
                    Text(
                        "Thử lại")
                }
                }
                if (!loading && error == null) {
                    if (method == null) Text("Đơn chưa có thông tin thanh toán") else {
                        Text(
                            "Phương thức: " + when (method) {
                                "CASH" -> "Tiền mặt"; "VNPAY" -> "VNPay"; else -> method
                            })
                        Text(
                            "Trạng thái: " + when (status) {
                                "PAID" -> "Đã thanh toán"
                                "PENDING", "UNPAID" -> "Chờ thanh toán"
                                "FAILED" -> "Thanh toán thất bại"
                                "REFUNDED" -> "Đã hoàn tiền"
                                else -> status.orEmpty()
                            })
                        Text("Số tiền: ${money(amount ?: 0)}")
                        paidAt?.let { Text("Thanh toán lúc: $it") }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Đóng") } },
    )
}

// Dialog cho phép khách hàng lựa chọn hình thức nhận món (Ăn tại bàn, Mang về, Giao hàng)
@Composable
fun OrderModeDialog(
    selectedType: OrderType,
    tableSession: HomeTableSession?,
    isLoggedIn: Boolean,
    onSelect: (OrderType) -> Unit,
    onEndTableSession: () -> Unit,
    onDismiss: () -> Unit,
) {
    FoodHubDialog(
        onDismissRequest = { onDismiss() },
        title = "Bạn muốn nhận món theo cách nào?",
        eyebrow = "Khởi tạo đặt món",
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OrderModeChoice(
                    "Ăn tại bàn (Quét mã QR)",
                    tableSession?.let {
                        "Đang kết nối: ${it.tableName}${
                            it.floor?.let { floor -> " ($floor)" }
                                .orEmpty()
                        }"
                    } ?: "Quét QR trên bàn để bắt đầu",
                    selectedType == OrderType.DINE_IN) {
                    onDismiss(); onSelect(OrderType.DINE_IN)
                }
                OrderModeChoice(
                    "Nhận tại quầy (Takeaway)",
                    "Cho phép khách vãng lai, không bắt buộc đăng nhập",
                    selectedType == OrderType.TAKEAWAY) {
                    onDismiss(); onSelect(OrderType.TAKEAWAY)
                }
                OrderModeChoice(
                    "Giao tận nơi (Delivery)",
                    if (isLoggedIn) "Giao tới địa chỉ của bạn" else "Yêu cầu đăng nhập",
                    selectedType == OrderType.DELIVERY,
                    enabled = isLoggedIn) {
                    onDismiss(); onSelect(OrderType.DELIVERY)
                }
            }
        },
        actions = {
            if (tableSession != null) {
                TextButton(
                    onClick = { onDismiss(); onEndTableSession() },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        if (tableSession.isHost) "Kết thúc phiên" else "Rời phiên",
                        color = MaterialTheme.colorScheme.error)
                }
            }
            TextButton(onClick = { onDismiss() }, modifier = Modifier.weight(1f)) {
                Text(
                    "Đóng")
            }
        },
    )

}

// Thẻ lựa chọn một hình thức nhận món trong OrderModeDialog
@Composable
private fun OrderModeChoice(
    title: String,
    detail: String,
    selected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = if (selected) BrandSoft.copy(alpha = .45f) else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            if (selected) 2.dp else 1.dp,
            if (selected) Brand else Color(0xFFE4E0DE)),
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                color = if (enabled) Neutral else OnSurfaceVariant)
            Text(detail, fontSize = 11.sp, color = OnSurfaceVariant)
        }
    }
}

// Dialog cho phép người dùng chỉnh sửa thông tin cá nhân và ảnh đại diện
@Composable
fun ProfileEditDialog(
    user: com.example.foodhub_app.feature.shared.auth.model.UserDto?,
    isUploadingAvatar: Boolean,
    isUpdatingProfile: Boolean,
    errorMessage: String?,
    onUploadAvatar: (Uri, (String) -> Unit) -> Unit,
    onUpdateProfile: (String, String, String, String, () -> Unit) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember(user?.name) { mutableStateOf(user?.name.orEmpty()) }
    var phone by remember(user?.phoneNumber) { mutableStateOf(user?.phoneNumber.orEmpty()) }
    var dateOfBirth by remember(user?.dateOfBirth) { mutableStateOf(user?.dateOfBirth.orEmpty()) }
    var avatarUrl by remember(user?.avatarUrl) { mutableStateOf(user?.avatarUrl.orEmpty()) }
    val avatarPicker =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { onUploadAvatar(it) { uploadedUrl -> avatarUrl = uploadedUrl } }
        }
    FoodHubDialog(
        onDismissRequest = { if (!isUploadingAvatar && !isUpdatingProfile) onDismiss() },
        title = "Thông tin cá nhân",
        eyebrow = "Hồ sơ tài khoản",
        dismissEnabled = !isUploadingAvatar && !isUpdatingProfile,
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                errorMessage?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp)
                }
                Box(
                    Modifier
                        .size(76.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    ProfileAvatar(avatarUrl)
                }
                OutlinedTextField(
                    name,
                    { name = it },
                    label = { Text("Họ tên") },
                    singleLine = true)
                OutlinedTextField(
                    phone,
                    {
                        phone = it.filter(Char::isDigit)
                            .take(11)
                    },
                    label = { Text("Số điện thoại") },
                    singleLine = true)
                OutlinedTextField(
                    dateOfBirth,
                    { value ->
                        dateOfBirth = value.filter { it.isDigit() || it == '-' }
                            .take(10)
                    },
                    label = { Text("Ngày sinh (YYYY-MM-DD)") },
                    singleLine = true,
                )
                OutlinedButton(
                    onClick = {
                        avatarPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    enabled = !isUploadingAvatar,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (isUploadingAvatar) {
                        CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(if (isUploadingAvatar) "Đang tải ảnh..." else "Chọn ảnh từ điện thoại")
                }
                OutlinedTextField(
                    avatarUrl,
                    { avatarUrl = it },
                    label = { Text("Link ảnh đại diện") },
                    singleLine = true,
                )
            }
        },
        actions = {
            OutlinedButton(
                onClick = { onDismiss() },
                modifier = Modifier.weight(1f),
                enabled = !isUploadingAvatar && !isUpdatingProfile,
            ) { Text("Hủy") }
            Spacer(Modifier.width(10.dp))
            Button(
                onClick = { onUpdateProfile(name, phone, dateOfBirth, avatarUrl) { onDismiss() } },
                modifier = Modifier.weight(1f),
                enabled = name.isNotBlank() && !isUploadingAvatar && !isUpdatingProfile,
            ) {
                if (isUpdatingProfile) {
                    CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                }
                Text(if (isUpdatingProfile) "Đang lưu..." else "Lưu")
            }
        },
    )
}

// Khung hiển thị ảnh đại diện người dùng dạng hình tròn
@Composable
private fun ProfileAvatar(avatarUrl: String?) {
    coil.compose.AsyncImage(
        model = com.example.foodhub_app.core.network.resolveMediaUrl(avatarUrl),
        fallback = painterResource(com.example.foodhub_app.R.drawable.avatar_user),
        error = painterResource(com.example.foodhub_app.R.drawable.avatar_user),
        placeholder = painterResource(com.example.foodhub_app.R.drawable.avatar_user),
        contentDescription = "Ảnh đại diện",
        modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape),
        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
    )
}

// Helpers shared by the dialogs above.
private fun money(value: Long): String =
    NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN"))
        .format(value) + "đ"

private fun OrderType.label(): String = when (this) {
    OrderType.DINE_IN -> "Tại bàn"
    OrderType.TAKEAWAY -> "Mang về"
    OrderType.DELIVERY -> "Giao hàng"
}

private data class OptionDraft(val id: String?, val name: String, val price: String)

private val VIETNAM_TIME_ZONE: TimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")

// Use Android 24-compatible date APIs; reject invalid or partially parsed input.
private fun parseVoucherDate(value: String): Date? {
    if (!Regex("[0-9]{4}-[0-9]{2}-[0-9]{2}").matches(value)) return null
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        isLenient = false
        timeZone = VIETNAM_TIME_ZONE
    }
    val position = ParsePosition(0)
    return formatter.parse(value, position)?.takeIf { position.index == value.length }
}

private fun voucherTimestamp(date: Date, endOfDay: Boolean = false): String {
    val timestamp = Calendar.getInstance(VIETNAM_TIME_ZONE, Locale.US).apply {
        time = date
        if (endOfDay) {
            add(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MILLISECOND, -1)
        }
    }.time
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(timestamp)
}
private fun String.numeric(decimal: Boolean = false): String =
    filter { it.isDigit() || decimal && it == '.' }

private fun Double.clean(): String =
    if (this % 1.0 == 0.0) toLong().toString() else toString()

private fun String.typeLabel(): String = when (this) {
    "DINE_IN" -> "Tại bàn"
    "DELIVERY" -> "Giao hàng"
    else -> "Mang về"
}

private val AdminPrimary = Color(0xFF087F79)
private val AdminPrimarySoft = Color(0xFFE4F7F3)
private val AdminSurface = Color.White
private val AdminMuted = Color(0xFF5E726E)
private val AdminRed = Color(0xFFC7382B)
private val AdminGreen = Color(0xFF087F79)

private fun createQrBitmap(content: String): Bitmap = content.toQrBitmap(768)

private fun String.toQrBitmap(size: Int): Bitmap {
    require(isNotBlank()) { "Nội dung QR không được để trống" }
    val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
        put(EncodeHintType.MARGIN, 1)
        put(EncodeHintType.CHARACTER_SET, "UTF-8")
    }
    val matrix: BitMatrix = MultiFormatWriter().encode(
        this, BarcodeFormat.QR_CODE, size, size, hints
    )
    val pixels = IntArray(size * size)
    for (y in 0 until size) {
        for (x in 0 until size) {
            pixels[y * size + x] = if (matrix[x, y]) 0xFF111111.toInt() else 0xFFFFFFFF.toInt()
        }
    }
    return createBitmap(size, size, Bitmap.Config.ARGB_8888).apply {
        setPixels(pixels, 0, size, 0, 0, size, size)
    }
}
