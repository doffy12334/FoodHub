package com.example.foodhub_app.feature.customer.table.data

import com.example.foodhub_app.core.network.ApiResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface TableApi {
    @POST("table/scan")
    suspend fun scanTable(
    @Body request : TableScanRequest
    ): ApiResponse<TableScanResponse>

    @POST("table/session/end")
    suspend fun endSession(
        @Header("X-Table-Token") tableToken : String
    ) : ApiResponse<Unit>
}