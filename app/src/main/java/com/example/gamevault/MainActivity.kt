package com.example.gamevault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gamevault.ui.ArcadeScreen
import com.example.gamevault.ui.BacklogScreen
import com.example.gamevault.ui.GameDetailScreen
import com.example.gamevault.ui.GameHomeScreen
import com.example.gamevault.ui.GameSplashScreen
import com.example.gamevault.ui.LootScreen
import com.example.gamevault.ui.ProfileScreen
import com.example.gamevault.ui.components.BgBlack
import com.example.gamevault.viewmodel.CoverQuizViewModel
import com.example.gamevault.viewmodel.DuelViewModel
import com.example.gamevault.viewmodel.LootViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(background = BgBlack)) {
                Surface(color = BgBlack, modifier = Modifier.fillMaxSize()) {
                    GameCatalogNavGraph()
                }
            }
        }
    }
}

@Composable
fun GameCatalogNavGraph(
    viewModel: GameViewModel = viewModel(),
    duelViewModel: DuelViewModel = viewModel(),
    coverQuizViewModel: CoverQuizViewModel = viewModel(),
    lootViewModel: LootViewModel = viewModel()
) {
    val navController = rememberNavController()
    val catalogState by viewModel.uiState.collectAsState()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            GameSplashScreen(navController = navController)
        }
        composable("home") {
            GameHomeScreen(navController = navController, viewModel = viewModel)
        }
        composable("loot") {
            LootScreen(
                navController = navController,
                vaultCount = catalogState.favoriteIds.size,
                viewModel = lootViewModel
            )
        }
        composable("duel") {
            ArcadeScreen(
                navController = navController,
                vaultCount = catalogState.favoriteIds.size,
                duelViewModel = duelViewModel,
                coverQuizViewModel = coverQuizViewModel
            )
        }
        composable("backlog") {
            BacklogScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable("profile") {
            ProfileScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable("detail/{id}") { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            val game = catalogState.games.find { it.id == gameId }
            if (game != null) {
                GameDetailScreen(
                    navController = navController,
                    game = game,
                    backlogEntry = catalogState.backlogEntries[game.id],
                    onToggleFavorite = { viewModel.toggleFavorite(game.id) },
                    onStatusChange = { status -> viewModel.updateBacklogStatus(game.id, status) },
                    onRatingChange = { rating -> viewModel.updatePersonalRating(game.id, rating) }
                )
            }
        }
    }
}
