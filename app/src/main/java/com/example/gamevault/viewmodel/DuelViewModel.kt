package com.example.gamevault.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamevault.data.DuelRepository
import com.example.gamevault.model.GameDuelItem
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
    val isRevealed: Boolean = false,
    val isGameOver: Boolean = false,
    val isLoading: Boolean = true
)

class DuelViewModel(
    private val repository: DuelRepository = DuelRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DuelUiState())
    val uiState: StateFlow<DuelUiState> = _uiState.asStateFlow()

    private var allGames: List<GameDuelItem> = emptyList()
    private val deck: ArrayDeque<GameDuelItem> = ArrayDeque()
    private var transitionJob: Job? = null

    init {
        loadGames()
    }

    /**
     * Inicia o reinicia la partida cargando y barajando los juegos con Metascore válido.
     */
    fun loadGames() {
        transitionJob?.cancel()
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isLoading = true,
                    isGameOver = false,
                    isRevealed = false,
                    score = 0
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

        // 1. Revelar el Metascore del retador
        _uiState.update { it.copy(isRevealed = true) }

        val isCorrect = if (guessHigher) {
            challenger.metacritic >= current.metacritic
        } else {
            challenger.metacritic <= current.metacritic
        }

        // 2. Esperar 1.2 segundos antes de avanzar o terminar el juego
        transitionJob?.cancel()
        transitionJob = viewModelScope.launch {
            delay(1200L)

            if (isCorrect) {
                val nextChallenger = drawNextGame(excludeId = challenger.id)
                _uiState.update { currentState ->
                    val newScore = currentState.score + 1
                    val newHighScore = maxOf(currentState.highScore, newScore)
                    currentState.copy(
                        currentGame = challenger,
                        challengerGame = nextChallenger,
                        score = newScore,
                        highScore = newHighScore,
                        isRevealed = false
                    )
                }
            } else {
                _uiState.update { currentState ->
                    currentState.copy(
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
