package com.example.gamevault.data

import android.content.Context
import android.content.SharedPreferences
import com.example.gamevault.model.BacklogEntry
import com.example.gamevault.model.BacklogStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Gestor de persistencia local para "The Backlog Matrix" (Mi Bóveda Evolucionada).
 * Almacena las entradas del backlog (estado, calificación personal) en JSON y mantiene
 * retrocompatibilidad automática con los IDs de favoritos previos.
 */
class VaultPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getBacklogEntries(): Map<Int, BacklogEntry> {
        val json = prefs.getString(KEY_BACKLOG_ENTRIES_JSON, null)
        if (!json.isNullOrBlank()) {
            return runCatching {
                val type = object : TypeToken<List<BacklogEntry>>() {}.type
                val list: List<BacklogEntry> = gson.fromJson(json, type) ?: emptyList()
                list.associateBy { it.gameId }
            }.getOrDefault(emptyMap())
        }

        // Migración transparente desde el conjunto de favoritos anterior
        val legacyRawSet = prefs.getStringSet(KEY_FAVORITE_IDS, emptySet()).orEmpty()
        val migrated = legacyRawSet.mapNotNull { it.toIntOrNull() }.associateWith { id ->
            BacklogEntry(
                gameId = id,
                status = BacklogStatus.PLAN_TO_PLAY,
                personalRating = 0
            )
        }
        if (migrated.isNotEmpty()) {
            saveEntries(migrated)
        }
        return migrated
    }

    fun getFavoriteIds(): Set<Int> = getBacklogEntries().keys

    fun toggleFavorite(gameId: Int): Map<Int, BacklogEntry> {
        val current = getBacklogEntries().toMutableMap()
        if (current.containsKey(gameId)) {
            current.remove(gameId)
        } else {
            current[gameId] = BacklogEntry(
                gameId = gameId,
                status = BacklogStatus.PLAN_TO_PLAY,
                personalRating = 0
            )
        }
        saveEntries(current)
        return current
    }

    fun setGameStatus(gameId: Int, status: BacklogStatus): Map<Int, BacklogEntry> {
        val current = getBacklogEntries().toMutableMap()
        val existing = current[gameId]
        current[gameId] = BacklogEntry(
            gameId = gameId,
            status = status,
            personalRating = existing?.personalRating ?: 0,
            updatedAt = System.currentTimeMillis()
        )
        saveEntries(current)
        return current
    }

    fun setPersonalRating(gameId: Int, rating: Int): Map<Int, BacklogEntry> {
        val safeRating = rating.coerceIn(0, 10)
        val current = getBacklogEntries().toMutableMap()
        val existing = current[gameId]
        current[gameId] = BacklogEntry(
            gameId = gameId,
            status = existing?.status ?: BacklogStatus.COMPLETED,
            personalRating = safeRating,
            updatedAt = System.currentTimeMillis()
        )
        saveEntries(current)
        return current
    }

    fun removeFromBacklog(gameId: Int): Map<Int, BacklogEntry> {
        val current = getBacklogEntries().toMutableMap()
        current.remove(gameId)
        saveEntries(current)
        return current
    }

    private fun saveEntries(entries: Map<Int, BacklogEntry>) {
        val json = gson.toJson(entries.values.toList())
        val stringIds = entries.keys.map { it.toString() }.toSet()
        prefs.edit()
            .putString(KEY_BACKLOG_ENTRIES_JSON, json)
            .putStringSet(KEY_FAVORITE_IDS, stringIds)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "gamevault_catalog_prefs"
        private const val KEY_FAVORITE_IDS = "favorite_game_ids"
        private const val KEY_BACKLOG_ENTRIES_JSON = "backlog_entries_json"
    }
}
