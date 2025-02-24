package com.fitformar.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.fitformar.data.Exercise
import com.fitformar.data.ExerciseDatabase
import com.fitformar.ui.screens.*
import com.fitformar.ui.theme.FitFormARTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitFormARTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FitFormARApp()
                }
            }
        }
    }
}

sealed class Screen {
    object Home : Screen()
    data class Tutorial(val exercise: Exercise) : Screen()
    data class Camera(val exercise: Exercise) : Screen()
}

@Composable
fun FitFormARApp() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var selectedExercise by remember { mutableStateOf<Exercise?>(null) }

    when (val screen = currentScreen) {
        is Screen.Home -> {
            HomeScreen(
                onExerciseSelected = { exercise ->
                    selectedExercise = exercise
                    currentScreen = Screen.Tutorial(exercise)
                }
            )
        }
        is Screen.Tutorial -> {
            TutorialScreen(
                exercise = screen.exercise,
                onStartExercise = {
                    currentScreen = Screen.Camera(screen.exercise)
                },
                onBack = {
                    currentScreen = Screen.Home
                }
            )
        }
        is Screen.Camera -> {
            CameraScreen(
                exercise = screen.exercise,
                onBackPressed = {
                    currentScreen = Screen.Tutorial(screen.exercise)
                }
            )
        }
    }
}
