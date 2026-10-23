package com.yess.inquizitivo

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yess.inquizitivo.ui.AddQuestionScreen
import com.yess.inquizitivo.ui.AddQuestionViewModel
import com.yess.inquizitivo.ui.HomeScreen
import com.yess.inquizitivo.ui.QuizScreen
import com.yess.inquizitivo.ui.QuizViewModel
import com.yess.inquizitivo.ui.ResultScreen

// NAVEGACIÓN: Conecta las Vistas (Screens) con sus respectivos ViewModels usando Navigation Compose
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onCategorySelected = { categoryId ->
                    navController.navigate("quiz/$categoryId")
                },
                onAddQuestionClick = {
                    navController.navigate("add_question")
                }
            )
        }

        composable(
            route = "quiz/{categoryId}",
            arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: 1
            val quizViewModel: QuizViewModel = viewModel()

            QuizScreen(
                categoryId = categoryId,
                viewModel = quizViewModel,
                onQuizFinished = { score ->
                    navController.navigate("result/$score") {
                        popUpTo("home")
                    }
                }
            )
        }

        composable(
            route = "result/{score}",
            arguments = listOf(navArgument("score") { type = NavType.IntType })
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0

            ResultScreen(
                score = score,
                onRestart = {
                    navController.popBackStack("home", false)
                }
            )
        }

        composable("add_question") {
            val addQuestionViewModel: AddQuestionViewModel = viewModel()

            AddQuestionScreen(
                viewModel = addQuestionViewModel,
                onQuestionAdded = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
