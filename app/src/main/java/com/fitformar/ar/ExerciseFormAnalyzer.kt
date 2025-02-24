package com.fitformar.ar

import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
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
            shoulders.first!!.position.y.toDouble(),
            hips.first!!.position.y.toDouble(),
            ankles.first!!.position.y.toDouble()
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

        // Calculate arm angles
        val leftArmAngle = calculateAngle(
            shoulders.first!!.position.y.toDouble(),
            elbows.first!!.position.y.toDouble(),
            wrists.first!!.position.y.toDouble()
        )

        val rightArmAngle = calculateAngle(
            shoulders.second!!.position.y.toDouble(),
            elbows.second!!.position.y.toDouble(),
            wrists.second!!.position.y.toDouble()
        )

        // Calculate hip alignment
        val hipAlignment = abs(hips.first!!.position.y - hips.second!!.position.y)

        val feedback = StringBuilder()

        when {
            leftArmAngle < PUSHUP_ANGLE_THRESHOLD || rightArmAngle < PUSHUP_ANGLE_THRESHOLD -> {
                feedback.append("Lower your body more")
            }
            hipAlignment > 20 -> {
                feedback.append("Keep your hips level")
            }
            else -> {
                feedback.append("Perfect push-up form!")
            }
        }

        return feedback.toString()
    }

    private fun calculateAngle(y1: Double, y2: Double, y3: Double): Float {
        return abs(Math.toDegrees(
            atan2(y3 - y2, 0.0) -
            atan2(y1 - y2, 0.0)
        )).toFloat()
    }
}
