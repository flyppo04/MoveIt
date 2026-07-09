package it.unipi.di.sam.moveit.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import it.unipi.di.sam.moveit.ui.screens.CalibrationScreen
import it.unipi.di.sam.moveit.ui.screens.GameScreen
import it.unipi.di.sam.moveit.ui.screens.HistoryScreen
import it.unipi.di.sam.moveit.ui.screens.HomeScreen
import it.unipi.di.sam.moveit.ui.screens.SettingScreen
import it.unipi.di.sam.moveit.ui.screens.SongSelectionScreen

@Composable
fun AppNavigation(navController: NavHostController){
    // NavHost is the container that displays the current screen component
    NavHost(navController = navController,
            startDestination = "home", // It has to have a start destination
            enterTransition = { fadeIn(animationSpec = tween(90)) },
            exitTransition = { fadeOut(animationSpec = tween(90)) },
            popEnterTransition = { fadeIn(animationSpec = tween(90)) },
            popExitTransition = { fadeOut(animationSpec = tween(90)) }
    ){
        // defines route "home", When you go this route, HomeScreen is called, which will create
        // the home interface. This function is passed a callback function that
        // tells HomeScreen what to do when the settings button is clicked.
        composable(route = "home") {
            // Here is the composable function that creates the HomeScreen
            HomeScreen(onSettingsClick = {navController.navigate("setting")},
                onPlayClick = {navController.navigate("songSelection")},
                onHistoryClick = {navController.navigate("History")},
            )
        }

        composable(route = "setting") {
            // Qui inseriremo il Composable dei Dettagli
            SettingScreen(onBack = {navController.popBackStack()},
                          onCalibrationClick = {navController.navigate("calibration")}
            )

        }

        composable(route = "calibration") {
            CalibrationScreen(onBack = {navController.popBackStack()})
        }

        composable(route = "songSelection") {
            SongSelectionScreen(
                onBack = { navController.popBackStack() },
                onSongSelected = { songId -> navController.navigate("game/$songId") }
            )
        }

        composable(route = "History") {
            // Qui inseriremo il Composable dei Dettagli
            HistoryScreen(onBack = {navController.popBackStack()})

        }

        // "{songId}" è un segmento variabile della rotta: il valore passato in
        // navigate("game/qualcosa") finisce negli arguments del backStackEntry,
        // da cui lo leggiamo sotto per passarlo a GameScreen.
        composable(
            route = "game/{songId}",
            arguments = listOf(navArgument("songId") { type = NavType.StringType })
        ) { backStackEntry ->
            val songId = backStackEntry.arguments?.getString("songId") ?: ""
            GameScreen(
                songId = songId,
                onExit = {
                    // torna alla home "consumando" sia GameScreen sia
                    // SongSelectionScreen dallo stack, per non tornare
                    // alla selezione canzone premendo indietro dalla home
                    navController.popBackStack("home", inclusive = false)
                }
            )
        }

    }
}
