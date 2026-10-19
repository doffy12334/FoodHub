package com.example.foodhub_app.feature.customer.table.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_app.feature.customer.table.data.TableScanRepository
import com.example.foodhub_app.feature.customer.table.data.TableSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Trạng thái giao diện màn hình quét mã QR bàn ăn
data class QrScanUiState(
    val isProcessing: Boolean = false,
    val isFlashOn: Boolean = false,
    val errorMessage: String? = null,
    val scannedSession: TableSession? = null
)

// ViewModel quản lý logic bật/tắt flash và quét mã QR bàn ăn
@HiltViewModel
class QrScanViewModel @Inject constructor(
    private val repository: TableScanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QrScanUiState())
    val uiState: StateFlow<QrScanUiState> = _uiState.asStateFlow()

    // Bật hoặc tắt đèn Flash của camera
    fun toggleFlash() {
        _uiState.update { it.copy(isFlashOn = !it.isFlashOn) }
    }

    // Gọi repository xử lý mã QR sau khi camera phát hiện chuỗi QR
    fun onQrCodeScanned(rawValue: String) {
        if (_uiState.value.isProcessing) return
        _uiState.update { it.copy(isProcessing = true, errorMessage = null) }
        viewModelScope.launch {
            repository.scanQrCode(rawValue)
                .onSuccess { session ->
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            scannedSession = session
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            errorMessage = error.message ?: "Mã QR bàn không hợp lệ"
                        )
                    }
                }
        }
    }

    // Xóa thông báo lỗi hiện tại
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // Reset kết quả quét, xóa thông báo lỗi và cờ xử lý
    fun resetScan() {
        _uiState.update {
            it.copy(
                isProcessing = false,
                errorMessage = null,
                scannedSession = null
            )
        }
    }

    // Đặt thông báo lỗi khi không đọc được ảnh hoặc lỗi camera
    fun setError(message: String) {
        _uiState.update {
            it.copy(
                isProcessing = false,
                errorMessage = message
            )
        }
    }
}
