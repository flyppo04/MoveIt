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
import it.unipi.di.sam.moveit.sensor.SensorsMenager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import it.unipi.di.sam.moveit.scoring.songMovesReferenceLoader
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
    private var player: MediaPlayer? = null
    private var gameJob: Job? = null   // serve import kotlinx.coroutines.Job


    init {
        gameJob = viewModelScope.launch {

            val song = songById(songId)
            if (song == null) { _uiState.value = Error("Canzone non trovata"); return@launch }

            player = MediaPlayer.create(appContext, song.audioResId)
            val p = player;
            if (p == null) { _uiState.value = Error("Impossibile caricare l'audio"); return@launch }


            val reference = songMovesReferenceLoader(appContext, songId)
            if (reference == null) { _uiState.value = Error("Impossibile recuperare riferimento canzone"); return@launch }

            sensorManager.startListening()

            for (i in 3 downTo 1) { _uiState.value = Countdown(i); delay(1000) }

            val finished = CompletableDeferred<Unit>()
            p.setOnCompletionListener { finished.complete(Unit) }

            p.start()

            movesRecorder.startRecording()


            val progressJob = launch {
                while (true) {
                    _uiState.value = Playing(p.currentPosition.toLong(), p.duration)
                    delay(250)
                }
            }

            finished.await()
            progressJob.cancel()

            val moves = movesRecorder.stopRecording()

            sensorManager.endListening()

            val score = withContext(Dispatchers.Default) {
                ScoringEngine.evaluateRecording(reference.moves, moves)
            }


            val finalScore = ScoringEngine.scaledScore(score)
            gameRepository.salva(
                Game(
                    songId = songId,
                    songTitle = song.title,
                    score = finalScore,
                    duration = p.duration,
                    timestamp = System.currentTimeMillis()
                )
            )
            _uiState.value = Finished(finalScore)

        }
    }

    fun onAppStopped() {
        val state = _uiState.value
        if (state is Finished || state is Error) return

        onEndGame()
        _uiState.value = Error("Partita interrotta: l'app è andata in background")
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
