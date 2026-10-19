package com.example.foodhub_app.feature.customer.table.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TableSession(
    val tableId: String,
    val tableName: String,
    val floor: String? = null,
    val capacity: Int? = null,
    val tableToken: String,
    val sessionId: String? = null,
    val expiresAtEpochMillis: Long? = null,
    val isHost: Boolean = false,
)

@Serializable
data class TableScanRequest(
    val qrToken: String
)

@Serializable
data class TableInfoDto(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("floor") val floor: String? = null,
    @SerialName("capacity") val capacity: Int? = null
)

@Serializable
data class TableScanResponse(
    val tableToken: String = "",
    val tableId: String? = null,
    val tableName: String? = null,
    val floor: String? = null,
    val capacity: Int? = null,
    val sessionId: String? = null,
    val expiresIn: Long? = null,
    val isHost: Boolean = false,
    val table: TableInfoDto? = null
)
