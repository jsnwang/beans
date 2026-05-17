package com.moo.beans.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import com.moo.beans.data.LineItem
import com.moo.beans.data.ReceiptParser
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCSignatureOverride
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureDevicePositionBack
import platform.AVFoundation.AVCapturePhoto
import platform.AVFoundation.AVCapturePhotoCaptureDelegateProtocol
import platform.AVFoundation.AVCapturePhotoOutput
import platform.AVFoundation.AVCapturePhotoSettings
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureSessionPresetPhoto
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.fileDataRepresentation
import platform.AVFoundation.position
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSError
import platform.UIKit.UIView
import platform.Vision.VNImageRequestHandler
import platform.Vision.VNRecognizeTextRequest
import platform.Vision.VNRecognizedTextObservation
import platform.Vision.VNRequestTextRecognitionLevelAccurate
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun CameraCaptureScreen(
    onItemsParsed: (List<LineItem>) -> Unit,
    onDone: () -> Unit,
) {
    var permissionGranted by remember { mutableStateOf<Boolean?>(null) }
    LaunchedEffect(Unit) {
        AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
            permissionGranted = granted
        }
    }
    when (permissionGranted) {
        null -> PermissionMessage(
            message = "Requesting camera permission…",
            actionLabel = null,
            onAction = {},
            onCancel = onDone,
        )
        false -> PermissionMessage(
            message = "Camera access is required to scan receipts. Enable it in Settings.",
            actionLabel = null,
            onAction = {},
            onCancel = onDone,
        )
        true -> CameraView(onItemsParsed, onDone)
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
private fun CameraView(
    onItemsParsed: (List<LineItem>) -> Unit,
    onDone: () -> Unit,
) {
    var parsing by remember { mutableStateOf(false) }
    var parseError by remember { mutableStateOf<String?>(null) }
    val controller = remember { CameraController() }

    DisposableEffect(Unit) {
        controller.start()
        onDispose { controller.stop() }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        UIKitView(
            factory = { controller.previewView },
            modifier = Modifier.fillMaxSize(),
            update = { /* no-op */ },
        )
        parseError?.let { err ->
            Text(
                text = err,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(8.dp),
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (parsing) {
                CircularProgressIndicator(color = Color.White)
                Spacer(Modifier.height(12.dp))
                Text("Reading receipt…", color = Color.White)
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedButton(onClick = onDone) { Text("Cancel") }
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center,
                    ) {
                        Button(
                            onClick = {
                                parsing = true
                                parseError = null
                                controller.capture(
                                    onSuccess = { lines ->
                                        val items = ReceiptParser.parse(lines)
                                        if (items.isEmpty()) {
                                            parsing = false
                                            parseError = "No items detected — try again or add manually"
                                        } else {
                                            onItemsParsed(items)
                                        }
                                    },
                                    onError = { msg ->
                                        parsing = false
                                        parseError = msg
                                    },
                                )
                            },
                            modifier = Modifier.size(64.dp).clip(CircleShape),
                        ) {}
                    }
                    Spacer(Modifier.size(72.dp))
                }
            }
        }
    }
}

@Composable
private fun PermissionMessage(
    message: String,
    actionLabel: String?,
    onAction: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = message)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onCancel) { Text("Back") }
            if (actionLabel != null) {
                Button(onClick = onAction) { Text(actionLabel) }
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private class CameraController {
    private val session = AVCaptureSession().apply {
        sessionPreset = AVCaptureSessionPresetPhoto
    }
    private val photoOutput = AVCapturePhotoOutput()
    private var captureDelegate: PhotoCaptureDelegate? = null

    val previewView: UIView = run {
        val view = UIView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0))
        val layer = AVCaptureVideoPreviewLayer(session = session)
        layer.videoGravity = AVLayerVideoGravityResizeAspectFill
        layer.frame = view.bounds
        view.layer.addSublayer(layer)
        view.layer.setNeedsLayout()
        view
    }

    fun start() {
        val device = AVCaptureDevice.devicesWithMediaType(AVMediaTypeVideo)
            .filterIsInstance<AVCaptureDevice>()
            .firstOrNull { it.position == AVCaptureDevicePositionBack }
            ?: return
        val input = AVCaptureDeviceInput.deviceInputWithDevice(device, null) ?: return
        if (session.canAddInput(input)) session.addInput(input)
        if (session.canAddOutput(photoOutput)) session.addOutput(photoOutput)
        session.startRunning()
    }

    fun stop() {
        session.stopRunning()
    }

    fun capture(onSuccess: (List<String>) -> Unit, onError: (String) -> Unit) {
        val settings = AVCapturePhotoSettings.photoSettings()
        val delegate = PhotoCaptureDelegate(
            onPhoto = { photo ->
                val data = photo.fileDataRepresentation()
                if (data == null) {
                    onError("Could not read captured image")
                    return@PhotoCaptureDelegate
                }
                val handler = VNImageRequestHandler(data = data, options = mapOf<Any?, Any?>())
                val request = VNRecognizeTextRequest { request, error ->
                    if (error != null) {
                        onError("OCR failed: ${error.localizedDescription}")
                        return@VNRecognizeTextRequest
                    }
                    val observations = request?.results
                        ?.filterIsInstance<VNRecognizedTextObservation>()
                        .orEmpty()
                    val lines = observations.mapNotNull { obs ->
                        obs.topCandidates(1u).firstOrNull()
                            ?.let { it as? platform.Vision.VNRecognizedText }
                            ?.string
                    }
                    onSuccess(lines)
                }
                request.recognitionLevel = VNRequestTextRecognitionLevelAccurate
                handler.performRequests(listOf(request), null)
            },
            onError = onError,
        )
        captureDelegate = delegate
        photoOutput.capturePhotoWithSettings(settings, delegate)
    }
}

@OptIn(ExperimentalForeignApi::class)
private class PhotoCaptureDelegate(
    private val onPhoto: (AVCapturePhoto) -> Unit,
    private val onError: (String) -> Unit,
) : NSObject(), AVCapturePhotoCaptureDelegateProtocol {

    @ObjCSignatureOverride
    override fun captureOutput(
        output: AVCapturePhotoOutput,
        didFinishProcessingPhoto: AVCapturePhoto,
        error: NSError?,
    ) {
        if (error != null) {
            onError("Capture failed: ${error.localizedDescription}")
        } else {
            onPhoto(didFinishProcessingPhoto)
        }
    }
}
