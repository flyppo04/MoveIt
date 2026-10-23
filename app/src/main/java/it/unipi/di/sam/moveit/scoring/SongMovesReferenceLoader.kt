package it.unipi.di.sam.moveit.scoring

import android.content.Context
import android.util.Log
import it.unipi.di.sam.moveit.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.IOException

suspend fun songMovesReferenceLoader(context: Context, songId: String ) :SongMovesReference? =
    withContext(Dispatchers.IO) {

    // ogni songId deve avere una entry qui E in Songs.list (data/Song.kt):
    // le due liste non sono collegate a livello di codice, vanno tenute
    // sincronizzate a mano quando si aggiunge una canzone.

    val id = when(songId){
        "mueve_la_colita" -> R.raw.mueve_la_colita_moves
        else -> null
    } ?: return@withContext null

    try {
        context.resources.openRawResource(id).bufferedReader().use { reader ->
            Json.decodeFromString<SongMovesReference>(reader.readText())
        }
    } catch (e: Exception) {
        if (e is SerializationException || e is IOException) {
            Log.e("MoveIt", "Impossibile caricare la coreografia $songId", e)
            null
        } else {
            throw e
        }
    }
}