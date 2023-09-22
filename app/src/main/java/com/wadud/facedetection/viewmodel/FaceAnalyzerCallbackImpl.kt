package com.wadud.facedetection.viewmodel

import android.graphics.PointF
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.pow

class FaceAnalyzerCallbackImpl @Inject constructor() : FaceAnalyzerCallback {
    private var previousFace: Face? = null
    private var currentFace: Face? = null
    override fun processFace(faces: List<Face>): String {
        if (faces.size == 1) {
            if (previousFace == null) previousFace = faces[0]
            if (previousFace != null) currentFace = faces[0]
            if (previousFace != null && currentFace != null && previousFace?.trackingId == currentFace?.trackingId) {
                return detectFaceMovement(previousFace!!, currentFace!!)
            }
        }
        else if (faces.size > 1) {
            return "Multiple Faces Detected, Please Ensure only your face is visible"
        }

        else {
            previousFace = null
        }
        return  "Place your face within the camera range"
    }

    override fun errorFace(error: String): String = "$error occurred while trying to locate Faces"


    private fun detectFaceMovement(previousFace: Face, currentFace: Face): String {
        return if (isFaceLive(previousFace,currentFace)) "Live Face Detected" else "Snoop Image Detected"
    }



    private fun isFaceLive(previousFace: Face, currentFace: Face): Boolean {
        val previousLeftEyeAspect = calculateEyeAspectRatio(previousFace.getContour(FaceContour.LEFT_EYE))
        val previousRightEyeAspect = calculateEyeAspectRatio(previousFace.getContour(FaceContour.RIGHT_EYE))

        val currentLeftEyeAspect = calculateEyeAspectRatio(currentFace.getContour(FaceContour.LEFT_EYE))
        val currentRightEyeAspect = calculateEyeAspectRatio(currentFace.getContour(FaceContour.RIGHT_EYE))

        println("wadud $previousLeftEyeAspect && $currentLeftEyeAspect")
        println("wadud $currentRightEyeAspect && $previousRightEyeAspect")

        if ((previousLeftEyeAspect < EYE_THRESHOLD || previousRightEyeAspect < EYE_THRESHOLD) &&
            (currentLeftEyeAspect < EYE_THRESHOLD || currentRightEyeAspect < EYE_THRESHOLD)
        ) {
            return false  // Both frames have low eye aspect ratios, indicating potential spoofing
        }

        // Analyze facial movement in both frames
        val previousFaceContour = previousFace.getContour(FaceContour.FACE)
        val currentFaceContour = currentFace.getContour(FaceContour.FACE)

        if (previousFaceContour != null && currentFaceContour!= null) {
            val previousFaceArea = calculateContourArea(previousFaceContour)
            val currentFaceArea = calculateContourArea(currentFaceContour)

            val areaChange1 = abs(currentFaceArea - previousFaceArea)
            println("wadud $areaChange1")

            return areaChange1 > MOVEMENT_THRESHOLD
        }
        return false
    }

    private fun calculateEyeAspectRatio(eyeContour: FaceContour?): Double {
        if (eyeContour == null) return 0.0

        val eyePoints = eyeContour.points
        val earNumerator = calculateEuclideanDistance(eyePoints[1], eyePoints[5]) +
                calculateEuclideanDistance(eyePoints[2], eyePoints[4])
        val earDenominator = 2 * calculateEuclideanDistance(eyePoints[0], eyePoints[3])

        return earNumerator / earDenominator
    }

    private fun calculateContourArea(contour: FaceContour): Double {
        val points = contour.points
        var area = 0.0

        for (i in 0 until points.size - 1) {
            val x1 = points[i].x
            val y1 = points[i].y
            val x2 = points[i + 1].x
            val y2 = points[i + 1].y
            area += (x1 * y2 - x2 * y1)
        }

        val x1 = points.last().x
        val y1 = points.last().y
        val x2 = points.first().x
        val y2 = points.first().y
        area += (x1 * y2 - x2 * y1)

        area = abs(area) / 2.0
        return area
    }

    private fun calculateEuclideanDistance(point1: PointF, point2: PointF): Double {
        return kotlin.math.sqrt(
            (point1.x - point2.x).toDouble().pow(2) + (point1.y - point2.y).toDouble().pow(2)
        )
    }

    companion object{
        // Constants
        const val EYE_THRESHOLD = 0.2
        const val MOVEMENT_THRESHOLD = 4000.0
    }
}
