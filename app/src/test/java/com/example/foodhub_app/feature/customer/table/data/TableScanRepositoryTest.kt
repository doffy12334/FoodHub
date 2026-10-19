package com.example.foodhub_app.feature.customer.table.data

import org.junit.Assert.assertEquals
import org.junit.Test

class TableScanRepositoryTest {

    @Test
    fun testRawToken() {
        val rawToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        val result = extractQrToken(rawToken)
        assertEquals("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9", result)
    }

    @Test
    fun testUrlToken() {
        val url = "https://foodhub.vn/scan?qrToken=token_12345&table=12"
        val result = extractQrToken(url)
        assertEquals("token_12345", result)
    }

    @Test
    fun testJsonToken() {
        val json = """{"qrToken": "json_token_999", "tableId": "12"}"""
        val result = extractQrToken(json)
        assertEquals("json_token_999", result)
    }

    @Test
    fun testEmptyToken() {
        val result = extractQrToken("   ")
        assertEquals("", result)
    }
}
