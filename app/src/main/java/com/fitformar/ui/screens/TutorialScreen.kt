package com.fitformar.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fitformar.data.Exercise

@Composable
fun TutorialScreen(
    exercise: Exercise,
    onStartExercise: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = exercise.name,
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            item {
                Text(
                    text = exercise.description,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            item {
                Text(
                    text = "Key Points",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(exercise.keyPoints) { point ->
                KeyPoint(point)
            }

            item {
                Text(
                    text = "Common Mistakes",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(exercise.commonMistakes) { mistake ->
                CommonMistake(mistake)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text("Back")
            }

            Button(
                onClick = onStartExercise,
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary
                )
            ) {
                Text("Start Exercise")
            }
        }
    }
}

@Composable
fun KeyPoint(point: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "•",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = point,
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Composable
fun CommonMistake(mistake: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 2.dp,
        backgroundColor = MaterialTheme.colors.error.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "✕",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.error,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = mistake,
                style = MaterialTheme.typography.body1
            )
        }
    }
}
