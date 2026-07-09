package it.unipi.di.sam.moveit.scoring

import it.unipi.di.sam.moveit.sensor.MovementData
import it.unipi.di.sam.moveit.utils.Constants.MAX_DISTANZA_VETTORIALE
import it.unipi.di.sam.moveit.utils.Constants.PASSO_GRIGLIA_MS
import it.unipi.di.sam.moveit.utils.Constants.FINESTRA_GRIGLIA_MS
import it.unipi.di.sam.moveit.utils.Constants.PUNTEGGIO_GREZZO_MAX
import it.unipi.di.sam.moveit.utils.Constants.PUNTEGGIO_GREZZO_MIN
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

// object perché non serve stato: ogni valutazione è indipendente dalle altre
object ScoringEngine {

    // confronto tra due campioni singoli, in un solo istante

    private fun scoreFromVectorDistance(detected: MovementData, reference: MovementData): Int {
        val deltaX = detected.x - reference.x
        val deltaY = detected.y - reference.y
        val deltaZ = detected.z - reference.z

        // distanza euclidea tra i due vettori: 0 = movimenti identici,
        // MAX_DISTANZA_VETTORIALE o più = troppo diversi, punteggio 0
        val vecDistance = sqrt( deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ )
        val score = (1 - vecDistance/MAX_DISTANZA_VETTORIALE).coerceIn(0f, 1f)
        return (score * 100).toInt()
    }

    private fun scoreFromMagnitudeDifference(detected: MovementData, reference: MovementData): Int {
        // magnitudine = quanto forte ci si muove, ignorando la direzione
        val deltaMag = abs(detected.magnitudine - reference.magnitudine)
        val score = (1 - deltaMag).coerceIn(0f, 1f)
        return (score * 100).toInt()
    }

    // confronta un campione con uno di riferimento; evaluateRecording la
    // chiama una volta per ogni istante scelto lungo la coreografia
    fun evaluate(det: MovementData, ref: MovementData): Int {
        val scoreV = scoreFromVectorDistance(det, ref)
        val scoreM = scoreFromMagnitudeDifference(det, ref)
        return (scoreV + scoreM) / 2
    }

    // valuta tramite griglia fissa: si campiona ogni PASSO_GRIGLIA_MS invece
    // di confrontare ogni singolo dato o solo i picchi

    // per ogni checkpoint si cerca, entro una finestra di tolleranza
    // FINESTRA_GRIGLIA_MS, il campione del giocatore col punteggio migliore
    fun evaluateRecording(
        riferimento: List<MovementSample>,
        daValutare: List<MovementSample>
    ): Double {
        if (riferimento.isEmpty() || daValutare.isEmpty()) return 0.0

        val tInizio = riferimento.first().timestampMs
        val tFine = riferimento.last().timestampMs

        val punteggi = mutableListOf<Int>()
        var t = tInizio

        while (t <= tFine) {
            val puntoRiferimento = riferimento
                .filter { abs(it.timestampMs - t) <= FINESTRA_GRIGLIA_MS / 2 }
                .minByOrNull { abs(it.timestampMs - t) }

            if (puntoRiferimento == null) {
                // nessun campione di riferimento vicino a "t" (capita ai
                // bordi della registrazione): si salta, non è un errore
                t += PASSO_GRIGLIA_MS
                continue
            }

            val candidati = daValutare.filter { abs(it.timestampMs - t) <= FINESTRA_GRIGLIA_MS }

            if (candidati.isEmpty()) {
                // il giocatore non ha dati in questa finestra: punteggio 0
                punteggi.add(0)
            } else {
                val migliore = candidati.maxByOrNull { evaluate(puntoRiferimento.data, it.data) }!!
                punteggi.add(evaluate(puntoRiferimento.data, migliore.data))
            }

            t += PASSO_GRIGLIA_MS
        }

        return if (punteggi.isNotEmpty()) punteggi.average() else 0.0
    }

    // trasformazione lineare con clamp: il punteggio grezzo oscilla sempre
    // in una fascia stretta, mai su tutto 0-100, quindi va riscalato
    fun scaledScore(rawScore: Double): Int {
        val normalizzato = (rawScore - PUNTEGGIO_GREZZO_MIN) / (PUNTEGGIO_GREZZO_MAX - PUNTEGGIO_GREZZO_MIN)
        return (normalizzato * 100).coerceIn(0.0, 100.0).roundToInt()
    }
}