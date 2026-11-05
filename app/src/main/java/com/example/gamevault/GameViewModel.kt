package com.example.gamevault

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamevault.data.CatalogRepository
import com.example.gamevault.data.DuelPreferences
import com.example.gamevault.data.VaultPreferences
import com.example.gamevault.model.BacklogEntry
import com.example.gamevault.model.BacklogStatus
import com.example.gamevault.model.CyberAchievement
import com.example.gamevault.model.CyberAvatar
import com.example.gamevault.model.NetrunnerProfileSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado inmutable de UI para el Catálogo General, "The Backlog Matrix" y "Netrunner Profile" (UDF con StateFlow).
 */
data class CatalogUiState(
    val games: List<Game> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val backlogEntries: Map<Int, BacklogEntry> = emptyMap(),
    val avatar: CyberAvatar = CyberAvatar.NETRUNNER,
    val username: String = "USER_SAMUEL",
    val bonusXp: Int = 0,
    val duelHighScore: Int = 0,
    val duelGamesPlayed: Int = 0,
    val duelCorrectGuesses: Int = 0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val favoriteIds: Set<Int>
        get() = backlogEntries.keys

    val filteredGames: List<Game>
        get() {
            val baseList = when (selectedCategory) {
                "All" -> if (searchQuery.isBlank() && games.size > 5) games.drop(5) else games
                "Favorites" -> games.filter { it.id in backlogEntries }
                else -> games.filter { it.genre.contains(selectedCategory, ignoreCase = true) }
            }

            if (searchQuery.isBlank()) return baseList

            val query = searchQuery.trim()
            return baseList.filter { game ->
                game.title.contains(query, ignoreCase = true) ||
                    game.genre.contains(query, ignoreCase = true) ||
                    game.developer.contains(query, ignoreCase = true) ||
                    game.platform.contains(query, ignoreCase = true)
            }
        }

    val backlogGames: List<Pair<Game, BacklogEntry>>
        get() = games.mapNotNull { game ->
            val entry = backlogEntries[game.id] ?: return@mapNotNull null
            game to entry
        }.sortedByDescending { it.second.updatedAt }

    /**
     * Calcula en tiempo real el Perfil Netrunner completo (XP, Nivel, Título, Logros y Estadísticas).
     */
    val profileSummary: NetrunnerProfileSummary
        get() {
            val vaultTotal = backlogEntries.size
            val completedCount = backlogEntries.values.count { it.status == BacklogStatus.COMPLETED }
            val playingCount = backlogEntries.values.count { it.status == BacklogStatus.PLAYING }
            val ratedCount = backlogEntries.values.count { it.personalRating > 0 }

            val topGenre = backlogGames
                .groupingBy { it.first.genre.trim() }
                .eachCount()
                .maxByOrNull { it.value }
                ?.key
                ?.uppercase()
                ?: "SIN DATOS"

            val achievements = listOf(
                CyberAchievement(
                    id = "first_vault",
                    title = "COLECCIONISTA DIGITAL",
                    description = "Guarda al menos 3 juegos en tu Backlog Matrix.",
                    xpReward = 100,
                    isUnlocked = vaultTotal >= 3,
                    progressText = "${vaultTotal.coerceAtMost(3)}/3"
                ),
                CyberAchievement(
                    id = "finisher",
                    title = "CAZADOR DE CRÉDITOS",
                    description = "Marca al menos 1 juego como COMPLETADO.",
                    xpReward = 150,
                    isUnlocked = completedCount >= 1,
                    progressText = "${completedCount.coerceAtMost(1)}/1"
                ),
                CyberAchievement(
                    id = "critic",
                    title = "VOZ DEL CIBERESPACIO",
                    description = "Califica al menos 3 juegos con tu nota personal.",
                    xpReward = 120,
                    isUnlocked = ratedCount >= 3,
                    progressText = "${ratedCount.coerceAtMost(3)}/3"
                ),
                CyberAchievement(
                    id = "duel_streak_5",
                    title = "ORÁCULO METACRITIC",
                    description = "Alcanza una racha de 5 o más en Metacritic Duel.",
                    xpReward = 200,
                    isUnlocked = duelHighScore >= 5,
                    progressText = "${duelHighScore.coerceAtMost(5)}/5"
                ),
                CyberAchievement(
                    id = "duel_veteran",
                    title = "GLADIADOR DEL DUELO",
                    description = "Acumula 15 predicciones correctas en Metacritic Duel.",
                    xpReward = 250,
                    isUnlocked = duelCorrectGuesses >= 15,
                    progressText = "${duelCorrectGuesses.coerceAtMost(15)}/15"
                ),
                CyberAchievement(
                    id = "vault_architect",
                    title = "ARQUITECTO DE LA BÓVEDA",
                    description = "Almacena 10 juegos en tu Backlog Matrix.",
                    xpReward = 300,
                    isUnlocked = vaultTotal >= 10,
                    progressText = "${vaultTotal.coerceAtMost(10)}/10"
                )
            )

            val achievementsXp = achievements.filter { it.isUnlocked }.sumOf { it.xpReward }
            val backlogActivityXp =
                (vaultTotal * 25) + (playingCount * 20) + (completedCount * 80) + (ratedCount * 15)
            val duelActivityXp = (duelGamesPlayed * 15) + bonusXp

            val totalXp = backlogActivityXp + duelActivityXp + achievementsXp
            val xpPerLevel = 250
            val level = (totalXp / xpPerLevel) + 1
            val currentLevelXp = totalXp % xpPerLevel

            val levelTitle = when {
                level >= 12 -> "CYBER GOD // S-RANK"
                level >= 8 -> "ARCH-NETRUNNER"
                level >= 5 -> "OVERCLOCK SPECIALIST"
                level >= 3 -> "STREET MERCENARY"
                else -> "ROOKIE TERMINAL"
            }

            return NetrunnerProfileSummary(
                username = username,
                avatar = avatar,
                totalXp = totalXp,
                level = level,
                currentLevelXp = currentLevelXp,
                nextLevelXp = xpPerLevel,
                levelTitle = levelTitle,
                duelHighScore = duelHighScore,
                duelGamesPlayed = duelGamesPlayed,
                duelCorrectGuesses = duelCorrectGuesses,
                vaultTotalCount = vaultTotal,
                completedCount = completedCount,
                playingCount = playingCount,
                ratedCount = ratedCount,
                favoriteGenre = topGenre,
                achievements = achievements
            )
        }
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CatalogRepository = CatalogRepository()
    private val vaultPreferences: VaultPreferences = VaultPreferences(application)
    private val duelPreferences: DuelPreferences = DuelPreferences(application)

    private val _uiState = MutableStateFlow(
        CatalogUiState(
            backlogEntries = vaultPreferences.getBacklogEntries(),
            avatar = CyberAvatar.fromId(duelPreferences.getSelectedAvatarId()),
            username = duelPreferences.getUsername(),
            bonusXp = duelPreferences.getBonusXp(),
            duelHighScore = duelPreferences.getHighScore(),
            duelGamesPlayed = duelPreferences.getGamesPlayed(),
            duelCorrectGuesses = duelPreferences.getTotalCorrectGuesses()
        )
    )
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    init {
        fetchGames()
    }

    fun refreshProfileStats() {
        _uiState.update {
            it.copy(
                backlogEntries = vaultPreferences.getBacklogEntries(),
                avatar = CyberAvatar.fromId(duelPreferences.getSelectedAvatarId()),
                username = duelPreferences.getUsername(),
                bonusXp = duelPreferences.getBonusXp(),
                duelHighScore = duelPreferences.getHighScore(),
                duelGamesPlayed = duelPreferences.getGamesPlayed(),
                duelCorrectGuesses = duelPreferences.getTotalCorrectGuesses()
            )
        }
    }

    fun fetchGames() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    backlogEntries = vaultPreferences.getBacklogEntries()
                )
            }

            repository.fetchCatalogGames()
                .onSuccess { fetchedGames ->
                    _uiState.update {
                        it.copy(
                            games = fetchedGames,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    error.printStackTrace()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "No se pudo conectar con la base de datos. Verifica tu conexión."
                        )
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCategorySelect(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun toggleFavorite(gameId: Int) {
        val updated = vaultPreferences.toggleFavorite(gameId)
        _uiState.update { it.copy(backlogEntries = updated) }
    }

    fun updateBacklogStatus(gameId: Int, status: BacklogStatus) {
        val updated = vaultPreferences.setGameStatus(gameId, status)
        _uiState.update { it.copy(backlogEntries = updated) }
    }

    fun updatePersonalRating(gameId: Int, rating: Int) {
        val updated = vaultPreferences.setPersonalRating(gameId, rating)
        _uiState.update { it.copy(backlogEntries = updated) }
    }

    fun removeFromBacklog(gameId: Int) {
        val updated = vaultPreferences.removeFromBacklog(gameId)
        _uiState.update { it.copy(backlogEntries = updated) }
    }

    fun selectAvatar(avatar: CyberAvatar) {
        duelPreferences.setSelectedAvatarId(avatar.id)
        _uiState.update { it.copy(avatar = avatar) }
    }
}
