package it.unipi.di.sam.moveit.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import it.unipi.di.sam.moveit.ui.screens.CalibrationScreen
import it.unipi.di.sam.moveit.ui.screens.GameScreen
import it.unipi.di.sam.moveit.ui.screens.HistoryScreen
import it.unipi.di.sam.moveit.ui.screens.HomeScreen
import it.unipi.di.sam.moveit.ui.screens.SettingScreen
import it.unipi.di.sam.moveit.ui.screens.SongSelectionScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    // Le transizioni di default di NavHost sono più lente, e questo causava
    // un problema concreto (sembrva lento/ non funzionante)
    // con transizioni rapide imposte il problema non si pone più
    NavHost(
        navController = navController,
        startDestination = "home",
        enterTransition = { fadeIn(animationSpec = tween(90)) },
        exitTransition = { fadeOut(animationSpec = tween(90)) },
        popEnterTransition = { fadeIn(animationSpec = tween(90)) },
        popExitTransition = { fadeOut(animationSpec = tween(90)) }
    ) {
        composable(route = "home") {
            HomeScreen(
                onSettingsClick = { navController.navigate("setting") },
                onPlayClick = { navController.navigate("songSelection") },
                onHistoryClick = { navController.navigate("history") },
            )
        }

        composable(route = "setting") {
            SettingScreen(
                onBack = { navController.popBackStack() },
                onCalibrationClick = { navController.navigate("calibration") }
            )
        }

        composable(route = "calibration") {
            CalibrationScreen(onBack = { navController.popBackStack() })
        }

        composable(route = "songSelection") {
            SongSelectionScreen(
                onBack = { navController.popBackStack() },
                onSongSelected = { songId -> navController.navigate("game/$songId") }
            )
        }

        composable(route = "history") {
            HistoryScreen(onBack = { navController.popBackStack() })
        }

        // "{songId}" è un segmento variabile della rotta: il valore passato
        // in navigate("game/qualcosa") finisce negli arguments del
        // backStackEntry, da cui viene letto subito sotto per passarlo a
        // GameScreen.
        composable(
            route = "game/{songId}",
            arguments = listOf(navArgument("songId") { type = NavType.StringType })
        ) { backStackEntry ->
            val songId = backStackEntry.arguments?.getString("songId") ?: ""
            GameScreen(
                songId = songId,
                onExit = {
                    // torna alla home consumando sia GameScreen sia
                    // songSelectionScreen dallo stack, così il tasto indietro
                    // dalla schermata game non riporta alla selezione canzone.
                    navController.popBackStack("home", inclusive = false)
                }
            )
        }
    }
}