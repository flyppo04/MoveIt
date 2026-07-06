package it.unipi.di.sam.moveit.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.IconButton
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.material3.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.unipi.di.sam.moveit.ui.theme.MoveItTheme

@Composable
fun HomeScreen(onPlayClick: () -> Unit,      // Definiamo cosa può succedere
               onHistoryClick: () -> Unit,
               onSettingsClick: () -> Unit,
               onCalibrationClick: () -> Unit
) {
    //main container for the home screen
    Box(
        modifier = Modifier.fillMaxSize()
    ){

        Column( modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .align(Alignment.TopCenter)
            .padding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(160.dp))

            Text(text = "MoveIt",
                 style = MaterialTheme.typography.displayLarge,
                 color = MaterialTheme.colorScheme.primary,
                 fontSize = 96.sp, // forzato io ad avre testo più grande
                 fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(240.dp))

            // BOTTONE PLAY
            Button(
                onClick = onPlayClick,
                modifier = Modifier
                    .width(240.dp) // Larghezza fissa per estetica
                    .height(80.dp) // Altezza fissa per estetica
            ) {
                Text(text = "PLAY", fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.height(24.dp)) // Piccolo spazio tra i due bottoni

            OutlinedButton( // versione del bottone solo bordo
                onClick = onHistoryClick,
                modifier = Modifier.width(184.dp)
            ) {
                Text("HISTORY")
            }

        }

        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.TopEnd) // La "attacchiamo" in alto a destra
                .padding(16.dp)          // Un po' di margine dal bordo
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Impostazioni"
            )
        }

        IconButton(
            onClick = onCalibrationClick,
            modifier = Modifier
                .align(Alignment.BottomStart) // La "attacchiamo" in alto a destra
                .padding(16.dp)          // Un po' di margine dal bordo
        ) {
            Icon(
                imageVector = Icons.Default.BugReport,
                contentDescription = "Calibration"
            )
        }

    }


}


@Preview(name = "Light Mode" , showBackground = true, showSystemUi = true)
@Preview(name = "Night Mode" , uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, showBackground = true, showSystemUi = true)

@Composable
fun HomeScreenPreview() {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    MoveItTheme(darkTheme = isDark) {
        // Qui passiamo delle funzioni "vuote" { } come mock
        HomeScreen(
            onPlayClick = {},
            onHistoryClick = {},
            onSettingsClick = {},
            onCalibrationClick = {}
        )
    }
}