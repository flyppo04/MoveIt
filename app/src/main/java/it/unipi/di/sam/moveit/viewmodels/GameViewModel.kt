package it.unipi.di.sam.moveit.viewmodels

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import it.unipi.di.sam.moveit.data.AppDatabase
import it.unipi.di.sam.moveit.data.Game
import it.unipi.di.sam.moveit.data.GameRepository
import it.unipi.di.sam.moveit.data.Songs.songById
import it.unipi.di.sam.moveit.scoring.MovesRecorder
import it.unipi.di.sam.moveit.scoring.ScoringEngine
import it.unipi.di.sam.moveit.scoring.SongMovesReference
import it.unipi.di.sam.moveit.sensor.SensorsMenager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import it.unipi.di.sam.moveit.scoring.songMovesReferenceLoader
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


sealed class GameUiState

object Loading: GameUiState()
data class Error (val error: String) : GameUiState()
data class Countdown (val secondsLeft: Int) : GameUiState()
data class Playing (val secondPassed: Long, val duration: Int) : GameUiState()
data class Finished (val score: Int) : GameUiState()


class GameViewModel(val appContext: Application, val songId: String): AndroidViewModel(appContext) {

    private val _uiState = MutableStateFlow<GameUiState>(Loading)
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val sensorManager = SensorsMenager(appContext)
    private val movesRecorder = MovesRecorder(sensorManager)

    private val gameRepository = GameRepository(AppDatabase.getInstance(appContext).gameDAO())
    private var importedReference: SongMovesReference?  = null

    private var song = songById(songId)

    private var player: MediaPlayer? = null
    private var currentTime = 0L

    private var gameJob: Job? = null   // serve import kotlinx.coroutines.Job

    suspend fun loadReference() {
        val reference = songMovesReferenceLoader(appContext, songId)
        if (reference == null) {
            _uiState.value = Error("Impossibile caricare la coreografia di riferimento per questa canzone")
        } else {
            importedReference = reference
        }
    }

    init {
        gameJob = viewModelScope.launch {

            if(song == null) {_uiState.value = Error("Song not found"); return@launch}
            player = MediaPlayer.create(appContext, song!!.audioResId)
            if (player == null) { _uiState.value = Error("Impossibile caricare l'audio"); return@launch }

            loadReference()

            if (importedReference == null) {
                return@launch
            }

            sensorManager.startListening()

            for (i in 3 downTo 1) { _uiState.value = Countdown(i); delay(1000) }

            player!!.start()

            _uiState.value = Playing(0, player!!.duration)

            movesRecorder.startRecording()

            while (currentTime < player!!.duration) {

                currentTime += 1000
                delay(1000)
                _uiState.value = Playing(currentTime, player!!.duration)

            }

            val moves = movesRecorder.stopRecording()

            sensorManager.endListening()

            val score = ScoringEngine.evaluateRecording(importedReference!!.moves, moves)
            val finalScore = ScoringEngine.scaledScore(score)
            gameRepository.salva(
                Game(
                    songId = songId,
                    songTitle = song?.title ?: songId,
                    score = finalScore,
                    duration = player!!.duration,
                    timestamp = System.currentTimeMillis()
                )
            )
            _uiState.value = Finished(finalScore)

        }
    }

    //gamejob cancelato pruima di arrivare a questa riga
    override fun onCleared(){
        super.onCleared()
        sensorManager.endListening()
        player?.release()
    }

    // uscita esplicita a partita in corso a differenza di onCleared, qui va cancellato anche il job

    fun onEndGame(){
        gameJob?.cancel()
        sensorManager.endListening()
        player?.release()
    }

}
