package com.fitformar.api

import com.fitformar.data.Exercise
import retrofit2.http.GET
import retrofit2.http.Query

interface ExerciseImageService {
    @GET("generate-exercise-image")
    suspend fun generateExerciseImage(
        @Query("exercise") exercise: String,
        @Query("style") style: String = "cartoon"
    ): ExerciseImageResponse
}

data class ExerciseImageResponse(
    val imageUrl: String
)

object ExerciseImageApi {
    // For now, return placeholder images
    // In a real app, this would call an actual image generation API
    fun getExerciseImage(exercise: Exercise): String {
        return when (exercise) {
            Exercise.PLANK -> "https://example.com/plank-demo.png"
            Exercise.PUSHUP -> "https://example.com/pushup-demo.png"
        }
    }
}
