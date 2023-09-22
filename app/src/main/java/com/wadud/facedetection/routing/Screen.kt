package com.wadud.facedetection.routing

sealed class Screen(val route: String) {

    object StartCamera : Screen("Camera")
    object DetectFaces : Screen("Detect faces")

    fun fromRoute(route: String?): Screen {
        return when (route) {
            StartCamera.route -> StartCamera
            DetectFaces.route -> DetectFaces
            else -> StartCamera
        }
    }
}