package it.unipi.di.sam.moveit.ui.screens

import android.app.Activity
import android.content.pm.ActivityInfo
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import it.unipi.di.sam.moveit.scoring.MovementSample
import it.unipi.di.sam.moveit.scoring.ReferenceRecorder
import it.unipi.di.sam.moveit.sensor.SensorsMenager
import it.unipi.di.sam.moveit.utils.Constants
import kotlin.math.pow
import kotlin.math.sqrt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalibrationScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val activity = context as? Activity


    val sensorManager = remember{ SensorsMenager(context) }
    val referenceRecorder = remember { ReferenceRecorder(sensorManager) }

    var isRecording by remember { mutableStateOf(false) }
    var results by remember { mutableStateOf(emptyList<MovementSample>()) }

    DisposableEffect(Unit) {

        val actualOrinetation = activity?.requestedOrientation
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        onDispose {
            sensorManager.endListening()
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
                referenceRecorder.startRecording()

            },
            enabled = !isRecording
        ) {
            Text("Start Recording")
        }

        Button(
            onClick = {
                sensorManager.endListening()
                isRecording = false
                results = referenceRecorder.stopRecording()
            },
            enabled = isRecording
        ) {
            Text("End Recording")
        }

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

                Log.d("Calibration", "t=${sample.timestampMs}ms accNorm=${accNorm} gyroNorm=${gyroNorm} x=${sample.data.x} y=${sample.data.y} z=${sample.data.z}")
            }
        }

    }

}


@Preview(showBackground = true)
@Composable
fun CalibrationScreenPreview() {
    CalibrationScreen()
}

