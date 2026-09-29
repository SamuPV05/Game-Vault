package com.example.gamevault

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// 1. Definimos la ruta específica
interface GameApiService {
    @GET("games")
    suspend fun getGames(): List<Game>
}

// 2. Construimos el cliente de Retrofit con la URL base
object RetrofitClient {
    private const val BASE_URL = "https://www.freetogame.com/api/"

    val apiService: GameApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GameApiService::class.java)
    }
}