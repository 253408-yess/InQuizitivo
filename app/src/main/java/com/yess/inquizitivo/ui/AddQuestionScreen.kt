package com.yess.inquizitivo.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

// VIEW: Formulario para crear y registrar nuevas preguntas conectado a AddQuestionViewModel
@Composable
fun AddQuestionScreen(
    viewModel: AddQuestionViewModel,
    onQuestionAdded: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetState()
            onQuestionAdded()
        }
    }

    val categories = viewModel.getCategories()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Agregar Nueva Pregunta",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Selecciona la Categoría:",
            fontWeight = FontWeight.SemiBold
        )

        categories.forEach { category ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.updateCategoryId(category.id) }
                    .padding(vertical = 2.dp)
            ) {
                RadioButton(
                    selected = (uiState.selectedCategoryId == category.id),
                    onClick = { viewModel.updateCategoryId(category.id) }
                )
                Text(
                    text = category.name,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.questionText,
            onValueChange = { viewModel.updateQuestionText(it) },
            label = { Text("Texto de la Pregunta") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Opciones de Respuesta:",
            fontWeight = FontWeight.SemiBold
        )

        val optionsList = listOf(uiState.option1, uiState.option2, uiState.option3, uiState.option4)
        val optionUpdaters = listOf(
            viewModel::updateOption1,
            viewModel::updateOption2,
            viewModel::updateOption3,
            viewModel::updateOption4
        )

        optionsList.forEachIndexed { index, optionVal ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButton(
                    selected = (uiState.correctAnswerIndex == index),
                    onClick = { viewModel.updateCorrectAnswerIndex(index) }
                )
                OutlinedTextField(
                    value = optionVal,
                    onValueChange = { optionUpdaters[index](it) },
                    label = { Text("Opción ${index + 1}${if (uiState.correctAnswerIndex == index) " (Correcta)" else ""}") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        uiState.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = Color.Red,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            Button(
                onClick = { viewModel.saveQuestion() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar")
            }
        }
    }
}
