package com.wadud.facedetection.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.wadud.facedetection.util.checkPermissionForCamera


const val cameraPermission = Manifest.permission.CAMERA

@Composable
@Preview
fun StartCameraScreen(
    shouldShowPermissionRationale: Boolean = true,
    onGotoAppSettingClicked: () -> Unit = {},
    navigateToDetectionScreen: () -> Unit = {}
) {
    val context = LocalContext.current
    var isCameraPermissionGranted by remember {
        mutableStateOf(context.checkPermissionForCamera(cameraPermission))
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isCameraPermissionGranted = true }
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(8.dp)
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(68.dp)
                .padding(bottom = 18.dp),
            onClick = {
                if (!isCameraPermissionGranted) launcher.launch(cameraPermission)
                else {
                    navigateToDetectionScreen.invoke()
                }
            }) {
            Text(text = "Start Detecting Faces")
        }
    }
}




