package com.wadud.facedetection.viewmodel

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetector
import javax.inject.Inject

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
class FaceAnalyzer @Inject constructor(
    private val faceDetector: FaceDetector,
    private val callBack: FaceAnalyzerCallback) {
    fun analyze(imageProxy: ImageProxy, processResult: (String) -> Unit) {
        val mediaImage = imageProxy.image
        mediaImage?.let {
            val inputImage =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            faceDetector.process(inputImage)
                .addOnSuccessListener { faces ->
                    processResult(callBack.processFace(faces))
                    imageProxy.close()
                }
                .addOnFailureListener {
                    processResult(callBack.errorFace(it.message.orEmpty()))
                    imageProxy.close()
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }

        }

    }
}

interface FaceAnalyzerCallback {
    fun processFace(faces: List<Face>): String
    fun errorFace(error: String): String
}