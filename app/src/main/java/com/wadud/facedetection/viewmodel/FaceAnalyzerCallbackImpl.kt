package com.wadud.facedetection.viewmodel

import android.graphics.PointF
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceLandmark
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class FaceAnalyzerCallbackImpl @Inject constructor() : FaceAnalyzerCallback {
    private var previousFace: Face? = null
    private var currentFace: Face? = null
    override fun processFace(faces: List<Face>): String {
        return if (faces.size > 1) {
            "Multiple Faces Detected, Please Ensure only your face is visible"
        } else {
            if (previousFace == null) previousFace = faces[0]
            if (previousFace != null) currentFace = faces[0]
            if (previousFace != null && currentFace != null && previousFace?.trackingId == currentFace?.trackingId) {
                return detectFaceMovement(previousFace!!, currentFace!!)
            } else {
                previousFace = null
                "Place your face within the camera range"
            }
        }

    }

    override fun errorFace(error: String): String = "$error occurred while trying to locate Faces"


    private fun detectFaceMovement(previousFace: Face, currentFace: Face): String {
        return if (isFaceLive(previousFace, currentFace)) "Live Face Detected" else "Snoop Image Detected"
    }

    private fun isFaceLive(previousFace: Face, currentFace: Face): Boolean {
        val leftEye1 = previousFace.getLandmark(FaceLandmark.LEFT_EYE)
        val rightEye1 = previousFace.getLandmark(FaceLandmark.RIGHT_EYE)
        val mouth1 = previousFace.getLandmark(FaceLandmark.MOUTH_BOTTOM)

        val leftEye2 = currentFace.getLandmark(FaceLandmark.LEFT_EYE)
        val rightEye2 = currentFace.getLandmark(FaceLandmark.RIGHT_EYE)
        val mouth2 = currentFace.getLandmark(FaceLandmark.MOUTH_BOTTOM)

        if (leftEye1 == null || rightEye1 == null || mouth1 == null) {
            return false
        }

        if (leftEye2 == null || rightEye2 == null || mouth2 == null) {
            return false
        }
        val eye1 = calculateEyeAspectRatio(leftEye1, rightEye1)
        val eye2 = calculateEyeAspectRatio(leftEye2, rightEye2)

        val faceContour1 = previousFace.getContour(FaceContour.FACE)

        val eyeContours1 = listOf(
            previousFace.getContour(FaceContour.LEFT_EYE),
            previousFace.getContour(FaceContour.RIGHT_EYE)
        )

        val mouthContour1 = listOf(
            previousFace.getContour(FaceContour.LOWER_LIP_TOP),
            previousFace.getContour(FaceContour.LOWER_LIP_BOTTOM),
            previousFace.getContour(FaceContour.UPPER_LIP_BOTTOM),
            previousFace.getContour(FaceContour.UPPER_LIP_TOP)
        )

        val faceContour2 = currentFace.getContour(FaceContour.FACE)

        val eyeContours2 = listOf(
            currentFace.getContour(FaceContour.LEFT_EYE),
            currentFace.getContour(FaceContour.RIGHT_EYE)
        )
        val mouthContour2 = listOf(
            currentFace.getContour(FaceContour.LOWER_LIP_TOP),
            currentFace.getContour(FaceContour.LOWER_LIP_BOTTOM),
            currentFace.getContour(FaceContour.UPPER_LIP_BOTTOM),
            currentFace.getContour(FaceContour.UPPER_LIP_TOP)
        )

        if (faceContour1 == null || faceContour2 == null) {
            return false
        }

        val contourArea1 = analyzeContourArea(faceContour1, eyeContours1, mouthContour1)
        val contourArea2 = analyzeContourArea(faceContour2, eyeContours2, mouthContour2)

        val counttourMovementScore = calculateCountourAreaDifference(contourArea1, contourArea2)

        val livenessScore1 = calculateLivenessScore(eye1, counttourMovementScore)
        val livenessScore2 = calculateLivenessScore(eye2, counttourMovementScore)

        val scoreDifference = Math.abs(livenessScore1 - livenessScore2)

        val SCORE_DIFFERENCE_THRESHOLD = 0.2

        return scoreDifference < SCORE_DIFFERENCE_THRESHOLD
    }

    private fun calculateEuclideanDistance(point1: PointF, point2: PointF): Double {
        return sqrt(
            (point1.x - point2.x.toDouble()).pow(2.0) +
                    (point1.y - point2.y.toDouble()).pow(2.0)
        )
    }


    private fun calculateEyeAspectRatio(leftEye: FaceLandmark, rightEye: FaceLandmark): Double {
        val leftEyeWidth = calculateEuclideanDistance(leftEye.position, rightEye.position)
        val leftEyeHeight = calculateEuclideanDistance(
            PointF(leftEye.position.x, leftEye.position.y),
            PointF(rightEye.position.x, leftEye.position.y)
        )
        return (2 * leftEyeHeight) / (leftEyeWidth + leftEyeWidth)
    }

    private fun analyzeContourArea(
        faceContour: FaceContour,
        eyeContours: List<FaceContour?>,
        mouthContour: List<FaceContour?>
    ): Double {

        val initialFaceArea = calculateContourArea(faceContour)
        val eyeContourArea = calculateContourArea(eyeContours)
        val mouthContour = calculateContourArea(mouthContour)

        return initialFaceArea + eyeContourArea + mouthContour
    }

    private fun calculateCountourAreaDifference(
        previousContourArea: Double,
        currentContourArea: Double
    ): Double {
        val areaChange = abs(currentContourArea - previousContourArea)

        val AREA_CHANGE_THRESHOLD = 100.0

        return if (areaChange > AREA_CHANGE_THRESHOLD) {
            1.0
        } else {
            0.0
        }
    }


    private fun calculateLivenessScore(
        eye: Double,
        contourMovementScore: Double
    ): Double {
        val confidenceWeight = 0.4
        val contourWeight = 0.6
        return (eye) * confidenceWeight + contourMovementScore * contourWeight
    }

    private fun calculateContourArea(contours: List<FaceContour?>): Double {
        val totalContourArea = contours.sumOf {
            calculateContourArea(it)
        }

        return totalContourArea / contours.size
    }

    private fun calculateContourArea(contour: FaceContour?): Double {
        val points = contour!!.points
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

        area = Math.abs(area) / 2.0
        return area
    }


}