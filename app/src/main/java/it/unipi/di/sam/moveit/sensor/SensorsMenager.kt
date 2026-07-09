package it.unipi.di.sam.moveit.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import it.unipi.di.sam.moveit.utils.Constants

class SensorsMenager(private val context: Context) {

    private var gravita = FloatArray(3)
    private var gravitaInizializzata = false
    private var ultimoAccNorm = 0f
    private var ultimoGyroNorm = 0f

    // chi riceve i dati generati dal sensore, se qualcuno è in ascolto
    var callback: ((MovementData) -> Unit)? = null

    // getSystemService ritorna Object: va castato a SensorManager per accedere ai sensori
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val giroscopio = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> gestisciAccelerometro(event.values)
                Sensor.TYPE_GYROSCOPE -> gestisciGiroscopio(event.values)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // richiesto dall'interfaccia SensorEventListener, non serve qui
        }
    }

    private fun gestisciAccelerometro(values: FloatArray) {
        if (!gravitaInizializzata) {
            // prima lettura: si parte dal valore reale invece che da 0,
            // per non avere uno scarto iniziale enorme
            gravita[0] = values[0]
            gravita[1] = values[1]
            gravita[2] = values[2]
            gravitaInizializzata = true
        } else {
            // filtro passa-basso
            gravita[0] = Constants.ALPHA_FILTRO_PASSA_BASSO * gravita[0] + (1 - Constants.ALPHA_FILTRO_PASSA_BASSO) * values[0]
            gravita[1] = Constants.ALPHA_FILTRO_PASSA_BASSO * gravita[1] + (1 - Constants.ALPHA_FILTRO_PASSA_BASSO) * values[1]
            gravita[2] = Constants.ALPHA_FILTRO_PASSA_BASSO * gravita[2] + (1 - Constants.ALPHA_FILTRO_PASSA_BASSO) * values[2]
        }

        // movimento vero = grezzo - gravità stimata
        val movimentoX = values[0] - gravita[0]
        val movimentoY = values[1] - gravita[1]
        val movimentoZ = values[2] - gravita[2]

        val intensitaAcc = kotlin.math.sqrt(
            movimentoX * movimentoX + movimentoY * movimentoY + movimentoZ * movimentoZ
        )

        ultimoAccNorm = (intensitaAcc / Constants.MAX_INTENSITA_ACC).coerceIn(0f, 1f)

        emettiDatiMovimento(movimentoX, movimentoY, movimentoZ)
    }

    private fun gestisciGiroscopio(values: FloatArray) {
        val intensitaGyro = kotlin.math.sqrt(
            values[0] * values[0] + values[1] * values[1] + values[2] * values[2]
        )

        ultimoGyroNorm = (intensitaGyro / Constants.MAX_INTENSITA_GYRO).coerceIn(0f, 1f)
    }

    private fun emettiDatiMovimento(x: Float, y: Float, z: Float) {
        // ultimoGyroNorm è l'ultimo valore del giroscopio ricevuto, non
        // necessariamente sincronizzato con questo istante: accelerometro
        // e giroscopio arrivano come eventi indipendenti
        val magnitudineCombinata = Constants.PESO_ACC * ultimoAccNorm + Constants.PESO_GYRO * ultimoGyroNorm

        val dati = MovementData(
            x = x,
            y = y,
            z = z,
            magnitudine = magnitudineCombinata
        )

        callback?.invoke(dati)
    }

    fun startListening() {
        accelerometro?.also {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_GAME)
        }
        giroscopio?.also {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun endListening() {
        sensorManager.unregisterListener(listener)
    }
}