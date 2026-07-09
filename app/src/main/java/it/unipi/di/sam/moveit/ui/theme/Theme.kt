package it.unipi.di.sam.moveit.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary          = MoveItOrange,
    onPrimary        = MoveItOnPrimary,

    background       = MoveItBackgroundLight,
    onBackground     = MoveItOnBackgroundLight,

    surface          = MoveItSurfaceLight,
    onSurface        = MoveItOnBackgroundLight,

    error            = MoveItRed,
    onError          = MoveItOnPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary          = MoveItOrange,
    onPrimary        = MoveItOnPrimary,

    background       = MoveItBackgroundDark,
    onBackground     = MoveItOnBackgroundDark,

    surface          = MoveItSurfaceDark,
    onSurface        = MoveItOnBackgroundDark,

    error            = MoveItRed,
    onError          = MoveItOnPrimary
)

@Composable
fun MoveItTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}