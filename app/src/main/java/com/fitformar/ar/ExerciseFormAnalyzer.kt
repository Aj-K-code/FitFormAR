package com.fitformar.ar

import com.google.mlkit.vision.pose.Pose
import kotlin.math.abs
import kotlin.math.atan2

class ExerciseFormAnalyzer {
    companion object {
        private const val PLANK_ANGLE_THRESHOLD = 15f // degrees
        private const val PUSHUP_ANGLE_THRESHOLD = 90f // degrees
        private const val CONFIDENCE_THRESHOLD = 0.7f
    }

    private var perfectRepCount = 0
    private var isInStartPosition = false
    private var lastFeedback = ""

    fun analyzePlank(pose: Pose): String {
        val shoulders = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER) to pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val hips = pose.getPoseLandmark(PoseLandmark.LEFT_HIP) to pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val ankles = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE) to pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

        if (shoulders.first == null || shoulders.second == null ||
            hips.first == null || hips.second == null ||
            ankles.first == null || ankles.second == null) {
            return "Please position your full body in the camera view"
        }

        // Check confidence
        if (listOf(shoulders, hips, ankles).any { 
            it.first!!.inFrameLikelihood < CONFIDENCE_THRESHOLD || 
            it.second!!.inFrameLikelihood < CONFIDENCE_THRESHOLD 
        }) {
            return "Cannot detect pose clearly. Please adjust your position"
        }

        // Calculate body angle
        val bodyAngle = calculateAngle(
            shoulders.first!!.position.y,
            hips.first!!.position.y,
            ankles.first!!.position.y
        )

        // Calculate hip sag
        val hipSag = abs(hips.first!!.position.y - hips.second!!.position.y)

        val feedback = StringBuilder()

        when {
            bodyAngle > PLANK_ANGLE_THRESHOLD -> {
                feedback.append("Lower your hips to maintain a straight line")
            }
            hipSag > 20 -> {
                feedback.append("Keep your hips level")
            }
            else -> {
                feedback.append("Perfect plank form!")
                perfectRepCount++
            }
        }

        return feedback.toString()
    }

    fun analyzePushup(pose: Pose): String {
        val shoulders = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER) to pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val elbows = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW) to pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val wrists = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST) to pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        val hips = pose.getPoseLandmark(PoseLandmark.LEFT_HIP) to pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)

        if (shoulders.first == null || shoulders.second == null ||
            elbows.first == null || elbows.second == null ||
            wrists.first == null || wrists.second == null ||
            hips.first == null || hips.second == null) {
            return "Please position your full body in the camera view"
        }

        // Check confidence
        if (listOf(shoulders, elbows, wrists, hips).any { 
            it.first!!.inFrameLikelihood < CONFIDENCE_THRESHOLD || 
            it.second!!.inFrameLikelihood < CONFIDENCE_THRESHOLD 
        }) {
            return "Cannot detect pose clearly. Please adjust your position"
        }

        // Calculate elbow angle
        val elbowAngle = calculateAngle(
            shoulders.first!!.position.y,
            elbows.first!!.position.y,
            wrists.first!!.position.y
        )

        // Calculate body alignment
        val hipAlignment = abs(hips.first!!.position.y - hips.second!!.position.y)

        val feedback = StringBuilder()

        when {
            !isInStartPosition && elbowAngle < 20 -> {
                isInStartPosition = true
                feedback.append("Good starting position. Now lower your body")
            }
            isInStartPosition && elbowAngle > PUSHUP_ANGLE_THRESHOLD -> {
                isInStartPosition = false
                feedback.append("Lower your body more until your elbows are at 90 degrees")
            }
            hipAlignment > 20 -> {
                feedback.append("Keep your hips level")
            }
            isInStartPosition && elbowAngle in 85f..95f -> {
                feedback.append("Perfect pushup form!")
                perfectRepCount++
                isInStartPosition = false
            }
        }

        return feedback.toString()
    }

    fun getPerfectRepCount(): Int = perfectRepCount

    private fun calculateAngle(y1: Float, y2: Float, y3: Float): Float {
        val angle = Math.toDegrees(
            atan2(y3 - y2, y2 - y1).toDouble()
        ).toFloat()
        return abs(angle)
    }

    fun reset() {
        perfectRepCount = 0
        isInStartPosition = false
        lastFeedback = ""
    }
}
