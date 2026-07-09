package it.unipi.di.sam.moveit.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.unipi.di.sam.moveit.data.ThemePreferences
import it.unipi.di.sam.moveit.ui.theme.MoveItTheme
import it.unipi.di.sam.moveit.utils.Constants.TEMA_DARK
import it.unipi.di.sam.moveit.utils.Constants.TEMA_LIGHT
import kotlinx.coroutines.launch
import it.unipi.di.sam.moveit.utils.Constants.TEMA_SYSTEM



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(onBack: () -> Unit, onCalibrationClick: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // remember { } tiene stabile lo stesso Flow tra le ricomposizioni,
    // invece di ricrearne uno nuovo ogni volta (stesso principio già usato
    // per GameRepository in HistoryScreen)
    val themeFlow = remember { ThemePreferences.themeFlow(context) }
    val currentTheme by themeFlow.collectAsState(initial = TEMA_SYSTEM)

    Scaffold(

        topBar = {
            TopAppBar(
                title = { Text("Impostazioni") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            Text(
                text = "Tema",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(modifier = Modifier.padding(bottom = 16.dp)) {
                RadioButton(
                    selected = currentTheme == TEMA_LIGHT,
                    onClick = {
                        scope.launch { ThemePreferences.setTheme(context, TEMA_LIGHT) }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = "tema chiaro",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Row(modifier = Modifier.padding(bottom = 16.dp)) {
                RadioButton(
                    selected = currentTheme == TEMA_DARK,
                    onClick = {
                        scope.launch { ThemePreferences.setTheme(context, TEMA_DARK) }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = "tema scuro",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Row(modifier = Modifier.padding(bottom = 16.dp)) {
                RadioButton(
                    selected = currentTheme == TEMA_SYSTEM,
                    onClick = {
                        scope.launch { ThemePreferences.setTheme(context, TEMA_SYSTEM) }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = "segui il sistema",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Text(
                text = "Sezione Sviluppatore",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = onCalibrationClick,
                modifier = Modifier
                    .width(264.dp) // Larghezza fissa per estetica
                    .height(40.dp) // Altezza fissa per estetica
            ) {
                Text(text = "Calibration/CratingJSON", fontSize = 16.sp)
            }
        }
    }

}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingScreenPreview() {
    MoveItTheme() {
        SettingScreen(
            onBack = {},
            onCalibrationClick = {}
        )
    }

}