package com.example.gamevault.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamevault.data.DuelPreferences
import com.example.gamevault.data.LootRepository
import com.example.gamevault.model.LootGiveaway
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LootUiState(
    val giveaways: List<LootGiveaway> = emptyList(),
    val selectedPlatform: String = "TODOS",
    val claimedIds: Set<Int> = emptySet(),
    val isLoading: Boolean = true
) {
    val filteredGiveaways: List<LootGiveaway>
        get() {
            if (selectedPlatform == "TODOS") return giveaways
            return giveaways.filter { giveaway ->
                giveaway.platforms.contains(selectedPlatform, ignoreCase = true) ||
                    giveaway.type.contains(selectedPlatform, ignoreCase = true)
            }
        }

    /**
     * Calcula el valor total en dólares ($ USD) ahorrado en las ofertas disponibles.
     */
    val totalAvailableValueUsd: Double
        get() = giveaways.sumOf { item ->
            item.worth
                .replace("$", "")
                .trim()
                .toDoubleOrNull() ?: 0.0
        }
}

class LootViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LootRepository = LootRepository()
    private val duelPreferences: DuelPreferences = DuelPreferences(application)
    private val prefs = application.applicationContext.getSharedPreferences(
        "gamevault_loot_prefs",
        Context.MODE_PRIVATE
    )

    private val _uiState = MutableStateFlow(
        LootUiState(claimedIds = loadClaimedIds())
    )
    val uiState: StateFlow<LootUiState> = _uiState.asStateFlow()

    init {
        loadGiveaways()
    }

    fun loadGiveaways() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, claimedIds = loadClaimedIds()) }
            val items = repository.fetchGiveaways()
            _uiState.update {
                it.copy(
                    giveaways = items,
                    isLoading = false
                )
            }
        }
    }

    fun selectPlatform(platform: String) {
        _uiState.update { it.copy(selectedPlatform = platform) }
    }

    /**
     * Registra un loot como reclamado y otorga +30 XP al perfil Netrunner la primera vez.
     */
    fun markAsClaimed(giveawayId: Int) {
        val current = loadClaimedIds().toMutableSet()
        if (current.add(giveawayId)) {
            prefs.edit()
                .putStringSet(KEY_CLAIMED_LOOT_IDS, current.map { it.toString() }.toSet())
                .apply()
            duelPreferences.addBonusXp(XP_PER_LOOT_CLAIM)
            _uiState.update { it.copy(claimedIds = current) }
        }
    }

    private fun loadClaimedIds(): Set<Int> {
        return prefs.getStringSet(KEY_CLAIMED_LOOT_IDS, emptySet())
            .orEmpty()
            .mapNotNull { it.toIntOrNull() }
            .toSet()
    }

    companion object {
        private const val KEY_CLAIMED_LOOT_IDS = "claimed_loot_ids"
        const val XP_PER_LOOT_CLAIM = 30
    }
}
