package com.example.gamevault.model

/**
 * Avatares Cyberpunk seleccionables para el perfil Netrunner.
 */
enum class CyberAvatar(
    val id: Int,
    val alias: String,
    val roleLabel: String
) {
    NETRUNNER(0, "GHOST_RUNNER", "INFILTRACIÓN RED"),
    SAMURAI(1, "CHROME_RONIN", "COMBATE TÁCTICO"),
    HACKER(2, "ZERO_COOL", "ARQUITECTO DE DATOS"),
    SYNTH(3, "NEON_VALKYRIE", "OPERADOR NEURAL");

    companion object {
        fun fromId(id: Int): CyberAvatar = entries.firstOrNull { it.id == id } ?: NETRUNNER
    }
}

/**
 * Insignias / Logros desbloqueables en el ecosistema GameVault.
 */
data class CyberAchievement(
    val id: String,
    val title: String,
    val description: String,
    val xpReward: Int,
    val isUnlocked: Boolean,
    val progressText: String
)

/**
 * Resumen calculado del Perfil Netrunner del jugador.
 */
data class NetrunnerProfileSummary(
    val username: String = "USER_SAMUEL",
    val avatar: CyberAvatar = CyberAvatar.NETRUNNER,
    val totalXp: Int = 0,
    val level: Int = 1,
    val currentLevelXp: Int = 0,
    val nextLevelXp: Int = 250,
    val levelTitle: String = "ROOKIE TERMINAL",
    val duelHighScore: Int = 0,
    val duelGamesPlayed: Int = 0,
    val duelCorrectGuesses: Int = 0,
    val vaultTotalCount: Int = 0,
    val completedCount: Int = 0,
    val playingCount: Int = 0,
    val ratedCount: Int = 0,
    val favoriteGenre: String = "N/A",
    val achievements: List<CyberAchievement> = emptyList()
) {
    val levelProgress: Float
        get() = if (nextLevelXp > 0) {
            (currentLevelXp.toFloat() / nextLevelXp.toFloat()).coerceIn(0f, 1f)
        } else {
            1f
        }
}
