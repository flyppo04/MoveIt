package it.unipi.di.sam.moveit.data

import it.unipi.di.sam.moveit.R

// Classe che rappresenta struttura song
data class Song (
    val id: String,
    val title: String,
    val artist: String,
    val duration: Long = 0,
    val audioResId: Int,
    val artWork: Int? = null
)

// Oggetto che contiene la lista di canzoni
object Songs {
    val list: List<Song> = listOf(
        Song(
            id = "mueve_la_colita",              // stringa a tua scelta, univoca — usala anche per la coreografia dopo
            title = "Mueve la colita",           // testo che vedrai in SongSelectionScreen
            artist = "El Gato DJ",
            audioResId = R.raw.mueve_la_colita,  // deve combaciare col nome del file in res/raw
        )

    )

    //metodo per recuperare una canzone in base all'id
    fun songById(songId: String): Song? = list.find({song -> song.id == songId})
}
