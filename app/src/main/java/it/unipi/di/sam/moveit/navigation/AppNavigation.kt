package it.unipi.di.sam.moveit.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController

import it.unipi.di.sam.moveit.ui.screens.HomeScreen
import it.unipi.di.sam.moveit.ui.screens.SettingScreen

@Composable
fun AppNavigation(navController: NavHostController){
    // NavHost is the container that displays the current screen component
    NavHost(navController = navController, startDestination = "home" // It has to have a start destination
    ){
        // defines route "home", When you go this route, HomeScreen is called, which will create
        // the home interface. This function is passed a callback function that
        // tells HomeScreen what to do when the settings button is clicked.
        composable(route = "home") {
            // Here is the composable function that creates the HomeScreen
            HomeScreen(onSettingsClick = {navController.navigate("setting")},
                       onPlayClick = {},
                       onHistoryClick = {}
            )
        }

        composable(route = "setting") {
            // Qui inseriremo il Composable dei Dettagli
            SettingScreen(onBack = {navController.popBackStack()})

        }

    }
}