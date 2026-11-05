package com.example.gamevault.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamevault.data.DuelPreferences
import com.example.gamevault.data.DuelRepository
import com.example.gamevault.model.CyberRank
import com.example.gamevault.model.GameDuelItem
import com.example.gamevault.model.GuessResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado inmutable de UI para el modo "Metacritic Duel" (UDF).
 */
data class DuelUiState(
    val currentGame: GameDuelItem? = null,
    val challengerGame: GameDuelItem? = null,
    val score: Int = 0,
    val highScore: Int = 0,
    val gamesPlayed: Int = 0,
    val isNewRecord: Boolean = false,
    val lastGuessResult: GuessResult = GuessResult.NONE,
    val isRevealed: Boolean = false,
    val isGameOver: Boolean = false,
    val isLoading: Boolean = true
) {
    val rank: CyberRank
        get() = CyberRank.fromScore(score)
}

class DuelViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DuelRepository = DuelRepository()
    private val preferences: DuelPreferences = DuelPreferences(application)

    private val _uiState = MutableStateFlow(
        DuelUiState(
            highScore = preferences.getHighScore(),
            gamesPlayed = preferences.getGamesPlayed()
        )
    )
    val uiState: StateFlow<DuelUiState> = _uiState.asStateFlow()

    private var allGames: List<GameDuelItem> = emptyList()
    private val deck: ArrayDeque<GameDuelItem> = ArrayDeque()
    private var transitionJob: Job? = null

    init {
        loadGames()
    }

    /**
     * Inicia o reinicia la partida cargando y barajando los juegos con Metascore válido,
     * preservando el récord histórico persistido en el dispositivo.
     */
    fun loadGames() {
        transitionJob?.cancel()
        viewModelScope.launch {
            val savedHighScore = maxOf(_uiState.value.highScore, preferences.getHighScore())
            val totalPlayed = preferences.getGamesPlayed()

            _uiState.update { state ->
                state.copy(
                    isLoading = true,
                    isGameOver = false,
                    isRevealed = false,
                    score = 0,
                    highScore = savedHighScore,
                    gamesPlayed = totalPlayed,
                    isNewRecord = false,
                    lastGuessResult = GuessResult.NONE
                )
            }

            if (allGames.size < 2) {
                allGames = repository.fetchDuelGames()
                    .filter { it.metacritic > 0 }
            }

            refillDeck(excludeId = null)

            val firstGame = drawNextGame(excludeId = null)
            val secondGame = drawNextGame(excludeId = firstGame?.id)

            _uiState.update { state ->
                state.copy(
                    currentGame = firstGame,
                    challengerGame = secondGame,
                    score = 0,
                    highScore = savedHighScore,
                    gamesPlayed = totalPlayed,
                    isNewRecord = false,
                    lastGuessResult = GuessResult.NONE,
                    isRevealed = false,
                    isGameOver = false,
                    isLoading = false
                )
            }
        }
    }

    /**
     * Evalúa la predicción del usuario:
     * @param guessHigher true si presionó "MÁS ALTO ▲", false si presionó "MÁS BAJO ▼"
     */
    fun onGuess(guessHigher: Boolean) {
        val state = _uiState.value
        if (state.isLoading || state.isRevealed || state.isGameOver) return

        val current = state.currentGame ?: return
        val challenger = state.challengerGame ?: return

        val isCorrect = if (guessHigher) {
            challenger.metacritic >= current.metacritic
        } else {
            challenger.metacritic <= current.metacritic
        }

        // 1. Revelar el Metascore del retador junto al resultado visual
        _uiState.update {
            it.copy(
                isRevealed = true,
                lastGuessResult = if (isCorrect) GuessResult.CORRECT else GuessResult.WRONG
            )
        }

        // 2. Esperar 1.2 segundos antes de avanzar o terminar el juego
        transitionJob?.cancel()
        transitionJob = viewModelScope.launch {
            delay(1200L)

            if (isCorrect) {
                val nextChallenger = drawNextGame(excludeId = challenger.id)
                val newScore = _uiState.value.score + 1
                val previousBest = preferences.getHighScore()
                val beatRecord = newScore > previousBest
                val newHighScore = maxOf(_uiState.value.highScore, newScore)

                preferences.incrementCorrectGuesses()
                preferences.saveHighScore(newHighScore)

                _uiState.update { currentState ->
                    currentState.copy(
                        currentGame = challenger,
                        challengerGame = nextChallenger,
                        score = newScore,
                        highScore = newHighScore,
                        isNewRecord = currentState.isNewRecord || beatRecord,
                        lastGuessResult = GuessResult.NONE,
                        isRevealed = false
                    )
                }
            } else {
                val updatedGamesPlayed = preferences.incrementGamesPlayed()
                _uiState.update { currentState ->
                    currentState.copy(
                        gamesPlayed = updatedGamesPlayed,
                        isGameOver = true
                    )
                }
            }
        }
    }

    private fun refillDeck(excludeId: Int?) {
        deck.clear()
        val shuffled = allGames
            .filter { it.id != excludeId }
            .shuffled()
        deck.addAll(shuffled)
    }

    private fun drawNextGame(excludeId: Int?): GameDuelItem? {
        if (allGames.isEmpty()) return null
        if (deck.isEmpty()) {
            refillDeck(excludeId)
        }
        var candidate = deck.removeFirstOrNull()
        if (candidate?.id == excludeId && deck.isNotEmpty()) {
            candidate = deck.removeFirstOrNull()
        }
        return candidate ?: allGames.firstOrNull { it.id != excludeId } ?: allGames.firstOrNull()
    }
}
