package com.example.gamevault

import com.google.gson.annotations.SerializedName

data class Game(
    val id: Int,
    val title: String,
    val thumbnail: String,
    @SerializedName("short_description") val shortDescription: String,
    @SerializedName("game_url") val gameUrl: String,
    val genre: String,
    val platform: String,
    val publisher: String,
    val developer: String,
    @SerializedName("release_date") val releaseDate: String
)