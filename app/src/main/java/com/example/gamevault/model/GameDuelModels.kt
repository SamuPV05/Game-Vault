package com.example.gamevault.model

import com.google.gson.annotations.SerializedName

/**
 * Respuesta paginada del endpoint /api/games de RAWG.
 */
data class RawgGamesResponse(
    @SerializedName("count") val count: Int? = null,
    @SerializedName("next") val next: String? = null,
    @SerializedName("previous") val previous: String? = null,
    @SerializedName("results") val results: List<RawgGameDto>? = emptyList()
)

/**
 * DTO de red para cada juego retornado por RAWG.
 * Todos los campos son nulables para garantizar cero crashes ante respuestas parciales.
 */
data class RawgGameDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("background_image") val backgroundImage: String? = null,
    @SerializedName("metacritic") val metacritic: Int? = null
)

/**
 * Modelo de dominio requerido para el modo "Metacritic Duel".
 */
data class GameDuelItem(
    val id: Int,
    val name: String,
    val backgroundImage: String?,
    val metacritic: Int
)

/**
 * Mapea un DTO de RAWG al modelo de dominio solo si cumple las reglas de validación:
 * - id y name válidos
 * - metacritic != null && metacritic > 0
 */
fun RawgGameDto.toDomainOrNull(): GameDuelItem? {
    val safeId = id ?: return null
    val safeName = name?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    val safeMetacritic = metacritic?.takeIf { it > 0 } ?: return null

    return GameDuelItem(
        id = safeId,
        name = safeName,
        backgroundImage = backgroundImage,
        metacritic = safeMetacritic
    )
}
