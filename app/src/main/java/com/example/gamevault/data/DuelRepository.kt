package com.example.gamevault.data

import com.example.gamevault.model.GameDuelItem
import com.example.gamevault.model.RawgGamesResponse
import com.example.gamevault.model.toDomainOrNull
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Servicio Retrofit para consumir la API pública de RAWG.
 * Endpoint: GET https://api.rawg.io/api/games?key={API_KEY}&ordering=-added&page_size=40
 */
interface RawgApiService {
    @GET("games")
    suspend fun getGamesForDuel(
        @Query("key") apiKey: String = RawgRetrofitClient.RAWG_API_KEY,
        @Query("ordering") ordering: String = "-added",
        @Query("page_size") pageSize: Int = 40
    ): RawgGamesResponse
}

object RawgRetrofitClient {
    private const val BASE_URL = "https://api.rawg.io/api/"

    // Reemplaza con tu API Key de https://rawg.io/apidocs si deseas consultar datos en vivo adicionales.
    const val RAWG_API_KEY = "YOUR_RAWG_API_KEY"

    val apiService: RawgApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RawgApiService::class.java)
    }
}

class DuelRepository(
    private val apiService: RawgApiService = RawgRetrofitClient.apiService
) {
    /**
     * Obtiene la lista de juegos para Metacritic Duel filtrando estrictamente:
     * metacritic != null && metacritic > 0.
     * Si la API Key no está configurada o hay un fallo de red, retorna un mazo de respaldo
     * con datos reales de RAWG/Metacritic para garantizar estabilidad y cero crashes.
     */
    suspend fun fetchDuelGames(): List<GameDuelItem> {
        return try {
            if (RawgRetrofitClient.RAWG_API_KEY.isBlank() ||
                RawgRetrofitClient.RAWG_API_KEY == "YOUR_RAWG_API_KEY"
            ) {
                getFallbackDuelGames()
            } else {
                val response = apiService.getGamesForDuel()
                val validGames = response.results
                    .orEmpty()
                    .mapNotNull { it.toDomainOrNull() }
                    .distinctBy { it.id }

                if (validGames.size >= 2) {
                    validGames
                } else {
                    getFallbackDuelGames()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            getFallbackDuelGames()
        }
    }

    private fun getFallbackDuelGames(): List<GameDuelItem> = listOf(
        GameDuelItem(
            id = 3498,
            name = "Grand Theft Auto V",
            backgroundImage = "https://media.rawg.io/media/games/20a/20aa03a10cda45239fe22d035c0ebe64.jpg",
            metacritic = 92
        ),
        GameDuelItem(
            id = 3328,
            name = "The Witcher 3: Wild Hunt",
            backgroundImage = "https://media.rawg.io/media/games/618/618c2031a07bbff6b4f611f10b6bcdbc.jpg",
            metacritic = 92
        ),
        GameDuelItem(
            id = 4200,
            name = "Portal 2",
            backgroundImage = "https://media.rawg.io/media/games/2ba/2bac0e87cf45e5b508f227d281c9252a.jpg",
            metacritic = 95
        ),
        GameDuelItem(
            id = 5286,
            name = "Tomb Raider (2013)",
            backgroundImage = "https://media.rawg.io/media/games/021/021c4e21a1824d2526f925eff6324653.jpg",
            metacritic = 86
        ),
        GameDuelItem(
            id = 4291,
            name = "Counter-Strike: Global Offensive",
            backgroundImage = "https://media.rawg.io/media/games/736/73619bd336c894d6941d926bfd563946.jpg",
            metacritic = 81
        ),
        GameDuelItem(
            id = 12020,
            name = "Left 4 Dead 2",
            backgroundImage = "https://media.rawg.io/media/games/d58/d588947d4286e7b5e0e12e1bea7d9844.jpg",
            metacritic = 89
        ),
        GameDuelItem(
            id = 5679,
            name = "The Elder Scrolls V: Skyrim",
            backgroundImage = "https://media.rawg.io/media/games/7cf/7cfc9220b401b7a300e409e539c9afd5.jpg",
            metacritic = 94
        ),
        GameDuelItem(
            id = 28,
            name = "Red Dead Redemption 2",
            backgroundImage = "https://media.rawg.io/media/games/511/5118aff5091cb3efec399c808f8c598f.jpg",
            metacritic = 96
        ),
        GameDuelItem(
            id = 4062,
            name = "BioShock Infinite",
            backgroundImage = "https://media.rawg.io/media/games/fc1/fc1307a2774506b5bd65d7e8424664a7.jpg",
            metacritic = 94
        ),
        GameDuelItem(
            id = 802,
            name = "Borderlands 2",
            backgroundImage = "https://media.rawg.io/media/games/49c/49c3dfa4ce2f6f140cc4825868e858cb.jpg",
            metacritic = 89
        ),
        GameDuelItem(
            id = 3439,
            name = "Life is Strange",
            backgroundImage = "https://media.rawg.io/media/games/562/562553814dd54e001a541e4ee83a591c.jpg",
            metacritic = 83
        ),
        GameDuelItem(
            id = 13536,
            name = "Portal",
            backgroundImage = "https://media.rawg.io/media/games/7fa/7fa0b586293c5861ee32490e953a4996.jpg",
            metacritic = 90
        ),
        GameDuelItem(
            id = 1030,
            name = "Limbo",
            backgroundImage = "https://media.rawg.io/media/games/942/9424d6bb763dc38d9378b488603c87fa.jpg",
            metacritic = 88
        ),
        GameDuelItem(
            id = 2454,
            name = "DOOM (2016)",
            backgroundImage = "https://media.rawg.io/media/games/c4b/c4b0cab189e73432de3a250d8cf1c84e.jpg",
            metacritic = 85
        ),
        GameDuelItem(
            id = 3070,
            name = "Fallout 4",
            backgroundImage = "https://media.rawg.io/media/games/d82/d82990b9c67ba0d2d09d4e6fa88885a7.jpg",
            metacritic = 84
        ),
        GameDuelItem(
            id = 58175,
            name = "God of War (2018)",
            backgroundImage = "https://media.rawg.io/media/games/4be/4be6a6ad0364751a96229c56bf69be59.jpg",
            metacritic = 94
        ),
        GameDuelItem(
            id = 3939,
            name = "PAYDAY 2",
            backgroundImage = "https://media.rawg.io/media/games/73e/73e28909e0c39fb246f457b5d6cbbe6d.jpg",
            metacritic = 79
        ),
        GameDuelItem(
            id = 278,
            name = "Horizon Zero Dawn",
            backgroundImage = "https://media.rawg.io/media/games/b7d/b7d3f1715fa8381a4e780173a197a615.jpg",
            metacritic = 89
        ),
        GameDuelItem(
            id = 41494,
            name = "Cyberpunk 2077",
            backgroundImage = "https://media.rawg.io/media/games/26d/26d4437715bee60138dab4a7c8c59c92.jpg",
            metacritic = 86
        ),
        GameDuelItem(
            id = 326292,
            name = "Fall Guys",
            backgroundImage = "https://media.rawg.io/media/games/5eb/5eb49eb2fa0738fdb5bacea557b1bc57.jpg",
            metacritic = 80
        ),
        GameDuelItem(
            id = 422,
            name = "Terraria",
            backgroundImage = "https://media.rawg.io/media/games/f46/f466571d536f2e3ea9e815ad17177501.jpg",
            metacritic = 83
        ),
        GameDuelItem(
            id = 10213,
            name = "Dota 2",
            backgroundImage = "https://media.rawg.io/media/games/6fc/6fcf4cd3b17c288821388e6085bb0fc9.jpg",
            metacritic = 90
        ),
        GameDuelItem(
            id = 766,
            name = "Warframe",
            backgroundImage = "https://media.rawg.io/media/games/f87/f87457e8347484033cb34cde6101d08d.jpg",
            metacritic = 69
        ),
        GameDuelItem(
            id = 3192,
            name = "Metal Gear Solid V: The Phantom Pain",
            backgroundImage = "https://media.rawg.io/media/games/490/49016e06ae2103881ff6373248843069.jpg",
            metacritic = 91
        )
    )
}
