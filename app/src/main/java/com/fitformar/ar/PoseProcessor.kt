package com.fitformar.ar

import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.abs
import kotlin.math.atan2

class PoseProcessor(
    private val isFrontCamera: Boolean,
    private val imageWidth: Int
) {
    fun analyzePose(pose: Pose, exerciseType: String): FormAnalysis {
        // Adjust x coordinates if using front camera (mirror effect)
        if (isFrontCamera) {
            pose.allPoseLandmarks.forEach { landmark ->
                // Mirror the x coordinate by subtracting from actual image width
                landmark.position.x = imageWidth - landmark.position.x
            }
        }

        return when (exerciseType.lowercase()) {
            "plank" -> analyzePlank(pose)
            "push-up" -> analyzePushUp(pose)
            "squat" -> analyzeSquat(pose)
            "hollow-body" -> analyzeHollowBody(pose)
            "russian-twist" -> analyzeRussianTwist(pose)
            "wall-sit" -> analyzeWallSit(pose)
            else -> FormAnalysis(false, "Exercise type not supported")
        }
    }

    private fun analyzePlank(pose: Pose): FormAnalysis {
        val shoulders = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER) to pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val hips = pose.getPoseLandmark(PoseLandmark.LEFT_HIP) to pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val ankles = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE) to pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

        if (shoulders.first == null || shoulders.second == null ||
            hips.first == null || hips.second == null ||
            ankles.first == null || ankles.second == null) {
            return FormAnalysis(false, "Cannot detect all required body points")
        }

        // Check if body is in straight line
        val hipAlignment = checkHipAlignment(hips.first!!, hips.second!!)
        val bodyLineAngle = calculateBodyLineAngle(shoulders.first!!, hips.first!!, ankles.first!!)

        return when {
            !hipAlignment -> FormAnalysis(false, "Keep your hips level")
            bodyLineAngle > 15 -> FormAnalysis(false, "Lower your hips to maintain a straight line")
            bodyLineAngle < -15 -> FormAnalysis(false, "Raise your hips to maintain a straight line")
            else -> FormAnalysis(true, "Good form! Keep your core tight")
        }
    }

    private fun analyzePushUp(pose: Pose): FormAnalysis {
        val shoulders = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER) to pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val elbows = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW) to pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val wrists = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST) to pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        val hips = pose.getPoseLandmark(PoseLandmark.LEFT_HIP) to pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)

        if (shoulders.first == null || elbows.first == null || wrists.first == null || hips.first == null) {
            return FormAnalysis(false, "Cannot detect all required body points")
        }

        val elbowAngle = calculateAngle(shoulders.first!!, elbows.first!!, wrists.first!!)
        val bodyAlignment = checkBodyAlignment(shoulders, hips, null)

        return when {
            !bodyAlignment -> FormAnalysis(false, "Keep your body straight, don't let your hips sag")
            elbowAngle < 45 -> FormAnalysis(false, "You're going too low, keep elbows at least 90 degrees")
            elbowAngle > 160 -> FormAnalysis(true, "Good! Now lower down with control")
            else -> FormAnalysis(true, "Good form! Keep going")
        }
    }

    private fun analyzeSquat(pose: Pose): FormAnalysis {
        val hips = pose.getPoseLandmark(PoseLandmark.LEFT_HIP) to pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val knees = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE) to pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
        val ankles = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE) to pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

        if (hips.first == null || knees.first == null || ankles.first == null) {
            return FormAnalysis(false, "Cannot detect all required body points")
        }

        val kneeAngle = calculateAngle(hips.first!!, knees.first!!, ankles.first!!)
        val kneeAlignment = checkKneeAlignment(knees.first!!, ankles.first!!)

        return when {
            !kneeAlignment -> FormAnalysis(false, "Keep your knees in line with your toes")
            kneeAngle < 90 -> FormAnalysis(false, "You're going too low, maintain at least 90 degrees at knees")
            kneeAngle > 170 -> FormAnalysis(true, "Good! Now lower down with control")
            else -> FormAnalysis(true, "Good depth! Keep your chest up")
        }
    }

    private fun analyzeHollowBody(pose: Pose): FormAnalysis {
        // Implementation for hollow body hold analysis
        return FormAnalysis(true, "Hollow body analysis to be implemented")
    }

    private fun analyzeRussianTwist(pose: Pose): FormAnalysis {
        // Implementation for Russian twist analysis
        return FormAnalysis(true, "Russian twist analysis to be implemented")
    }

    private fun analyzeWallSit(pose: Pose): FormAnalysis {
        // Implementation for wall sit analysis
        return FormAnalysis(true, "Wall sit analysis to be implemented")
    }

    private fun calculateAngle(first: PoseLandmark, middle: PoseLandmark, last: PoseLandmark): Double {
        val angle = Math.toDegrees(
            atan2((last.position.y - middle.position.y).toDouble(),
                  (last.position.x - middle.position.x).toDouble()) -
            atan2((first.position.y - middle.position.y).toDouble(),
                  (first.position.x - middle.position.x).toDouble())
        ).let { Math.abs(it) }
        return if (angle > 180) 360 - angle else angle
    }

    private fun checkHipAlignment(leftHip: PoseLandmark, rightHip: PoseLandmark): Boolean {
        return abs(leftHip.position.y - rightHip.position.y) < 30
    }

    private fun checkKneeAlignment(knee: PoseLandmark, ankle: PoseLandmark): Boolean {
        return abs(knee.position.x - ankle.position.x) < 30
    }

    private fun calculateBodyLineAngle(shoulder: PoseLandmark, hip: PoseLandmark, ankle: PoseLandmark): Double {
        return calculateAngle(shoulder, hip, ankle)
    }

    private fun checkBodyAlignment(
        shoulders: Pair<PoseLandmark?, PoseLandmark?>,
        hips: Pair<PoseLandmark?, PoseLandmark?>,
        ankles: Pair<PoseLandmark?, PoseLandmark?>?
    ): Boolean {
        if (shoulders.first == null || shoulders.second == null ||
            hips.first == null || hips.second == null) {
            return false
        }

        val shoulderAlignment = abs(shoulders.first!!.position.y - shoulders.second!!.position.y) < 30
        val hipAlignment = abs(hips.first!!.position.y - hips.second!!.position.y) < 30

        if (ankles?.first == null || ankles.second == null) {
            return shoulderAlignment && hipAlignment
        }

        val ankleAlignment = abs(ankles.first!!.position.y - ankles.second!!.position.y) < 30
        return shoulderAlignment && hipAlignment && ankleAlignment
    }
}

data class FormAnalysis(
    val isCorrect: Boolean,
    val feedback: String
)
