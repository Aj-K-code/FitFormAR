package com.fitformar.data

enum class Exercise(val category: ExerciseCategory) {
    PLANK(ExerciseCategory.CORE_ABDOMINAL),
    PUSHUP(ExerciseCategory.UPPER_BODY);

    val displayName: String
        get() = name.lowercase().replaceFirstChar { it.uppercase() }

    val description: String
        get() = when (this) {
            PLANK -> "Hold a straight body position parallel to the ground, supported by your forearms and toes"
            PUSHUP -> "Lower your body to the ground and push back up while maintaining proper form"
        }

    val keyPoints: List<String>
        get() = when (this) {
            PLANK -> listOf(
                "Keep your body in a straight line",
                "Engage your core muscles",
                "Keep your neck neutral",
                "Don't let your hips sag"
            )
            PUSHUP -> listOf(
                "Keep your body straight",
                "Lower until your elbows are at 90 degrees",
                "Keep your elbows close to your body",
                "Look slightly ahead of you"
            )
        }

    val commonMistakes: List<String>
        get() = when (this) {
            PLANK -> listOf(
                "Sagging hips",
                "Raised hips",
                "Looking up",
                "Not engaging core"
            )
            PUSHUP -> listOf(
                "Sagging hips",
                "Not going low enough",
                "Flaring elbows",
                "Looking down"
            )
        }
}

enum class ExerciseCategory {
    CORE_ABDOMINAL,
    UPPER_BODY,
    LOWER_BODY,
    FLEXIBILITY_BALANCE
}

object ExerciseDatabase {
    val exercises = listOf(
        Exercise.PLANK,
        Exercise.PUSHUP
    )
}
