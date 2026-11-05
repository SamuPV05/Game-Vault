package com.example.gamevault.data

import com.example.gamevault.Game
import com.example.gamevault.GameApiService
import com.example.gamevault.RetrofitClient

/**
 * Repositorio para el catálogo general de juegos (FreeToGame API).
 * Encapsula las llamadas de red y provee manejo seguro de errores.
 */
class CatalogRepository(
    private val apiService: GameApiService = RetrofitClient.apiService
) {
    suspend fun fetchCatalogGames(): Result<List<Game>> {
        return runCatching {
            apiService.getGames()
        }
    }
}
