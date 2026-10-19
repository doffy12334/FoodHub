package com.example.foodhub_app.feature.customer.table.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal val InkColor = Color(0xFF181C22)
internal val BurntOrangeColor = Color(0xFFA73400)
internal val GoldColor = Color(0xFFFFB600)

// Hiển thị icon nằm trong khung hình tròn có màu nền tùy chỉnh
@Composable
internal fun IconCircle(
    icon: ImageVector,
    size: Dp,
    background: Color,
    foreground: Color,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(background, CircleShape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = foreground,
            modifier = Modifier.size(size * 0.5f)
        )
    }
}

// Hiển thị một chấm tròn màu nhỏ làm điểm nhấn
@Composable
internal fun Dot(color: Color, size: Dp = 5.dp) {
    Box(Modifier.size(size).background(color, CircleShape))
}
