package it.unipi.di.sam.moveit.scoring

import kotlinx.serialization.Serializable

// Sfruttando Kotlin Serializable deve sìesstre esteso a tutte le strutture sottostanti in quanto anchesse andrano
// trafromate in json
@Serializable
data class SongMovesReference (
    val songid: String,
    val moves: List<MovementSample>
)
