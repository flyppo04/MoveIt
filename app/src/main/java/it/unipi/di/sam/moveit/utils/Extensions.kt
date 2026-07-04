package it.unipi.di.sam.moveit.utils

import java.text.SimpleDateFormat   // formattazione date
import java.util.Date               // rappresenta un punto nel tempo (millisecondi)
import java.util.Locale             // impostazione regionale utente (italiano, inglese, ecc.)

// Le Extensions mi permettono di estendere tipi/classi preesistenti con nuove funzioni senza toccarne la struttura o gerarchia

fun Long.toDataFormattata(): String {
    // SimpleDateFormat formatta una Date secondo un pattern.
    // Locale.getDefault() usa il locale dell'utente per eventuali adattamenti
    val formatter = SimpleDateFormat("dd/MM/yyyy 'alle' HH:mm", Locale.getDefault())

    // Date(this): crea un oggetto Date dal Long.
    // "this" qui si riferisce al Long su cui viene chiamata l'extension e non la classe Long
    return formatter.format(Date(this))
}

// m:ss
fun Int.toStringDurata(): String {
    val minuti  = this / 60
    val secondi = this % 60

    return "$minuti:${"%02d".format(secondi)}"
}

// Converte un punteggio 0-100 nel numero di stelle (1, 2 o 3)
// Usato nella ResultScreen e nelle PartitaCard dello storico
fun Int.toPunteggioStelle(): Int {
    return when {
        this >= 70 -> 3
        this >= 40 -> 2
        else       -> 1
    }
    // "when" senza argomento funziona come un if-else a cascata
}

// Converte punteggio in feedback nella GameScreen ("PERFECT!", "GOOD", ecc.).
// Ritorna null se il punteggio è 0 (nessun movimento rilevato → nessun feedback).
fun Int.toEtichettaFeedback(): String? {
    return when {
        this >= 80 -> "PERFECT!"
        this >= 60 -> "GOOD"
        this >= 30 -> "OK"
        this > 0   -> "MISS"
        else       -> null
    }
}
