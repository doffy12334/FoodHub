package com.example.foodhub_app.feature.customer.table.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview as ComposePreview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodhub_app.feature.customer.table.data.TableSession
import com.example.foodhub_app.feature.customer.table.viewmodel.QrScanUiState
import com.example.foodhub_app.feature.customer.table.viewmodel.QrScanViewModel
import com.example.foodhub_app.feature.shared.ui.FoodHubAlertDialog
import com.example.foodhub_app.ui.theme.Brand
import com.example.foodhub_app.ui.theme.Foodhub_AppTheme
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

// Route chính kết nối QrScanViewModel và điều hướng khi quét thành công
@Composable
fun QrScanRoute(
    onBackClick: () -> Unit,
    onScanSuccess: (TableSession) -> Unit,
    viewModel: QrScanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    QrScanScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onToggleFlash = viewModel::toggleFlash,
        onQrScanned = viewModel::onQrCodeScanned,
        onConfirmSession = { session ->
            onScanSuccess(session)
        },
        onErrorDismiss = viewModel::clearError,
        onResetScan = viewModel::resetScan,
        onCameraError = viewModel::setError
    )
}

// Màn hình giao diện quét mã QR bao gồm camera, khung overlay và các công cụ
@Composable
fun QrScanScreen(
    uiState: QrScanUiState,
    onBackClick: () -> Unit,
    onToggleFlash: () -> Unit,
    onQrScanned: (String) -> Unit,
    onConfirmSession: (TableSession) -> Unit,
    onErrorDismiss: () -> Unit,
    onResetScan: () -> Unit = {},
    onCameraError: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var scanRevision by remember { mutableIntStateOf(0) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            scanQrFromImage(context, it) { rawValue ->
                if (rawValue != null) {
                    onQrScanned(rawValue)
                } else {
                    onCameraError("Không tìm thấy mã QR hợp lệ trong hình ảnh đã chọn")
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF17181B))) {
        Column(
            Modifier
                .fillMaxSize()
                .safeDrawingPadding()) {
            // Header Top Bar
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xCC121417), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Close,
                        "Đóng",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp))
                }

                Text(
                    "Quét mã QR bàn",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp)

                IconButton(
                    onClick = onToggleFlash,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (uiState.isFlashOn) GoldColor else Color(0xCC121417), CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (uiState.isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flash",
                        tint = if (uiState.isFlashOn) Color.Black else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Hướng dẫn
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Hướng camera vào mã QR dán trên bàn ăn",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(16.dp))

            // Khung Camera Scan
            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                ScannerFrame(
                    hasCameraPermission = hasCameraPermission,
                    isFlashOn = uiState.isFlashOn,
                    scanEnabled = !uiState.isProcessing && uiState.scannedSession == null,
                    scanRevision = scanRevision,
                    onQrDetected = onQrScanned,
                    onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    onCameraError = onCameraError
                )

                if (uiState.isProcessing) {
                    Box(
                        Modifier
                            .size(80.dp)
                            .background(Color(0xCC17181B), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Brand, modifier = Modifier.size(36.dp))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Công cụ tác vụ bên dưới
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ScanTool(Icons.Default.Image, "Thư viện ảnh")
                { galleryLauncher.launch("image/*") }
                ScanTool(Icons.Default.Refresh, "Quét lại")
                {
                    onResetScan()
                    scanRevision++
                }
            }

            Spacer(Modifier.height(16.dp))

            // Thẻ kết quả phiên bàn
            TableSessionCard(
                session = uiState.scannedSession,
                isLoading = uiState.isProcessing,
                onContinue = {
                    uiState.scannedSession?.let { onConfirmSession(it) }
                }
            )
        }
    }

    // Dialog thông báo khi lỗi
    uiState.errorMessage?.let { message ->
        FoodHubAlertDialog(
            onDismissRequest = onErrorDismiss,
            title = { Text("Lỗi quét bàn", fontWeight = FontWeight.Bold) },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = onErrorDismiss) {
                    Text("Quét lại", color = Brand, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// Thẻ hiển thị kết quả bàn ăn vừa nhận diện và nút xác nhận vào bàn
@Composable
private fun TableSessionCard(
    session: TableSession?,
    isLoading: Boolean,
    onContinue: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(topStart = 27.dp, topEnd = 27.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconCircle(Icons.Default.TableRestaurant, 36.dp, Brand.copy(alpha = 0.15f), Brand)
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                Text("PHIÊN DÙNG BỮA", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Text(session?.tableName ?: "Chưa kết nối bàn", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Box(
                Modifier
                    .background(
                        if (session == null) Color(0xFFF0F0F2) else Brand.copy(alpha = 0.15f),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (session != null) {
                        Dot(Brand, 6.dp)
                        Spacer(Modifier.size(5.dp))
                    }
                    Text(
                        if (session == null) "Chờ quét" else "Đã kết nối",
                        fontSize = 10.sp,
                        color = if (session == null) Color.Gray else Brand,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .background(Brand.copy(alpha = 0.08f), RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(48.dp).background(Brand, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.QrCode, null, Modifier.size(26.dp), tint = Color.White)
                }
                Column(Modifier.padding(start = 12.dp)) {
                    Text(
                        if (session == null) "Hướng camera vào mã QR" else "Đã xác thực với máy chủ",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    Text(
                        session?.tableName ?: "Đang chờ nhận diện bàn",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        listOfNotNull(session?.floor, session?.capacity?.let { "$it chỗ" })
                            .joinToString(" · ")
                            .ifBlank { "Thông tin bàn sẽ xuất hiện sau khi quét" },
                        fontSize = 11.sp,
                        color = Color.DarkGray
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onContinue,
                enabled = session != null && !isLoading,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Brand),
                shape = RoundedCornerShape(13.dp)
            ) {
                Text(
                    if (isLoading) "Đang kết nối..." else "Xác nhận vào bàn ngay",
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                if (!isLoading) {
                    Spacer(Modifier.size(7.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.size(18.dp), tint = Color.White)
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CheckCircle, null, Modifier.size(15.dp), tint = Brand)
            Spacer(Modifier.size(6.dp))
            Text("Phiên bàn được bảo vệ bằng mã xác thực riêng", fontSize = 11.sp, color = Color.Gray)
        }
    }
}

// Nút công cụ chọn ảnh từ thư viện hoặc quét lại
@Composable
private fun ScanTool(icon: ImageVector, name: String, onClick: () -> Unit) {
    Column(
        Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally) {
        IconCircle(icon, 42.dp, Color(0xFF303135), Color.White)
        Spacer(Modifier.height(4.dp))
        Text(name, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}

// Khung chứa CameraPreview và khung vuông định vị quét QR
@Composable
private fun ScannerFrame(
    hasCameraPermission: Boolean,
    isFlashOn: Boolean,
    scanEnabled: Boolean,
    scanRevision: Int,
    onQrDetected: (String) -> Unit,
    onRequestPermission: () -> Unit,
    onCameraError: (String) -> Unit = {}
) {
    Box(
        Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF242529)),
        contentAlignment = Alignment.Center
    ) {
        if (androidx.compose.ui.platform.LocalInspectionMode.current) {
            Text("Camera Preview", color = Color.White)
        } else if (hasCameraPermission) {
            QrCameraPreview(
                isFlashOn = isFlashOn,
                scanEnabled = scanEnabled,
                scanRevision = scanRevision,
                onQrDetected = onQrDetected,
                onCameraError = onCameraError
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Cần quyền Camera để quét mã QR", color = Color.White)
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onRequestPermission) {
                    Text("Cấp quyền Camera", color = Brand, fontWeight = FontWeight.Bold)
                }
            }
        }

        ScannerOverlay(Modifier.fillMaxSize())
    }
}

// Tích hợp CameraX và ML Kit Barcode Scanning để tự động đọc mã QR
@Composable
@SuppressLint("UnsafeOptInUsageError")
private fun QrCameraPreview(
    isFlashOn: Boolean,
    scanEnabled: Boolean,
    scanRevision: Int,
    onQrDetected: (String) -> Unit,
    onCameraError: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentScanEnabled by rememberUpdatedState(scanEnabled)
    val currentOnQrDetected by rememberUpdatedState(onQrDetected)
    val currentOnCameraError by rememberUpdatedState(onCameraError)

    var camera by remember { mutableStateOf<androidx.camera.core.Camera?>(null) }

    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }
    val analyzerExecutor = remember { Executors.newSingleThreadExecutor() }
    val scanner = remember {
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )
    }
    val processing = remember { AtomicBoolean(false) }
    val delivered = remember { AtomicBoolean(false) }

    LaunchedEffect(scanRevision) { delivered.set(false) }

    LaunchedEffect(isFlashOn, camera) {
        val activeCamera = camera ?: return@LaunchedEffect
        if (activeCamera.cameraInfo.hasFlashUnit()) {
            runCatching { activeCamera.cameraControl.enableTorch(isFlashOn) }
        }
    }

    DisposableEffect(lifecycleOwner, previewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        var provider: ProcessCameraProvider? = null

        cameraProviderFuture.addListener({
            runCatching {
                provider = cameraProviderFuture.get()
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }
                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                analysis.setAnalyzer(analyzerExecutor) { imageProxy ->
                    if (!currentScanEnabled || delivered.get() || !processing.compareAndSet(
                            false,
                            true)) {
                        imageProxy.close()
                        return@setAnalyzer
                    }
                    val mediaImage = imageProxy.image
                    if (mediaImage == null) {
                        processing.set(false)
                        imageProxy.close()
                        return@setAnalyzer
                    }
                    val inputImage =
                        InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                    scanner.process(inputImage)
                        .addOnSuccessListener { barcodes ->
                            val value =
                                barcodes.firstNotNullOfOrNull { it.rawValue?.takeIf(String::isNotBlank) }
                            if (value != null && delivered.compareAndSet(false, true)) {
                                currentOnQrDetected(value)
                            }
                        }
                        .addOnCompleteListener {
                            processing.set(false)
                            imageProxy.close()
                        }
                }

                provider?.unbindAll()
                camera = provider?.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analysis
                )
            }.onFailure {
                currentOnCameraError("Không thể mở Camera. Vui lòng kiểm tra quyền truy cập Camera.")
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            provider?.unbindAll()
            analyzerExecutor.shutdown()
            scanner.close()
        }
    }

    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
}

// Khung viền overlay vẽ 4 góc định vị màu vàng rực rỡ
@Composable
private fun ScannerOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val x0 = w * .07f
        val x1 = w * .93f
        val y0 = h * .06f
        val y1 = h * .94f
        val len = 22.dp.toPx()
        val stroke = 4.dp.toPx()

        listOf(
            Offset(x0, y0 + len) to Offset(x0, y0),
            Offset(x0, y0) to Offset(x0 + len, y0),
            Offset(x1 - len, y0) to Offset(x1, y0),
            Offset(x1, y0) to Offset(x1, y0 + len),
            Offset(x0, y1 - len) to Offset(x0, y1),
            Offset(x0, y1) to Offset(x0 + len, y1),
            Offset(x1 - len, y1) to Offset(x1, y1),
            Offset(x1, y1) to Offset(x1, y1 - len)
        ).forEach { (start, end) -> drawLine(GoldColor, start, end, stroke, cap = StrokeCap.Round) }

        drawRect(
            Color(0x66FFFFFF),
            topLeft = Offset(w * .18f, h * .20f),
            size = androidx.compose.ui.geometry.Size(w * .64f, h * .60f),
            style = Stroke(1.dp.toPx())
        )
    }
}

// Giải mã và đọc mã QR từ tệp ảnh được chọn trong thư viện
private fun scanQrFromImage(context: Context, uri: Uri, onResult: (String?) -> Unit) {
    val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    )
    runCatching { InputImage.fromFilePath(context, uri) }
        .onSuccess { image ->
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    onResult(barcodes.firstNotNullOfOrNull { it.rawValue?.takeIf(String::isNotBlank) })
                }
                .addOnFailureListener { onResult(null) }
                .addOnCompleteListener { scanner.close() }
        }
        .onFailure {
            scanner.close()
            onResult(null)
        }
}


@ComposePreview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun QrScanScreenPreview() {
    Foodhub_AppTheme {
        QrScanScreen(
            uiState = QrScanUiState(),
            onBackClick = {},
            onToggleFlash = {},
            onQrScanned = {},
            onConfirmSession = {},
            onErrorDismiss = {}
        )
    }
}
