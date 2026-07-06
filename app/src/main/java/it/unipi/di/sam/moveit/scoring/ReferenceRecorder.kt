package it.unipi.di.sam.moveit.scoring

import it.unipi.di.sam.moveit.sensor.MovementData
import it.unipi.di.sam.moveit.sensor.SensorsMenager
import it.unipi.di.sam.moveit.utils.Constants.INTERVALLO_CAMPIONAMENTO_MS

class ReferenceRecorder(private val sensorsMenager: SensorsMenager){

    private var startTime : Long = 0L
    private val recordedSamples = mutableListOf<MovementSample>()

    fun startRecording(){
        startTime = System.currentTimeMillis()
        recordedSamples.clear()
        var referenceTime = INTERVALLO_CAMPIONAMENTO_MS

        sensorsMenager.callback = {movementData ->
            val timestamp = System.currentTimeMillis() - startTime
            if (timestamp >= referenceTime) {
                recordedSamples.add(MovementSample(timestamp, movementData))
                referenceTime += INTERVALLO_CAMPIONAMENTO_MS
            }
        }


    }

    fun stopRecording(): List<MovementSample> {
        sensorsMenager.callback = null
        return recordedSamples.toList()
    }

}