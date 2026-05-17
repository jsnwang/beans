package com.moo.beans.ui

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.moo.beans.data.ReceiptParser
import com.moo.beans.viewmodel.SplitterViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraCaptureScreen(
    splitterViewModel: SplitterViewModel,
    onDone: () -> Unit,
) {
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }
    when {
        cameraPermission.status.isGranted -> CameraView(splitterViewModel, onDone)
        cameraPermission.status.shouldShowRationale -> PermissionMessage(
            message = "Camera access lets the app scan a receipt and pre-fill items.",
            actionLabel = "Grant",
            onAction = { cameraPermission.launchPermissionRequest() },
            onCancel = onDone,
        )
        else -> PermissionMessage(
            message = "Waiting for camera permission…",
            actionLabel = "Request",
            onAction = { cameraPermission.launchPermissionRequest() },
            onCancel = onDone,
        )
    }
}

@Composable
private fun CameraView(viewModel: SplitterViewModel, onDone: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val executor = remember { ContextCompat.getMainExecutor(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val previewView = remember {
        PreviewView(context).apply { scaleType = PreviewView.ScaleType.FILL_CENTER }
    }
    val recognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }

    DisposableEffect(lifecycleOwner) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        providerFuture.addListener({
            val cameraProvider = providerFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture,
            )
        }, executor)
        onDispose {
            providerFuture.get().unbindAll()
            recognizer.close()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        state.parseError?.let { err ->
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
            if (state.parsing) {
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
                                capture(viewModel, imageCapture, executor, recognizer, onDone)
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

private fun capture(
    viewModel: SplitterViewModel,
    imageCapture: ImageCapture,
    executor: java.util.concurrent.Executor,
    recognizer: com.google.mlkit.vision.text.TextRecognizer,
    onDone: () -> Unit,
) {
    viewModel.setParsing(true)
    imageCapture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
            val mediaImage = image.image
            if (mediaImage == null) {
                viewModel.setParseError("Could not read captured image")
                image.close()
                return
            }
            val input = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
            recognizer.process(input)
                .addOnSuccessListener { text ->
                    val lines = text.textBlocks.flatMap { block -> block.lines.map { it.text } }
                    val items = ReceiptParser.parse(lines)
                    if (items.isEmpty()) {
                        viewModel.setParseError("No items detected — try again or add manually")
                    } else {
                        viewModel.addItems(items)
                        viewModel.setParsing(false)
                        onDone()
                    }
                }
                .addOnFailureListener { e ->
                    viewModel.setParseError("OCR failed: ${e.localizedMessage ?: "unknown"}")
                }
                .addOnCompleteListener { image.close() }
        }

        override fun onError(exc: ImageCaptureException) {
            viewModel.setParseError("Capture failed: ${exc.localizedMessage ?: "unknown"}")
        }
    })
}

@Composable
private fun PermissionMessage(
    message: String,
    actionLabel: String,
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
            Button(onClick = onAction) { Text(actionLabel) }
        }
    }
}
