package com.example.gamevault.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestor de persistencia local para récords, experiencia (XP) y perfil del jugador.
 * Utiliza SharedPreferences nativo de Android para garantizar cero conflictos de dependencias.
 */
class DuelPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getHighScore(): Int = prefs.getInt(KEY_HIGH_SCORE, 0)

    fun saveHighScore(score: Int): Boolean {
        val currentBest = getHighScore()
        return if (score > currentBest) {
            prefs.edit().putInt(KEY_HIGH_SCORE, score).apply()
            true
        } else {
            false
        }
    }

    fun getGamesPlayed(): Int = prefs.getInt(KEY_GAMES_PLAYED, 0)

    fun incrementGamesPlayed(): Int {
        val updated = getGamesPlayed() + 1
        prefs.edit().putInt(KEY_GAMES_PLAYED, updated).apply()
        return updated
    }

    fun getTotalCorrectGuesses(): Int = prefs.getInt(KEY_TOTAL_CORRECT_GUESSES, 0)

    fun incrementCorrectGuesses(): Int {
        val updated = getTotalCorrectGuesses() + 1
        prefs.edit().putInt(KEY_TOTAL_CORRECT_GUESSES, updated).apply()
        addBonusXp(XP_PER_CORRECT_GUESS)
        return updated
    }

    fun getBonusXp(): Int = prefs.getInt(KEY_BONUS_XP, 0)

    fun addBonusXp(amount: Int): Int {
        if (amount <= 0) return getBonusXp()
        val updated = getBonusXp() + amount
        prefs.edit().putInt(KEY_BONUS_XP, updated).apply()
        return updated
    }

    fun getSelectedAvatarId(): Int = prefs.getInt(KEY_AVATAR_ID, 0)

    fun setSelectedAvatarId(avatarId: Int) {
        prefs.edit().putInt(KEY_AVATAR_ID, avatarId).apply()
    }

    fun getUsername(): String = prefs.getString(KEY_USERNAME, "USER_SAMUEL") ?: "USER_SAMUEL"

    fun setUsername(name: String) {
        val clean = name.trim().uppercase().takeIf { it.isNotEmpty() } ?: "USER_SAMUEL"
        prefs.edit().putString(KEY_USERNAME, clean).apply()
    }

    companion object {
        private const val PREFS_NAME = "gamevault_duel_prefs"
        private const val KEY_HIGH_SCORE = "duel_high_score"
        private const val KEY_GAMES_PLAYED = "duel_games_played"
        private const val KEY_TOTAL_CORRECT_GUESSES = "duel_total_correct"
        private const val KEY_BONUS_XP = "netrunner_bonus_xp"
        private const val KEY_AVATAR_ID = "netrunner_avatar_id"
        private const val KEY_USERNAME = "netrunner_username"

        const val XP_PER_CORRECT_GUESS = 40
    }
}
