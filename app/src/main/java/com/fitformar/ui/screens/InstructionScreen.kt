package com.fitformar.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitformar.R
import com.fitformar.data.Exercise
import com.fitformar.utils.SpeechManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun InstructionScreen(
    exercise: Exercise,
    onInstructionComplete: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val speechManager = remember { SpeechManager(context) }
    
    LaunchedEffect(Unit) {
        // Initial instruction
        speechManager.speak("Please turn up your volume. Get ready for ${exercise.displayName}")
        delay(2000)
        
        // Exercise specific instruction
        val instruction = when (exercise) {
            Exercise.PLANK -> "Position yourself in a plank position with your arms straight and body aligned"
            Exercise.PUSHUP -> "Get into a pushup position with your hands shoulder-width apart"
        }
        speechManager.speak(instruction)
        
        // Wait for speech and animation
        delay(3000)
        onInstructionComplete()
    }

    DisposableEffect(Unit) {
        onDispose {
            speechManager.shutdown()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Exercise demonstration image
        val imageRes = when (exercise) {
            Exercise.PLANK -> R.drawable.plank_demo
            Exercise.PUSHUP -> R.drawable.pushup_demo
        }
        
        Image(
            painter = painterResource(imageRes),
            contentDescription = "Exercise demonstration",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentScale = ContentScale.Fit
        )
        
        Text(
            text = "Get Ready for ${exercise.displayName}",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}
