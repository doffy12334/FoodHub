package com.example.foodhub_app.feature.customer.table.data

import androidx.compose.animation.unveilIn
import com.example.foodhub_app.core.datastore.TokenStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URI
import java.net.URLDecoder
import javax.inject.Inject

class TableScanRepository @Inject constructor(
    private val tableApi: TableApi,
    private val tokenStore: TokenStore
) {

    //Gọi API quét mã và trả về Rusult chứa TableSession
    suspend fun scanQrCode(rawQrValue: String): Result<TableSession> = withContext(Dispatchers.IO) {
        runCatching {
            val qrToken = extractQrToken(rawQrValue)
            if (qrToken.isBlank()) {
                throw IllegalArgumentException("Mã QR không chứa thông tin bàn hợp lệ")
            }
            val response = tableApi.scanTable(TableScanRequest(qrToken))
            val data =
                response.data ?: throw IllegalStateException("Không nhận được dữ liệu từ API")
            val tableId = data.table?.id ?: data.tableId.orEmpty()
            val tableName = data.table?.name ?: data.tableName ?: "Bàn đã quét"
            val floor = data.table?.floor ?: data.floor
            val capacity = data.table?.capacity ?: data.capacity

            val session = TableSession(
                tableId = tableId,
                tableName = tableName,
                floor = floor,
                capacity = capacity,
                tableToken = data.tableToken,
                sessionId = data.sessionId,
                expiresAtEpochMillis = data.expiresIn?.let {
                    System.currentTimeMillis() + it * 1000L
                },
                isHost = data.isHost
            )
            tokenStore.saveTableSession(
                com.example.foodhub_app.feature.customer.home.viewmodel.HomeTableSession(
                    tableId = session.tableId,
                    tableName = session.tableName,
                    floor = session.floor,
                    capacity = session.capacity,
                    tableToken = session.tableToken,
                    sessionId = session.sessionId,
                    expiresAtEpochMillis = session.expiresAtEpochMillis,
                    isHost = session.isHost
                )
            )
            session
        }
    }

    // Kết thúc phiên bàn
    suspend fun endSession(tableToken: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            tableApi.endSession(tableToken)
            tokenStore.saveTableSession(null)
            Unit
        }
    }
}

// Bóc tách QR (JSON, URL hoặc Token thô)
internal fun extractQrToken(rawValue: String): String {
    val value = rawValue.trim()
    if (value.isBlank()) return ""

    // 1. Dạng chuỗi JSON
    runCatching {
        val jsonObj = org.json.JSONObject(value)
        val token = jsonObj.optString("qrToken").ifBlank { jsonObj.optString("token") }
        if (token.isNotBlank()) return token
    }

    // 2. Dạng URL Query Parameter (ví dụ: https://foodhub.com/scan?qrToken=xxx)
    val query = runCatching { URI(value).rawQuery }.getOrNull()
    query?.split("&")
        ?.mapNotNull { param ->
            val parts = param.split("=", limit = 2)
            if (parts.size == 2) {
                val key = URLDecoder.decode(parts[0], "UTF-8")
                val token = URLDecoder.decode(parts[1], "UTF-8")
                key to token
            } else null
        }
        ?.firstOrNull { (key, token) ->
            key in setOf("qrToken", "qr_token", "token") && token.isNotBlank()
        }?.second?.let { return it }

    // 3. Chuỗi Token thô
    return value
}