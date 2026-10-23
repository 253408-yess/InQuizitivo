package com.yess.inquizitivo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// VIEW: Observa el QuizViewModel y renderiza la pantalla del cuestionario
@Composable
fun QuizScreen(
    categoryId: Int,
    viewModel: QuizViewModel,
    onQuizFinished: (score: Int) -> Unit
) {
    LaunchedEffect(categoryId) {
        viewModel.loadCategory(categoryId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isQuizFinished) {
        if (uiState.isQuizFinished) {
            onQuizFinished(uiState.score)
        }
    }

    val category = uiState.category
    val currentQuestion = uiState.currentQuestion

    if (category == null || currentQuestion == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Encabezado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(category.color, RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${category.name} (${uiState.currentIndex + 1}/${uiState.questions.size})",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = currentQuestion.question,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Opciones
        currentQuestion.options.forEachIndexed { index, optionText ->
            val isSelected = uiState.selectedOptionIndex == index
            Button(
                onClick = { viewModel.selectOption(index) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) category.color else Color(0xFFE0E0E0)
                )
            ) {
                Text(
                    text = optionText,
                    color = if (isSelected) Color.White else Color.Black,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.submitAnswerAndNext() },
            enabled = uiState.selectedOptionIndex != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = category.color)
        ) {
            Text(
                text = if (uiState.currentIndex + 1 == uiState.questions.size) "Ver Resultados" else "Siguiente",
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}
