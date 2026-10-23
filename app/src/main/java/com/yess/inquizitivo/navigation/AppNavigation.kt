package com.yess.inquizitivo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yess.inquizitivo.data.local.UserPreferencesRepository
import com.yess.inquizitivo.data.repository.QuestionRepository
import com.yess.inquizitivo.ui.screens.add_question.AddQuestionScreen
import com.yess.inquizitivo.ui.screens.add_question.AddQuestionViewModel
import com.yess.inquizitivo.ui.screens.add_question.AddQuestionViewModelFactory
import com.yess.inquizitivo.ui.screens.home.HomeScreen
import com.yess.inquizitivo.ui.screens.quiz.QuizScreen
import com.yess.inquizitivo.ui.screens.quiz.QuizViewModel
import com.yess.inquizitivo.ui.screens.quiz.QuizViewModelFactory
import com.yess.inquizitivo.ui.screens.results.ResultScreen
import com.yess.inquizitivo.ui.screens.stats.StatsScreen
import com.yess.inquizitivo.ui.screens.stats.StatsViewModel
import com.yess.inquizitivo.ui.screens.stats.StatsViewModelFactory

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Quiz : Screen("quiz/{categoryId}") {
        fun createRoute(categoryId: String) = "quiz/$categoryId"
    }
    object Result : Screen("result/{score}/{total}/{correct}") {
        fun createRoute(score: Int, total: Int, correct: Int) = "result/$score/$total/$correct"
    }
    object Stats : Screen("stats")
    object AddQuestion : Screen("add_question")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val questionRepository = remember { QuestionRepository() }
    val userPreferencesRepository = remember { UserPreferencesRepository(context) }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToQuiz = { categoryId ->
                    navController.navigate(Screen.Quiz.createRoute(categoryId))
                },
                onNavigateToStats = {
                    navController.navigate(Screen.Stats.route)
                },
                onNavigateToAddQuestion = {
                    navController.navigate(Screen.AddQuestion.route)
                }
            )
        }

        composable(
            route = Screen.Quiz.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            val quizViewModel: QuizViewModel = viewModel(
                factory = QuizViewModelFactory(questionRepository, userPreferencesRepository)
            )

            QuizScreen(
                categoryId = categoryId,
                viewModel = quizViewModel,
                onFinishQuiz = { score, total, correct ->
                    navController.navigate(Screen.Result.createRoute(score, total, correct)) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("score") { type = NavType.IntType },
                navArgument("total") { type = NavType.IntType },
                navArgument("correct") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            val total = backStackEntry.arguments?.getInt("total") ?: 0
            val correct = backStackEntry.arguments?.getInt("correct") ?: 0

            ResultScreen(
                score = score,
                totalQuestions = total,
                correctAnswers = correct,
                onPlayAgain = {
                    navController.popBackStack(Screen.Home.route, false)
                },
                onHome = {
                    navController.popBackStack(Screen.Home.route, false)
                }
            )
        }

        composable(Screen.Stats.route) {
            val statsViewModel: StatsViewModel = viewModel(
                factory = StatsViewModelFactory(userPreferencesRepository)
            )

            StatsScreen(
                viewModel = statsViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddQuestion.route) {
            val addQuestionViewModel: AddQuestionViewModel = viewModel(
                factory = AddQuestionViewModelFactory(userPreferencesRepository)
            )

            AddQuestionScreen(
                viewModel = addQuestionViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
