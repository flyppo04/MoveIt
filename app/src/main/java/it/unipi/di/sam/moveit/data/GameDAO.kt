package it.unipi.di.sam.moveit.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

//ciò che dice a room quali sono i metodi per leggere e scrivere sul database
//si occupa di tradurre da kotlin a SQL e viceversa
@Dao
interface GameDAO {

    // INSERT: Room genera automaticamente la query SQL
    // "suspend" perché è un'operazione di I/O — non può girare sul main thread
    @Insert
    suspend fun inserisci(partita: Game): Long

    // SELECT: ritorna un Flow — Room aggiorna automaticamente la lista
    // ogni volta che la tabella cambia
    @Query("SELECT * FROM games ORDER BY timestamp DESC")
    fun tuttiGame(): Flow<List<Game>>

    // DELETE di una singola partita
    @Delete
    suspend fun elimina(partita: Game)

    // DELETE di tutte le partite
    @Query("DELETE FROM games")
    suspend fun eliminaTutti()
}
