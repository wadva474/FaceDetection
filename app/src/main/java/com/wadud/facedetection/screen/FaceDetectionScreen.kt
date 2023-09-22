package com.wadud.facedetection.screen


import androidx.camera.core.ImageProxy
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.wadud.facedetection.components.CameraPreview


@Composable
@Preview
fun FaceDetectionScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {},
    onImageReceived: (ImageProxy) -> Unit = {},
    imageDetectedResult: String = ""
) {
    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (cameraPreview, stopScanningButton, detectionText) = createRefs()

        CameraPreview(
            modifier = modifier
                .constrainAs(cameraPreview) {
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            onImageReceived(it)
        }

        Text(
            modifier = modifier
                .constrainAs(detectionText) {
                    top.linkTo(parent.top)
                    centerHorizontallyTo(parent)
                }
                .padding(top = 16.dp),
            text = imageDetectedResult, color = Color.White,
            fontSize = 30.sp
        )

        Button(
            onClick = { navigateBack.invoke() },
            modifier = modifier
                .constrainAs(stopScanningButton) {
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "Stop Scanning")
        }

    }
}