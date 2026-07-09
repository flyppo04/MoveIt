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

// object perche non c'è bisogno di stato (che le classi hanno) in quanto ogni valutazione è a se stante
object ScoringEngine {

    // ============================================================
    // LIVELLO 1: confronto tra DUE CAMPIONI SINGOLI (invariato da prima)
    // ============================================================
    // Tutto questo blocco esisteva già. Risponde alla domanda:
    // "in QUESTO preciso istante, quanto il movimento rilevato (detected)
    // assomiglia al movimento di riferimento (reference)?"
    // Non sa nulla di "prima" o "dopo": vede solo due fotografie isolate
    // dell'accelerometro/giroscopio e le confronta.

    private fun scoreFromVectorDistance(detected: MovementData, reference: MovementData): Int {
        val deltaX = detected.x - reference.x
        val deltaY = detected.y - reference.y
        val deltaZ = detected.z - reference.z

        // distanza euclidea tra i due vettori di movimento (x,y,z):
        // quanto sono "lontani" nello spazio 3D i due movimenti istantanei
        val vecDisatnce = sqrt( deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ )

        // normalizziamo: distanza 0 -> punteggio 1 (identici)
        //                distanza >= MAX_DISTANZA_VETTORIALE -> punteggio 0 (troppo diversi)
        val score = (1 - vecDisatnce/MAX_DISTANZA_VETTORIALE).coerceIn(0f, 1f)
        return (score * 100).toInt()
    }

    private fun scoreFromMagnitudeDifference(detected: MovementData, reference: MovementData): Int {
        // magnitudine = "quanto forte" ti stai muovendo, ignorando la direzione
        val deltaMag = abs(detected.magnitudine - reference.magnitudine)
        val score = (1 - deltaMag).coerceIn(0f, 1f)
        return (score * 100).toInt()
    }

    /**
     * Confronta UN campione rilevato contro UN campione di riferimento.
     * Questa funzione non cambia: resta il "mattone" elementare del confronto.
     * evaluateRecording() (sotto) la chiama molte volte, una per ogni istante
     * scelto lungo la coreografia.
     */
    fun evaluate(det: MovementData, ref: MovementData): Int {
        val scoreV = scoreFromVectorDistance(det, ref)
        val scoreM = scoreFromMagnitudeDifference(det, ref)
        val score = ((scoreV + scoreM)/2) // no cast int perche se x / y entrambi sono int torna int
        return score
    }

    // ============================================================
    // LIVELLO 2: confronto tra DUE REGISTRAZIONI INTERE (NUOVO)
    // ============================================================
    // Questa è la parte aggiunta. Risponde alla domanda:
    // "durante TUTTA la coreografia, quanto sei stato bravo in media?"
    //
    // METODO: "griglia fissa". Invece di confrontare ogni singolo campione
    // (ce ne sono troppi, e il rumore tra un campione e l'altro confonde la
    // media) o solo i "picchi" del riferimento (metodo scartato: instabile
    // quando il riferimento è quasi fermo, vedi test), scegliamo istanti
    // di controllo a intervalli regolari e fissi (ogni PASSO_GRIGLIA_MS,
    // es. 450ms). Per ognuno di questi istanti, cerchiamo nella
    // registrazione da valutare il campione che si avvicina di più
    // (entro una finestra di tolleranza FINESTRA_GRIGLIA_MS, es. ±100ms),
    // per assorbire il normale ritardo/anticipo umano nel tempismo.

    /**
     * Valuta un'intera esecuzione (daValutare) contro un'intera coreografia
     * di riferimento (riferimento), camminando lungo il riferimento a passi
     * regolari e cercando il miglior match in ciascuna finestra di tolleranza.
     *
     * @return punteggio medio (0-100) sull'intera registrazione
     */
    fun evaluateRecording(
        riferimento: List<MovementSample>,
        daValutare: List<MovementSample>
    ): Double {
        // guardia: se manca una delle due registrazioni non c'è nulla da valutare
        if (riferimento.isEmpty() || daValutare.isEmpty()) return 0.0

        val tInizio = riferimento.first().timestampMs
        val tFine = riferimento.last().timestampMs

        val punteggi = mutableListOf<Int>()
        var t = tInizio

        // camminiamo lungo la coreografia a passi fissi di PASSO_GRIGLIA_MS
        // (es. t = 0, 450, 900, 1350, ... fino alla fine del riferimento)
        while (t <= tFine) {

            // STEP 1: trova il campione REALE del riferimento più vicino
            // all'istante teorico "t" (il sensore non campiona esattamente
            // ogni 450ms, quindi cerchiamo il più vicino a disposizione,
            // scartando se troppo lontano piu' di meta' finestra)
            val puntoRiferimento = riferimento
                .filter { abs(it.timestampMs - t) <= FINESTRA_GRIGLIA_MS / 2 }
                .minByOrNull { abs(it.timestampMs - t) }

            if (puntoRiferimento == null) {
                // nessun campione di riferimento vicino a questo istante
                // (capita ai bordi della registrazione): saltiamo il punto
                // della griglia, non e' un errore del giocatore
                t += PASSO_GRIGLIA_MS
                continue
            }

            // STEP 2: tra i campioni REALI del giocatore (daValutare) intorno
            // a questo stesso istante "t" (finestra piu' larga, +-FINESTRA_GRIGLIA_MS,
            // per dare margine al ritardo/anticipo umano), prendi quello che
            // ottiene il MIGLIOR punteggio contro il punto di riferimento.
            // Nota: "il migliore", non "il piu' vicino nel tempo" — cosi'
            // assorbiamo davvero il jitter di tempismo umano (vedi discussione
            // precedente sul perche' "piu' vicino" vanificherebbe la tolleranza).
            val candidati = daValutare.filter { abs(it.timestampMs - t) <= FINESTRA_GRIGLIA_MS }

            if (candidati.isEmpty()) {
                // il giocatore non ha dati sensore in questa finestra
                // (es. si e' fermato prima, o l'esecuzione e' incompleta):
                // punteggio 0 per questo punto della griglia, giustamente
                // penalizzante
                punteggi.add(0)
            } else {
                val migliore = candidati.maxByOrNull { evaluate(puntoRiferimento.data, it.data) }!!
                punteggi.add(evaluate(puntoRiferimento.data, migliore.data))
            }

            t += PASSO_GRIGLIA_MS
        }

        // media di tutti i punteggi raccolti lungo la griglia: e' il
        // punteggio finale dell'intera esecuzione
        return if (punteggi.isNotEmpty()) punteggi.average() else 0.0
    }

    /**
     * Riscala il punteggio grezzo (che in pratica oscilla sempre in una fascia
     * stretta, mai su tutto 0-100) sull'intera scala mostrata al giocatore.
     * Trasformazione lineare, con clamp ai bordi.
     */
    fun scaledScore(rawScore: Double): Int {
        val normalizzato = (rawScore - PUNTEGGIO_GREZZO_MIN) / (PUNTEGGIO_GREZZO_MAX - PUNTEGGIO_GREZZO_MIN)
        return (normalizzato * 100).coerceIn(0.0, 100.0).roundToInt()
    }
}


// da valutare poi se c'è tempo idea MAX_DIFFERENZA_MAGNITUDINE