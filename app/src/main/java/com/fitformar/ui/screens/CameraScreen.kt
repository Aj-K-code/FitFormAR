package com.fitformar.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.fitformar.ar.ExerciseFormAnalyzer
import com.fitformar.ar.PoseProcessor
import com.fitformar.data.Exercise
import com.fitformar.utils.SpeechManager
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import java.util.concurrent.Executors

private const val TAG = "CameraScreen"
private const val PERFECT_REPS_GOAL = 20

@Composable
fun CameraScreen(
    exercise: Exercise,
    onBackPressed: () -> Unit
) {
    var showInstructions by remember { mutableStateOf(true) }
    var isFrontCamera by remember { mutableStateOf(true) }
    var feedback by remember { mutableStateOf("") }
    var perfectReps by remember { mutableStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val context = LocalContext.current
    val speechManager = remember { SpeechManager(context) }
    val formAnalyzer = remember { ExerciseFormAnalyzer() }
    
    if (showInstructions) {
        InstructionScreen(
            exercise = exercise,
            onInstructionComplete = { showInstructions = false }
        )
    } else {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.weight(1f)) {
                CameraPreview(
                    isFrontCamera = isFrontCamera,
                    onSwitchCamera = { isFrontCamera = !isFrontCamera },
                    exercise = exercise,
                    onFeedbackUpdate = { newFeedback -> 
                        if (newFeedback != feedback) {
                            feedback = newFeedback
                            speechManager.speak(newFeedback)
                            
                            // Update perfect reps count
                            if (newFeedback.contains("Perfect")) {
                                perfectReps++
                                if (perfectReps == 1) {
                                    speechManager.speak("Great! Now do $PERFECT_REPS_GOAL more perfect reps!")
                                } else if (perfectReps == PERFECT_REPS_GOAL) {
                                    speechManager.speak("Congratulations! You've completed $PERFECT_REPS_GOAL perfect reps!")
                                } else {
                                    speechManager.speak("$perfectReps perfect reps completed!")
                                }
                            }
                        }
                    },
                    onError = { error -> 
                        errorMessage = error
                        speechManager.speak("Error: $error")
                    },
                    formAnalyzer = formAnalyzer,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Overlay with feedback and rep counter
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Rep counter
                    Text(
                        text = "Perfect Reps: $perfectReps / $PERFECT_REPS_GOAL",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(8.dp)
                    )
                    
                    // Feedback text
                    Text(
                        text = feedback,
                        color = Color.White,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(8.dp)
                    )
                }
                
                // Camera switch button
                IconButton(
                    onClick = onSwitchCamera,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(48.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Switch Camera",
                        tint = Color.White
                    )
                }
            }
        }
    }
    
    // Error dialog
    errorMessage?.let { error ->
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = {
                Button(onClick = { errorMessage = null }) {
                    Text("OK")
                }
            }
        )
    }
    
    DisposableEffect(Unit) {
        onDispose {
            speechManager.shutdown()
        }
    }
}

@Composable
private fun CameraPreview(
    isFrontCamera: Boolean,
    onSwitchCamera: () -> Unit,
    exercise: Exercise,
    onFeedbackUpdate: (String) -> Unit,
    onError: (String) -> Unit,
    formAnalyzer: ExerciseFormAnalyzer,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    DisposableEffect(Unit) {
        onDispose {
            try {
                cameraExecutor.shutdown()
            } catch (e: Exception) {
                Log.e(TAG, "Error shutting down camera executor", e)
            }
        }
    }
    
    Box(modifier = modifier) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { previewView ->
                cameraProviderFuture.addListener({
                    try {
                        val cameraProvider = cameraProviderFuture.get()
                        
                        val preview = Preview.Builder()
                            .build()
                            .also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                        
                        val imageAnalyzer = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .apply {
                                setAnalyzer(cameraExecutor) { imageProxy ->
                                    try {
                                        val mediaImage = imageProxy.image
                                        if (mediaImage != null) {
                                            val inputImage = InputImage.fromMediaImage(
                                                mediaImage,
                                                imageProxy.imageInfo.rotationDegrees
                                            )
                                            
                                            val options = PoseDetectorOptions.Builder()
                                                .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
                                                .build()
                                            val poseDetector = PoseDetection.getClient(options)
                                            
                                            poseDetector.process(inputImage)
                                                .addOnSuccessListener { pose ->
                                                    // Analyze form based on exercise type
                                                    val feedback = when (exercise) {
                                                        Exercise.PLANK -> formAnalyzer.analyzePlank(pose)
                                                        Exercise.PUSHUP -> formAnalyzer.analyzePushup(pose)
                                                    }
                                                    onFeedbackUpdate(feedback)
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.e(TAG, "Pose detection failed", e)
                                                    onError("Failed to detect pose: ${e.localizedMessage}")
                                                }
                                        }
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Error analyzing image", e)
                                        onError("Failed to analyze pose: ${e.localizedMessage}")
                                    } finally {
                                        imageProxy.close()
                                    }
                                }
                            }
                        
                        val cameraSelector = if (isFrontCamera) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        }
                        
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalyzer
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Use case binding failed", e)
                            onError("Failed to start camera: ${e.localizedMessage}")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Camera provider setup failed", e)
                        onError("Camera initialization failed: ${e.localizedMessage}")
                    }
                }, ContextCompat.getMainExecutor(context))
            }
        )
    }
}

@Composable
fun AROverlay(
    exercise: Exercise,
    feedback: String,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = feedback,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ControlPanel(
    exercise: Exercise,
    onBackPressed: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onBackPressed) {
                Text("Back")
            }
            Button(onClick = { /* Toggle recording */ }) {
                Text("Start Recording")
            }
        }
    }
}
