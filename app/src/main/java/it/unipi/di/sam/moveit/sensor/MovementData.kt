package it.unipi.di.sam.moveit.sensor

import kotlinx.serialization.Serializable

@Serializable
data class MovementData (
    val x: Float,           // dal solo accelerometro (dopo filtro)
    val y: Float,           // dal solo accelerometro (dopo filtro)
    val z: Float,           // dal solo accelerometro (dopo filtro)
    val magnitudine: Float  // combinazione pesata acc+gyro, normalizzata [0,1]
)