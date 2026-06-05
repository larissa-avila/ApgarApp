package com.example.apgarapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.apgarapp.ui.screens.CalculatorScreen
import com.example.apgarapp.ui.screens.HistoryScreen
import com.example.apgarapp.ui.screens.HomeScreen
import com.example.apgarapp.ui.screens.ResultScreen
import com.example.apgarapp.viewmodel.ApgarViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Calculator : Screen("calculator")
    object Result : Screen("result/{score}") {
        fun createRoute(score: Int) = "result/$score"
    }
    object History : Screen("history")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: ApgarViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToCalculator = {
                    viewModel.resetScores()
                    navController.navigate(Screen.Calculator.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                }
            )
        }

        composable(Screen.Calculator.route) {
            CalculatorScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToResult = { score ->
                    navController.navigate(Screen.Result.createRoute(score))
                }
            )
        }

        composable(
            route = Screen.Result.route,
            arguments = listOf(navArgument("score") { type = NavType.IntType })
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            ResultScreen(
                score = score,
                viewModel = viewModel,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToCalculator = {
                    viewModel.resetScores()
                    navController.navigate(Screen.Calculator.route) {
                        popUpTo(Screen.Calculator.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.History.route) {
            HistoryScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
