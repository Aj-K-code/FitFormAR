package com.fitformar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fitformar.data.Exercise
import com.fitformar.data.ExerciseCategory
import com.fitformar.data.ExerciseDatabase

@Composable
fun HomeScreen(
    onExerciseSelected: (Exercise) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<ExerciseCategory?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "FitFormAR",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        LazyColumn {
            item {
                CategoryButton(
                    name = "Core & Abdominal Exercises",
                    isSelected = selectedCategory == ExerciseCategory.CORE_ABDOMINAL,
                    onClick = {
                        selectedCategory = ExerciseCategory.CORE_ABDOMINAL
                    }
                )
            }
            
            if (selectedCategory == ExerciseCategory.CORE_ABDOMINAL) {
                items(ExerciseDatabase.exercises.filter { it.category == ExerciseCategory.CORE_ABDOMINAL }) { exercise ->
                    ExerciseItem(exercise = exercise, onClick = onExerciseSelected)
                }
            }
            
            item {
                CategoryButton(
                    name = "Upper Body Strength",
                    isSelected = selectedCategory == ExerciseCategory.UPPER_BODY,
                    onClick = {
                        selectedCategory = ExerciseCategory.UPPER_BODY
                    }
                )
            }
            
            if (selectedCategory == ExerciseCategory.UPPER_BODY) {
                items(ExerciseDatabase.exercises.filter { it.category == ExerciseCategory.UPPER_BODY }) { exercise ->
                    ExerciseItem(exercise = exercise, onClick = onExerciseSelected)
                }
            }
            
            item {
                CategoryButton(
                    name = "Lower Body Strength",
                    isSelected = selectedCategory == ExerciseCategory.LOWER_BODY,
                    onClick = {
                        selectedCategory = ExerciseCategory.LOWER_BODY
                    }
                )
            }
            
            if (selectedCategory == ExerciseCategory.LOWER_BODY) {
                items(ExerciseDatabase.exercises.filter { it.category == ExerciseCategory.LOWER_BODY }) { exercise ->
                    ExerciseItem(exercise = exercise, onClick = onExerciseSelected)
                }
            }
            
            item {
                CategoryButton(
                    name = "Flexibility & Balance",
                    isSelected = selectedCategory == ExerciseCategory.FLEXIBILITY_BALANCE,
                    onClick = {
                        selectedCategory = ExerciseCategory.FLEXIBILITY_BALANCE
                    }
                )
            }
            
            if (selectedCategory == ExerciseCategory.FLEXIBILITY_BALANCE) {
                items(ExerciseDatabase.exercises.filter { it.category == ExerciseCategory.FLEXIBILITY_BALANCE }) { exercise ->
                    ExerciseItem(exercise = exercise, onClick = onExerciseSelected)
                }
            }
        }
    }
}

@Composable
fun CategoryButton(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.surface
        )
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(vertical = 8.dp),
            color = if (isSelected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
        )
    }
}

@Composable
fun ExerciseItem(
    exercise: Exercise,
    onClick: (Exercise) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp),
        elevation = 2.dp
    ) {
        Button(
            onClick = { onClick(exercise) },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.surface
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = exercise.name,
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
