package com.example.audiobookreader.presentation.navigation

sealed class Screen(val route: String) {
    object Library : Screen("library")
    object Player : Screen("player/{bookId}") {
        fun createRoute(bookId: Long) = "player/$bookId"
    }
}
