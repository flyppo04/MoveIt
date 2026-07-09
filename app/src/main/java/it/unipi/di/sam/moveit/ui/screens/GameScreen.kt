package it.unipi.di.sam.moveit.ui.screens

import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import it.unipi.di.sam.moveit.viewmodels.Countdown
import it.unipi.di.sam.moveit.viewmodels.Error
import it.unipi.di.sam.moveit.viewmodels.Finished
import it.unipi.di.sam.moveit.viewmodels.GameViewModel
import it.unipi.di.sam.moveit.viewmodels.Loading
import it.unipi.di.sam.moveit.viewmodels.Playing

// Un solo composable: crea il GameViewModel, blocca l'orientamento e
// disegna la UI in base allo stato. Nessuna preview (il ViewModel parte
// da solo nel suo init {} con sensori/MediaPlayer veri)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(songId: String, onExit: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity
    val application = context.applicationContext as Application

    val viewModel: GameViewModel = viewModel(
        factory = viewModelFactory {
            initializer { GameViewModel(application, songId) }
        }
    )
    val uiState by viewModel.uiState.collectAsState()

    // Stesso pattern gia' usato in CalibrationScreen: blocca l'orientamento in
    // portrait finche' la schermata e' visibile, poi lo ripristina.
    DisposableEffect(Unit) {
        val originalOrientation = activity?.requestedOrientation
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        onDispose {
            activity?.requestedOrientation =
                originalOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MoveIt") },
                actions = {
                    IconButton(onClick = {
                        // onEndGame() e' sicuro da chiamare anche se la
                        // partita e' gia' finita da sola o mai partita.
                        viewModel.onEndGame()
                        onExit()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Esci")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            val state = uiState

            when (state) {

                is Loading -> {
                    Text(
                        text = "Caricamento...",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                is Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.error,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(
                            onClick = { viewModel.onEndGame(); onExit() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Torna indietro")
                        }
                    }
                }

                is Countdown -> {
                    Text(
                        text = "${state.secondsLeft}",
                        style = MaterialTheme.typography.displayLarge,
                        fontSize = 96.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                is Playing -> {
                    val progress = if (state.duration > 0) {
                        (state.secondPassed.toFloat() / state.duration.toFloat()).coerceIn(0f, 1f)
                    } else 0f

                    val secondsLeft = ((state.duration - state.secondPassed) / 1000).coerceAtLeast(0)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${secondsLeft}s",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold
                        )

                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp)
                                .height(12.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                is Finished -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Punteggio",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "${state.score}",
                            style = MaterialTheme.typography.displayLarge,
                            fontSize = 72.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Button(
                            onClick = { viewModel.onEndGame(); onExit() },
                            modifier = Modifier.padding(top = 24.dp)
                        ) {
                            Text("Torna alla home")
                        }
                    }
                }

            }
        }
    }
}
