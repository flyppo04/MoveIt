package it.unipi.di.sam.moveit.scoring

import android.content.Context
import it.unipi.di.sam.moveit.R
import kotlinx.serialization.json.Json

fun songMovesReferenceLoader(context: Context, songId: String ) :SongMovesReference? {

    val id = when(songId){
        //"one_more_time" -> R.raw.one_more_time
        else -> null
    } ?: return null

    val rawResource = context.resources.openRawResource(id)
    val jsonResource = rawResource.bufferedReader().readText()
    val finalObject: SongMovesReference = Json.decodeFromString<SongMovesReference>(jsonResource)
    return finalObject
}