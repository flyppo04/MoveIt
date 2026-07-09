package it.unipi.di.sam.moveit.scoring

import android.content.Context
import it.unipi.di.sam.moveit.R
import kotlinx.serialization.json.Json

fun songMovesReferenceLoader(context: Context, songId: String ) :SongMovesReference? {

    // ogni songId deve avere una entry qui E in Songs.list (data/Song.kt):
    // le due liste non sono collegate a livello di codice, vanno tenute
    // sincronizzate a mano quando si aggiunge una canzone.

    val id = when(songId){
        "mueve_la_colita" -> R.raw.mueve_la_colita_moves
        else -> null
    } ?: return null

    val rawResource = context.resources.openRawResource(id)
    val jsonResource = rawResource.bufferedReader().readText()
    val finalObject: SongMovesReference = Json.decodeFromString<SongMovesReference>(jsonResource)
    return finalObject
}