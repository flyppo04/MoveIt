package it.unipi.di.sam.moveit.scoring

import it.unipi.di.sam.moveit.sensor.MovementData
import it.unipi.di.sam.moveit.utils.Constants.MAX_DISTANZA_VETTORIALE
import kotlin.math.abs
import kotlin.math.sqrt

// object perche non c'è bisogno di stato (che le classi hanno) in quanto ogni valutazione è a se stante
object ScoringEngine {

    private fun scoreFromVectorDistance(detected: MovementData, reference: MovementData): Int {
        val deltaX = detected.x - reference.x
        val deltaY = detected.y - reference.y
        val deltaZ = detected.z - reference.z

        val vecDisatnce = sqrt( deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ )
        val score = (1 - vecDisatnce/MAX_DISTANZA_VETTORIALE).coerceIn(0f, 1f)
        return (score * 100).toInt()
    }

    private fun scoreFromMagnitudeDifference(detected: MovementData, reference: MovementData): Int {
        val deltaMag = abs(detected.magnitudine - reference.magnitudine)
        val score = (1 - deltaMag).coerceIn(0f, 1f)
        return (score * 100).toInt()
    }

    fun evaluate(det: MovementData, ref: MovementData):Int {
       val scoreV = scoreFromVectorDistance(det, ref)
       val scoreM = scoreFromMagnitudeDifference(det, ref)
       val score = ((scoreV + scoreM)/2) //no cast int perche se x / y entrambi sono int torna int
       return score
    }
}