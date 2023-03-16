package com.vitorpamplona.amethyst.ui.qrcode
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.util.Log
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.vitorpamplona.amethyst.service.Nip19
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun DIDQrCodeScanner(onScan: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    val cameraExecutor= Executors.newSingleThreadExecutor()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    val analyzer = DIDQRCodeAnalyzer { result ->
        result?.let {
            Log.d("wangran", "DIDQrCodeScanner:11 result = "+it)
            try {
//                val nip19 = Nip19().uriToRoute(it)
//                val startingPage = when (nip19?.type) {
//                    Nip19.Type.USER -> "User/${nip19.hex}"
//                    Nip19.Type.NOTE -> "Note/${nip19.hex}"
//                    else -> null
//                }
//
//                if (startingPage != null) {
//                    onScan(startingPage)
//                }
                if (it != null){
                    Log.d("wangran", "DIDQrCodeScanner:22 result = "+it)
                    onScan(it)
                }
            } catch (e: Throwable) {
                // QR can be anythign. do not throw errors.
            }
        }
    }

    DisposableEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
        onDispose() {
            cameraProviderFuture.get().unbindAll()
            cameraExecutor.shutdown()
        }
    }

    Column() {
        if (hasCameraPermission) {
            AndroidView(
                factory = { context ->
                    val previewView = PreviewView(context)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        bindPreview(analyzer, previewView, cameraExecutor, cameraProvider, lifecycleOwner)
                    }, ContextCompat.getMainExecutor(context))

                    return@AndroidView previewView
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

//fun bindPreview(
//    analyzer: ImageAnalysis.Analyzer,
//    previewView: PreviewView,
//    cameraExecutor: ExecutorService,
//    cameraProvider: ProcessCameraProvider,
//    lifecycleOwner: LifecycleOwner
//) {
//    val preview = Preview.Builder().build()
//
//    val selector = CameraSelector.Builder()
//        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//        .build()
//
//    preview.setSurfaceProvider(previewView.surfaceProvider)
//
//    val imageAnalysis = ImageAnalysis.Builder()
//        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//        .build()
//
//    imageAnalysis.setAnalyzer(
//        cameraExecutor,
//        analyzer
//    )
//
//    cameraProvider.bindToLifecycle(
//        lifecycleOwner,
//        selector,
//        imageAnalysis,
//        preview
//    )
//}

class DIDQRCodeAnalyzer(
    private val onQrCodeScanned: (result: String?) -> Unit
) : ImageAnalysis.Analyzer {

    private val scanningOptions = BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build()

    fun scanBarcodes(inputImage: InputImage, imageProxy:ImageProxy) {
//        Log.d("wangran", "scanBarcodes0000: barcodes"+inputImage)

        BarcodeScanning.getClient(scanningOptions).process(inputImage)
            .addOnSuccessListener { barcodes ->
                Log.d("wangran", "scanBarcodes: barcodes"+barcodes.toString())

                if (barcodes.isNotEmpty()) {
                    Log.d("wangran", "wangran scanBarcodes: barcodes"+barcodes.toString())
                    onQrCodeScanned(barcodes[0].displayValue)
                }
            }
            .addOnFailureListener {
                Log.d("wangran", "eeeeeeeeeeeeeE")

                it.printStackTrace()
            }
            .addOnCompleteListener{
//                imageProxy.close();
            }
    }

    fun test(){

    }

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image?.let { image ->
            val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
            scanBarcodes(inputImage,imageProxy)
        }
//        imageProxy.close()
    }
}
