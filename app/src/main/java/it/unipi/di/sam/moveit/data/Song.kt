package it.unipi.di.sam.moveit.data

// Classe che rappresenta struttura song
data class Song (
    val id: String,
    val title: String,
    val artist: String,
    val duration: Long = 0,
    val path: String,
    val artWork: Int? = null
)

// Oggetto che contiene la lista di canzoni
object Songs {
    val list: List<Song> = listOf(

    )

    //metodo per recuperare una canzone in base all'id
    fun songById(songId: String): Song? = list.find({song -> song.id == songId})
}
