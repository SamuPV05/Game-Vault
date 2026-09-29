package com.example.gamevault

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    // Lista donde se guardarán los juegos. La UI se actualizará sola cuando esto cambie.
    var games by mutableStateOf<List<Game>>(emptyList())
        private set

    // Indicador de carga (ruedita girando) mientras llegan los datos
    var isLoading by mutableStateOf(true)
        private set

    init {
        fetchGames() // Se ejecuta automáticamente al abrir la app
    }

    private fun fetchGames() {
        viewModelScope.launch {
            try {
                // Va a la API y descarga la lista de juegos gratuitos
                games = RetrofitClient.apiService.getGames()
            } catch (e: Exception) {
                // Si no hay internet o falla, muestra el error en la consola
                e.printStackTrace()
            } finally {
                // Pase lo que pase, apaga el indicador de carga al terminar
                isLoading = false
            }
        }
    }
}