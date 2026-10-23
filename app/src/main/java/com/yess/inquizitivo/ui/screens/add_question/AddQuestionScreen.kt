package com.yess.inquizitivo.ui.screens.add_question

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yess.inquizitivo.data.model.Category
import com.yess.inquizitivo.ui.components.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQuestionScreen(
    viewModel: AddQuestionViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSavedSuccess) {
        if (uiState.isSavedSuccess) {
            viewModel.resetSavedState()
            onBack()
        }
    }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(
                title = "Agregar Pregunta Custom",
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Nueva Pregunta Personalizada",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Category Selector
            ExposedDropdownMenuBox(
                expanded = categoryDropdownExpanded,
                onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                val selectedCategory = Category.defaultCategories.find { it.id == uiState.categoryId }
                    ?: Category.defaultCategories.first()

                OutlinedTextField(
                    value = selectedCategory.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = categoryDropdownExpanded,
                    onDismissRequest = { categoryDropdownExpanded = false }
                ) {
                    Category.defaultCategories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                viewModel.updateCategoryId(category.id)
                                categoryDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Question Text
            OutlinedTextField(
                value = uiState.questionText,
                onValueChange = { viewModel.updateQuestionText(it) },
                label = { Text("Texto de la Pregunta") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            // Options
            Text(
                text = "Opciones de Respuesta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            val options = listOf(uiState.option1, uiState.option2, uiState.option3, uiState.option4)
            val updaters = listOf(
                viewModel::updateOption1,
                viewModel::updateOption2,
                viewModel::updateOption3,
                viewModel::updateOption4
            )

            options.forEachIndexed { index, optionVal ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = uiState.correctAnswerIndex == index,
                        onClick = { viewModel.updateCorrectAnswerIndex(index) }
                    )
                    OutlinedTextField(
                        value = optionVal,
                        onValueChange = { updaters[index](it) },
                        label = { Text("Opción ${index + 1}${if (uiState.correctAnswerIndex == index) " (Correcta)" else ""}") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Explanation
            OutlinedTextField(
                value = uiState.explanation,
                onValueChange = { viewModel.updateExplanation(it) },
                label = { Text("Explicación (Opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Error Message
            uiState.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.saveQuestion() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Guardar Pregunta",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
