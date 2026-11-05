package com.example.gamevault.data

import com.example.gamevault.model.GiveawayDto
import com.example.gamevault.model.LootGiveaway
import com.example.gamevault.model.toDomainOrNull
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * Servicio Retrofit para consultar promociones y juegos 100% gratis en vivo desde GamerPower API.
 */
interface GamerPowerApiService {
    @GET("giveaways")
    suspend fun getActiveGiveaways(): List<GiveawayDto>
}

object GamerPowerRetrofitClient {
    private const val BASE_URL = "https://www.gamerpower.com/api/"

    val apiService: GamerPowerApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GamerPowerApiService::class.java)
    }
}

class LootRepository(
    private val apiService: GamerPowerApiService = GamerPowerRetrofitClient.apiService
) {
    /**
     * Obtiene las promociones activas de juegos y recompensas gratuitas.
     * Si hay un fallo de red, retorna un listado de respaldo para garantizar estabilidad total.
     */
    suspend fun fetchGiveaways(): List<LootGiveaway> {
        return try {
            val response = apiService.getActiveGiveaways()
            val items = response.mapNotNull { it.toDomainOrNull() }
            if (items.isNotEmpty()) items else getFallbackGiveaways()
        } catch (e: Exception) {
            e.printStackTrace()
            getFallbackGiveaways()
        }
    }

    private fun getFallbackGiveaways(): List<LootGiveaway> = listOf(
        LootGiveaway(
            id = 9001,
            title = "Epic Games Store: Juego Misterioso Semanal (100% Gratis)",
            worth = "$29.99",
            imageUrl = "https://media.rawg.io/media/games/26d/26d4437715bee60138dab4a7c8c59c92.jpg",
            description = "Reclama el juego gratuito de esta semana en Epic Games Store y consérvalo para siempre en tu biblioteca.",
            instructions = "1. Inicia sesión en Epic Games Store. 2. Haz clic en Obtener antes del jueves.",
            claimUrl = "https://store.epicgames.com/es-ES/free-games",
            type = "Game",
            platforms = "PC, Epic Games Store",
            endDate = "Jueves 17:00 UTC"
        ),
        LootGiveaway(
            id = 9002,
            title = "Steam: Promociones Free-to-Keep Activas",
            worth = "$19.99",
            imageUrl = "https://media.rawg.io/media/games/736/73619bd336c894d6941d926bfd563946.jpg",
            description = "Explora los títulos con 100% de descuento temporal en Steam para añadirlos permanentemente a tu cuenta.",
            instructions = "1. Abre el enlace en tu navegador. 2. Pulsa Añadir a la cuenta.",
            claimUrl = "https://store.steampowered.com/search/?maxprice=free&specials=1",
            type = "Game",
            platforms = "PC, Steam",
            endDate = "Tiempo limitado"
        ),
        LootGiveaway(
            id = 9003,
            title = "GOG.com: Clásicos DRM-Free y Giveaways",
            worth = "$14.99",
            imageUrl = "https://media.rawg.io/media/games/618/618c2031a07bbff6b4f611f10b6bcdbc.jpg",
            description = "Colección de juegos clásicos y packs de arte digitales sin DRM disponibles gratis en GOG.",
            instructions = "1. Accede a GOG.com. 2. Reclama desde el banner principal de ofertas.",
            claimUrl = "https://www.gog.com/partner/free_games",
            type = "Game",
            platforms = "PC, GOG",
            endDate = "Disponible ahora"
        )
    )
}
