package it.unipi.di.sam.moveit.scoring
import it.unipi.di.sam.moveit.sensor.MovementData
import kotlinx.serialization.Serializable

@Serializable
data class MovementSample(
    val timestampMs: Long,
    val data: MovementData
)