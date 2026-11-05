package com.example.gamevault.model

import com.google.gson.annotations.SerializedName

/**
 * DTO de red para la API pública de GamerPower (https://www.gamerpower.com/api/giveaways).
 * Todos los campos son nulables para garantizar cero crashes.
 */
data class GiveawayDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("worth") val worth: String? = null,
    @SerializedName("thumbnail") val thumbnail: String? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("instructions") val instructions: String? = null,
    @SerializedName("open_giveaway_url") val openGiveawayUrl: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("platforms") val platforms: String? = null,
    @SerializedName("end_date") val endDate: String? = null,
    @SerializedName("status") val status: String? = null
)

/**
 * Modelo de dominio para una promoción / juego gratis en "Loot Radar".
 */
data class LootGiveaway(
    val id: Int,
    val title: String,
    val worth: String,
    val imageUrl: String?,
    val description: String,
    val instructions: String,
    val claimUrl: String,
    val type: String,
    val platforms: String,
    val endDate: String
)

fun GiveawayDto.toDomainOrNull(): LootGiveaway? {
    val safeId = id ?: return null
    val safeTitle = title?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    val safeClaimUrl = openGiveawayUrl?.trim()?.takeIf { it.isNotEmpty() } ?: return null

    val cleanWorth = worth?.trim()?.takeIf { it.isNotEmpty() && !it.equals("N/A", ignoreCase = true) }
        ?: "GRATIS"
    val cleanEndDate = endDate?.trim()?.takeIf { it.isNotEmpty() && !it.equals("N/A", ignoreCase = true) }
        ?: "Tiempo limitado"
    val cleanInstructions = instructions
        ?.replace(Regex("<[^>]*>"), "")
        ?.replace("&quot;", "\"")
        ?.replace("&amp;", "&")
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?: "Reclama directamente desde la tienda oficial."

    return LootGiveaway(
        id = safeId,
        title = safeTitle,
        worth = cleanWorth,
        imageUrl = image ?: thumbnail,
        description = description?.trim().orEmpty(),
        instructions = cleanInstructions,
        claimUrl = safeClaimUrl,
        type = type?.trim().orEmpty().ifEmpty { "Game" },
        platforms = platforms?.trim().orEmpty().ifEmpty { "PC" },
        endDate = cleanEndDate
    )
}
