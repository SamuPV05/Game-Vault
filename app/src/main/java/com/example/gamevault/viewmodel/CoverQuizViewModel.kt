package com.example.gamevault.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamevault.data.DuelPreferences
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
 * Estado inmutable para el minijuego "Mystery Cover" (Adivina por la Portada Desenfocada).
 */
data class CoverQuizUiState(
    val targetGame: GameDuelItem? = null,
    val options: List<GameDuelItem> = emptyList(),
    val selectedOptionId: Int? = null,
    val isRevealed: Boolean = false,
    val blurLevel: Int = 3, // 3 = Máximo desenfoque (x3 pts), 2 = Medio (x2 pts), 1 = Bajo (x1 pt), 0 = Nítido
    val score: Int = 0,
    val highScore: Int = 0,
    val lives: Int = 3,
    val isGameOver: Boolean = false,
    val isLoading: Boolean = true
)

class CoverQuizViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DuelRepository = DuelRepository()
    private val duelPreferences: DuelPreferences = DuelPreferences(application)
    private val prefs = application.applicationContext.getSharedPreferences(
        "gamevault_cover_quiz_prefs",
        Context.MODE_PRIVATE
    )

    private val _uiState = MutableStateFlow(
        CoverQuizUiState(highScore = prefs.getInt(KEY_COVER_HIGH_SCORE, 0))
    )
    val uiState: StateFlow<CoverQuizUiState> = _uiState.asStateFlow()

    private var allGames: List<GameDuelItem> = emptyList()
    private var nextRoundJob: Job? = null

    init {
        startNewGame()
    }

    fun startNewGame() {
        nextRoundJob?.cancel()
        viewModelScope.launch {
            val savedHigh = maxOf(_uiState.value.highScore, prefs.getInt(KEY_COVER_HIGH_SCORE, 0))
            _uiState.update {
                it.copy(
                    isLoading = true,
                    score = 0,
                    highScore = savedHigh,
                    lives = 3,
                    isGameOver = false,
                    isRevealed = false,
                    selectedOptionId = null
                )
            }

            if (allGames.size < 4) {
                allGames = repository.fetchDuelGames()
                    .filter { !it.backgroundImage.isNullOrBlank() }
            }

            prepareRound()
        }
    }

    private fun prepareRound() {
        if (allGames.size < 4) return

        val currentTargetId = _uiState.value.targetGame?.id
        val pool = allGames.filter { it.id != currentTargetId }.ifEmpty { allGames }
        val target = pool.random()
        val distractors = allGames
            .filter { it.id != target.id }
            .shuffled()
            .take(3)
        val roundOptions = (distractors + target).shuffled()

        _uiState.update {
            it.copy(
                targetGame = target,
                options = roundOptions,
                selectedOptionId = null,
                isRevealed = false,
                blurLevel = 3,
                isLoading = false
            )
        }
    }

    /**
     * Reduce el nivel de desenfoque ("ENFOCAR LENTE") a cambio de reducir los puntos posibles de la ronda.
     */
    fun enhanceClarity() {
        val state = _uiState.value
        if (state.isRevealed || state.isGameOver || state.blurLevel <= 1) return
        _uiState.update { it.copy(blurLevel = (it.blurLevel - 1).coerceAtLeast(1)) }
    }

    /**
     * Evalúa la opción elegida por el usuario entre los 4 títulos posibles.
     */
    fun onSelectOption(selectedGameId: Int) {
        val state = _uiState.value
        if (state.isLoading || state.isRevealed || state.isGameOver) return
        val target = state.targetGame ?: return

        val isCorrect = selectedGameId == target.id
        val earnedPoints = if (isCorrect) state.blurLevel else 0
        val newScore = state.score + earnedPoints
        val newHighScore = maxOf(state.highScore, newScore)
        val remainingLives = if (isCorrect) state.lives else (state.lives - 1).coerceAtLeast(0)

        if (isCorrect) {
            duelPreferences.addBonusXp(earnedPoints * 15)
            if (newHighScore > prefs.getInt(KEY_COVER_HIGH_SCORE, 0)) {
                prefs.edit().putInt(KEY_COVER_HIGH_SCORE, newHighScore).apply()
            }
        }

        _uiState.update {
            it.copy(
                selectedOptionId = selectedGameId,
                isRevealed = true,
                blurLevel = 0,
                score = newScore,
                highScore = newHighScore,
                lives = remainingLives
            )
        }

        nextRoundJob?.cancel()
        nextRoundJob = viewModelScope.launch {
            delay(1300L)
            if (remainingLives <= 0) {
                duelPreferences.incrementGamesPlayed()
                _uiState.update { it.copy(isGameOver = true) }
            } else {
                prepareRound()
            }
        }
    }

    companion object {
        private const val KEY_COVER_HIGH_SCORE = "cover_quiz_high_score"
    }
}
