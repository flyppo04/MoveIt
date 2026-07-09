package it.unipi.di.sam.moveit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import it.unipi.di.sam.moveit.data.ThemePreferences
import it.unipi.di.sam.moveit.ui.theme.MoveItTheme
import it.unipi.di.sam.moveit.navigation.AppNavigation
import it.unipi.di.sam.moveit.utils.Constants.TEMA_DARK
import it.unipi.di.sam.moveit.utils.Constants.TEMA_LIGHT
import it.unipi.di.sam.moveit.utils.Constants.TEMA_SYSTEM


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current

            // remember { } tiene stabile lo stesso Flow tra le ricomposizioni
            val themeFlow = remember { ThemePreferences.themeFlow(context) }
            val currentTheme by themeFlow.collectAsState(initial = TEMA_SYSTEM)

            val useDarkTheme = when (currentTheme) {
                TEMA_DARK -> true
                TEMA_LIGHT -> false
                else -> isSystemInDarkTheme()
            }

            MoveItTheme(darkTheme = useDarkTheme) {

                val navController = rememberNavController()

                AppNavigation(navController)

            }
        }
    }
}
