package it.unipi.di.sam.moveit.data

import kotlinx.coroutines.flow.Flow

// repository si frappone fra viewmodel e dao in funzione di rendere i viewmodel
// indipendenti da sistema di persistenza
class GameRepository(private val dao: GameDAO) {

    fun AllGame(): Flow<List<Game>> = dao.tuttiGame()

    suspend fun salva(game: Game) = dao.inserisci(game)

    suspend fun elimina(game: Game) = dao.elimina(game)

    suspend fun eliminaTutte() = dao.eliminaTutti()
}