package it.unipi.di.sam.moveit.ui.screens

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import it.unipi.di.sam.moveit.data.Songs
import it.unipi.di.sam.moveit.scoring.MovementSample
import it.unipi.di.sam.moveit.scoring.MovesRecorder
import it.unipi.di.sam.moveit.scoring.SongMovesReference
import it.unipi.di.sam.moveit.sensor.SensorsMenager
import it.unipi.di.sam.moveit.utils.Constants
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.math.pow
import kotlin.math.sqrt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalibrationScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val activity = context as? Activity


    val sensorManager = remember{ SensorsMenager(context) }
    val movesRecorder = remember { MovesRecorder(sensorManager) }

    var isRecording by remember { mutableStateOf(false) }
    var results by remember { mutableStateOf(emptyList<MovementSample>()) }

    var contatoreRegistrazioni by remember { mutableStateOf(0) }

    // --- Stato per la registrazione di coreografie di riferimento ---
    var songId by remember { mutableStateOf("") }
    var player: MediaPlayer? by remember { mutableStateOf(null) }
    var choreographyStatus by remember { mutableStateOf("") }

    // secondi rimasti del countdown, null quando non e' in corso nessun countdown
    var countdown by remember { mutableStateOf<Int?>(null) }

    // riferimento alla coroutine di countdown+registrazione, serve per
    // poterla interrompere a meta' dal bottone "Interrompi"
    var recordingJob: Job? by remember { mutableStateOf(null) }

    // per poter lanciare una coroutine (per il delay del countdown) da dentro
    // un onClick, che di per se' non e' un contesto "suspend"
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {

        val actualOrinetation = activity?.requestedOrientation
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        onDispose {
            sensorManager.endListening()
            player?.release()
            activity?.requestedOrientation = actualOrinetation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    Column{

        TopAppBar(
            title = { Text("Calibrazione") },
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

        Button(
            onClick = {
                sensorManager.startListening()
                isRecording = true
                movesRecorder.startRecording()

            },
            enabled = !isRecording
        ) {
            Text("Start Recording")
        }

        Button(
            onClick = {
                sensorManager.endListening()
                isRecording = false
                results = movesRecorder.stopRecording()

                contatoreRegistrazioni++
                val nomeFile = if (contatoreRegistrazioni % 2 == 1) "calibration_A.csv" else "calibration_B.csv"
                salvaRisultatiSuFile(context, results, nomeFile)
            },
            enabled = isRecording
        ) {
            Text("End Recording")
        }

        Text("Prossima registrazione: ${if (contatoreRegistrazioni % 2 == 0) "A" else "B"}")

        // --- Sezione: registrazione coreografia di riferimento ---
        Text("Registra coreografia di riferimento")

        OutlinedTextField(
            value = songId,
            onValueChange = { songId = it },
            label = { Text("songId (deve esistere in Songs.list)") },
            enabled = !isRecording
        )

        Button(
            onClick = {
                // isRecording = true e' la PRIMA cosa che si fa, in modo
                // sincrono, prima ancora di aprire la coroutine: cosi' il
                // bottone si disabilita nello stesso istante del click,
                // senza finestre in cui un doppio tap potrebbe far partire
                // due MediaPlayer sulla stessa canzone.
                isRecording = true
                choreographyStatus = ""

                recordingJob = scope.launch {
                    val song = Songs.songById(songId)
                    if (song == null) {
                        choreographyStatus = "Nessuna canzone con id \"$songId\" in Songs.list"
                        isRecording = false
                        return@launch
                    }

                    val mediaPlayer = MediaPlayer.create(context, song.audioResId)
                    if (mediaPlayer == null) {
                        choreographyStatus = "Impossibile caricare l'audio"
                        isRecording = false
                        return@launch
                    }
                    player = mediaPlayer

                    // sensori accesi SUBITO, si "scaldano" durante il
                    // countdown (stesso motivo per cui GameViewModel fa lo
                    // stesso: il filtro passa-basso ha bisogno di un attimo
                    // di assestamento prima che i campioni siano affidabili)
                    sensorManager.startListening()

                    for (i in 3 downTo 1) {
                        countdown = i
                        delay(1000)
                    }
                    countdown = null

                    // registrazione vera + audio partono insieme, DOPO il countdown
                    movesRecorder.startRecording()
                    mediaPlayer.start()
                    choreographyStatus = "Registrazione in corso... balla!"

                    mediaPlayer.setOnCompletionListener {
                        val samples = movesRecorder.stopRecording()
                        sensorManager.endListening()
                        isRecording = false
                        recordingJob = null
                        results = samples

                        val reference = SongMovesReference(songid = songId, moves = samples)
                        val json = Json.encodeToString(reference)

                        val file = File(context.filesDir, "$songId.json")
                        file.writeText(json)

                        choreographyStatus = "Salvato in: ${file.absolutePath}\n(${samples.size} campioni)"
                    }
                }
            },
            enabled = !isRecording && songId.isNotBlank()
        ) {
            Text(
                when {
                    countdown != null -> "Parte tra ${countdown}..."
                    isRecording -> "Registrazione in corso..."
                    else -> "Avvia canzone + registrazione coreografia"
                }
            )
        }

        Button(
            onClick = {
                // cancella la coroutine ovunque si trovi (durante il
                // countdown o durante la registrazione vera): il codice
                // dopo il punto di cancellazione semplicemente non esegue
                // piu', quindi non serve distinguere i due casi qui
                recordingJob?.cancel()
                recordingJob = null
                countdown = null

                // safe anche se non era mai partita per davvero: se
                // startRecording() non era ancora stato chiamato, questo si
                // limita ad azzerare un callback gia' nullo
                movesRecorder.stopRecording()
                sensorManager.endListening()

                // stop() lancia IllegalStateException se il player non era
                // in riproduzione (es. interrotto durante il countdown,
                // prima che start() fosse mai chiamato) - isPlaying evita
                // di doverlo gestire con un try/catch
                if (player?.isPlaying == true) {
                    player?.stop()
                }
                player?.release()
                player = null

                isRecording = false
                choreographyStatus = "Registrazione interrotta."
            },
            enabled = isRecording
        ) {
            Text("Interrompi")
        }

        // countdown grande e visibile, utile quando ti allontani dal telefono
        // per prepararti a ballare
        countdown?.let {
            Text(
                text = "$it",
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Text(choreographyStatus)

        LazyColumn(){
            items(results) {sample ->
                val intensitaAcc = sqrt(
                    sample.data.x.pow(2) + sample.data.y.pow(2) + sample.data.z.pow(2)
                )
                val accNorm = (intensitaAcc / Constants.MAX_INTENSITA_ACC).coerceIn(0f, 1f)

                val gyroNorm = ((sample.data.magnitudine - Constants.PESO_ACC * accNorm)
                        / Constants.PESO_GYRO).coerceIn(0f, 1f)

                val vec = sample.data

                Text(
                    "t=${sample.timestampMs}ms  aN  =${"%.2f".format(accNorm)}  gN=${"%.2f".format(gyroNorm)}  [${"%.2f".format(vec.x)}, ${"%.2f".format(vec.y)}, ${"%.2f".format(vec.z)}]"
                )

                //Log.d("Calibration", "t=${sample.timestampMs}ms accNorm=${accNorm} gyroNorm=${gyroNorm} x=${sample.data.x} y=${sample.data.y} z=${sample.data.z}")
            }
        }



    }

}

fun salvaRisultatiSuFile(context: Context, risultati: List<MovementSample>, nomeFile: String) {
    val file = File(context.filesDir, nomeFile)
    file.bufferedWriter().use { writer ->
        writer.write("t,accNorm,gyroNorm,x,y,z\n")
        risultati.forEach { sample ->
            val intensitaAcc = sqrt(sample.data.x.pow(2) + sample.data.y.pow(2) + sample.data.z.pow(2))
            val accNorm = (intensitaAcc / Constants.MAX_INTENSITA_ACC).coerceIn(0f, 1f)
            val gyroNorm = ((sample.data.magnitudine - Constants.PESO_ACC * accNorm) / Constants.PESO_GYRO).coerceIn(0f, 1f)
            writer.write("${sample.timestampMs},$accNorm,$gyroNorm,${sample.data.x},${sample.data.y},${sample.data.z}\n")
        }
    }
    Log.d("Calibration", "Salvato in: ${file.absolutePath}")
}

@Preview(showBackground = true)
@Composable
fun CalibrationScreenPreview() {
    CalibrationScreen()
}
