package com.wadud.facedetection.components

import android.content.Context
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@Composable
fun CameraPreview(
    modifier: Modifier,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    cameraSelector : CameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA,
    onImageReceived: (ImageProxy) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    AndroidView(factory = { context ->
        val previewView = PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            this.scaleType = scaleType
        }

        coroutineScope.launch {
            val cameraProvider = context.cameraProvider()
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)){
                onImageReceived(it)
            }
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner,cameraSelector, imageAnalysis)
        }

        previewView
    })

}


suspend fun Context.cameraProvider(): ProcessCameraProvider = suspendCoroutine {
    val listenableFuture = ProcessCameraProvider.getInstance(this)
    listenableFuture.addListener({
        it.resume(listenableFuture.get())
    }, ContextCompat.getMainExecutor(this))
}