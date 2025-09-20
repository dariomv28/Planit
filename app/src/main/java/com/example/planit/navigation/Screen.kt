package com.example.planit.navigation

sealed class Screen(val route: String) {
    object Calendar: Screen("calendar")
}