package com.wadud.facedetection

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wadud.facedetection.components.CameraPreview
import com.wadud.facedetection.routing.Screen
import com.wadud.facedetection.screen.FaceDetectionScreen
import com.wadud.facedetection.screen.StartCameraScreen
import com.wadud.facedetection.screen.cameraPermission
import com.wadud.facedetection.ui.theme.FaceDetectionTheme
import com.wadud.facedetection.util.openSettings
import com.wadud.facedetection.viewmodel.FaceAnalyzerViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FaceDetectionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppContent()
                }
            }
        }
    }

    @Composable
    private fun AppContent() {
        val navController = rememberNavController()
        val viewModel: FaceAnalyzerViewModel = viewModel()

        NavHost(
            navController = navController,
            startDestination = Screen.StartCamera.route
        ) {
            composable(Screen.StartCamera.route) {
                StartCameraScreen(
                    shouldShowPermissionRationale = !shouldShowRequestPermissionRationale(
                        cameraPermission
                    ),
                    { openSettings() },
                    { navController.navigate(Screen.DetectFaces.route) })
            }
            composable(Screen.DetectFaces.route) {
                FaceDetectionScreen(
                    navigateBack = { navController.popBackStack() },
                    onImageReceived = { imageProxy ->
                        viewModel.processImage(imageProxy)
                    },
                    imageDetectedResult = viewModel.faceDetected.collectAsState().value
                )
            }
        }
    }

}


