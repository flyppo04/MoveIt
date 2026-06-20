package it.unipi.di.sam.moveit.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.IconButton
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.material3.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.unipi.di.sam.moveit.ui.theme.MoveItTheme

@Composable
fun HomeScreen(onPlayClick: () -> Unit,      // Definiamo cosa può succedere
               onHistoryClick: () -> Unit,
               onSettingsClick: () -> Unit
) {
    //main container for the home screen
    Box( modifier = Modifier.fillMaxSize() ){

        Column( modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopCenter)
            .padding(),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "MoveIt",
                 style = MaterialTheme.typography.displayLarge,
                 color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(100.dp))

            // BOTTONE PLAY
            Button(
                onClick = onPlayClick,
                modifier = Modifier.width(200.dp) // Larghezza fissa per estetica
            ) {
                Text("PLAY")
            }

            Spacer(modifier = Modifier.height(16.dp)) // Piccolo spazio tra i due bottoni

            OutlinedButton( // Una versione diversa del bottone (solo bordo)
                onClick = onHistoryClick,
                modifier = Modifier.width(200.dp)
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
    }


}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    MoveItTheme {
        // Qui passiamo delle funzioni "vuote" { } come mock
        HomeScreen(
            onPlayClick = {},
            onHistoryClick = {},
            onSettingsClick = {}
        )
    }
}