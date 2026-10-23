package com.yess.inquizitivo.ui.screens.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yess.inquizitivo.data.model.Category
import com.yess.inquizitivo.ui.components.TopBar

@Composable
fun QuizScreen(
    categoryId: String,
    viewModel: QuizViewModel,
    onFinishQuiz: (score: Int, total: Int, correct: Int) -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(categoryId) {
        viewModel.loadQuestionsForCategory(categoryId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isQuizFinished) {
        if (uiState.isQuizFinished) {
            onFinishQuiz(uiState.score, uiState.totalQuestions, uiState.correctAnswersCount)
        }
    }

    val category = remember(categoryId) {
        Category.defaultCategories.find { it.id == categoryId }
            ?: Category.defaultCategories.first()
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Quiz: ${category.name}",
                showBackButton = true,
                onBackClick = onBack,
                containerColor = category.color,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = category.color)
            }
        } else if (uiState.questions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay preguntas disponibles para esta categoría.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            val currentQuestion = uiState.currentQuestion

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Category Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(category.color.copy(alpha = 0.15f))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = category.name,
                                tint = category.color,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pregunta ${uiState.currentQuestionIndex + 1} de ${uiState.totalQuestions}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = category.color
                            )
                        }

                        Text(
                            text = "Puntos: ${uiState.score}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = category.color
                        )
                    }
                }

                // Question Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        if (currentQuestion != null) {
                            Text(
                                text = currentQuestion.questionText,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(24.dp))

                            currentQuestion.options.forEachIndexed { index, optionText ->
                                val isSelected = uiState.selectedOptionIndex == index

                                if (isSelected) {
                                    Button(
                                        onClick = { viewModel.selectOption(index) },
                                        colors = ButtonDefaults.buttonColors(containerColor = category.color),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = optionText,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Color.White
                                        )
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = { viewModel.selectOption(index) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = optionText,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            uiState.selectedOptionIndex?.let { selected ->
                                viewModel.answerCurrentQuestion(selected)
                            }
                        },
                        enabled = uiState.selectedOptionIndex != null,
                        colors = ButtonDefaults.buttonColors(containerColor = category.color),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = if (uiState.currentQuestionIndex + 1 == uiState.totalQuestions) "Finalizar Trivia" else "Siguiente Pregunta",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
