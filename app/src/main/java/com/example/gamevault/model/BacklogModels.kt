package com.example.gamevault.model

import com.google.gson.annotations.SerializedName

/**
 * Estados del juego dentro de "The Backlog Matrix".
 */
enum class BacklogStatus(
    val label: String,
    val badgeText: String
) {
    PLAN_TO_PLAY("EN COLA", "EN COLA"),
    PLAYING("JUGANDO", "EN CURSO"),
    COMPLETED("COMPLETADOS", "COMPLETADO"),
    DROPPED("ABANDONADOS", "ABANDONADO");
}

/**
 * Entrada individual del usuario en su Backlog Matrix.
 * @param gameId ID del juego en el catálogo.
 * @param status Estado actual del juego.
 * @param personalRating Calificación personal del usuario (0 = sin calificar, 1..10).
 * @param updatedAt Timestamp de la última actualización para ordenar por actividad reciente.
 */
data class BacklogEntry(
    @SerializedName("gameId") val gameId: Int,
    @SerializedName("status") val status: BacklogStatus = BacklogStatus.PLAN_TO_PLAY,
    @SerializedName("personalRating") val personalRating: Int = 0,
    @SerializedName("updatedAt") val updatedAt: Long = System.currentTimeMillis()
)
