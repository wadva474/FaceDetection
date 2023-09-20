package com.wadud.facedetection.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wadud.facedetection.components.CameraPreview


@Composable
@Preview
fun  FaceDetectionScreen(modifier: Modifier = Modifier){
    CameraPreview(modifier = modifier)
}