package com.example.foodhub_app.core.network

import android.net.Uri
import androidx.core.net.toUri

fun resolveMediaUrl(url: String?): String? {
    val value = url?.trim()?.takeIf(String::isNotBlank) ?: return null
    val uri = runCatching { value.toUri() }.getOrNull() ?: return value
    if (!uri.host.orEmpty().endsWith("r2.cloudflarestorage.com", ignoreCase = true)) return value

    val fileName = uri.pathSegments.lastOrNull()?.takeIf(String::isNotBlank) ?: return value
    return "https://foodhub-8lv1.onrender.com/api/v1/media/file/${Uri.encode(fileName)}"
}
