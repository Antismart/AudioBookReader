package com.example.audiobookreader.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.audiobookreader.presentation.library.LibraryScreen
import com.example.audiobookreader.presentation.player.PlayerScreen

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Library.route
    ) {
        composable(Screen.Library.route) {
            LibraryScreen(
                onBookClick = { bookId ->
                    navController.navigate(Screen.Player.createRoute(bookId))
                }
            )
        }
        
        composable(
            route = Screen.Player.route,
            arguments = listOf(
                navArgument("bookId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getLong("bookId") ?: return@composable
            
            PlayerScreen(
                bookId = bookId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
