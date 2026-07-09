package it.unipi.di.sam.moveit.scoring

import kotlinx.serialization.Serializable

// per usare kotlinx.serialization, @Serializable va applicato anche a tutte
// le classi annidate (qui MovementSample e MovementData), altrimenti non
// vengono convertite in json.

@Serializable
data class SongMovesReference (
    val songid: String,
    val moves: List<MovementSample>
)
