package it.unipi.di.sam.moveit.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")

data class Game(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Chiave primaria, mettendo 0 la chiave viene generata automaticamente

    @ColumnInfo(name = "song_id") // definisce nome della colonna
    val songId: String,

    @ColumnInfo(name = "song_title")
    val songTitle: String,

    val score: Int,
    val duration: Int,
    val timestamp: Long  // in millesecondi
)