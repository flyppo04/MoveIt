package it.unipi.di.sam.moveit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.unipi.di.sam.moveit.ui.theme.MoveItTheme

@Composable
fun HomeScreen(
    onPlayClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    // contenitore principale della home
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .align(Alignment.TopCenter)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(160.dp))

            Text(
                text = "MoveIt",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 96.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(240.dp))

            // bottone play
            Button(
                onClick = onPlayClick,
                modifier = Modifier
                    .width(240.dp)
                    .height(80.dp)
            ) {
                Text(text = "PLAY", fontSize = 36.sp)
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // versione del bottone solo bordo
            OutlinedButton(
                onClick = onHistoryClick,
                modifier = Modifier.width(184.dp)
            ) {
                Text("HISTORY")
            }
        }

        // icona impostazioni, ancorata in alto a destra con margine dalla status bar
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(8.dp)
                .size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Impostazioni",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}


@Preview(name = "Light Mode", showBackground = true, showSystemUi = true)
@Preview(name = "Night Mode", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    val isDark = isSystemInDarkTheme()
    MoveItTheme(darkTheme = isDark) {
        HomeScreen(
            onPlayClick = {},
            onHistoryClick = {},
            onSettingsClick = {}
        )
    }
}