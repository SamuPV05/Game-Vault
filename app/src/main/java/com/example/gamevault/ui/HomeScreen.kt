package com.example.gamevault.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.gamevault.GameViewModel
import com.example.gamevault.ui.components.BgBlack
import com.example.gamevault.ui.components.CyberBottomNavigation
import com.example.gamevault.ui.components.CyberGradient
import com.example.gamevault.ui.components.NeonCyan
import com.example.gamevault.ui.components.NeonPink
import com.example.gamevault.ui.components.SurfaceDark
import com.example.gamevault.ui.components.TextMuted

@Composable
fun GameHomeScreen(navController: NavHostController, viewModel: GameViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val categories = listOf(
        "All",
        "Favorites",
        "Shooter",
        "MMORPG",
        "Strategy",
        "Racing",
        "Sports",
        "Fighting"
    )

    Scaffold(
        containerColor = BgBlack,
        bottomBar = {
            CyberBottomNavigation(
                currentRoute = "home",
                vaultCount = uiState.favoriteIds.size,
                onNavigate = { route ->
                    if (route != "home") {
                        navController.navigate(route) {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = NeonCyan)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "SINCRONIZANDO BASE DE DATOS...",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }

            uiState.errorMessage != null && uiState.games.isEmpty() -> {
                CatalogErrorView(
                    message = uiState.errorMessage ?: "Error desconocido",
                    onRetry = { viewModel.fetchGames() },
                    onPlayDuel = { navController.navigate("duel") },
                    modifier = Modifier.padding(paddingValues)
                )
            }

            else -> {
                val filteredGames = uiState.filteredGames
                val chunkedGames = filteredGames.chunked(2)

                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // Header Tecnológico
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "USER_SAMUEL // VAULT (${uiState.favoriteIds.size})",
                                    color = NeonCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp
                                )
                                Text(
                                    text = "DATABASE",
                                    color = Color.White,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(CutCornerShape(12.dp))
                                    .background(SurfaceDark)
                                    .border(1.dp, NeonPink, CutCornerShape(12.dp))
                                    .clickable { navController.navigate("duel") }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.SportsEsports,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "DUEL",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }
                        }
                    }

                    // Buscador Cyberpunk en Tiempo Real
                    item {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChange(it) },
                            placeholder = {
                                Text(
                                    text = "Buscar por título, género, estudio o plataforma...",
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Buscar",
                                    tint = NeonCyan
                                )
                            },
                            trailingIcon = {
                                if (uiState.searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Limpiar búsqueda",
                                            tint = NeonPink
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = SurfaceDark,
                                unfocusedContainerColor = SurfaceDark,
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = NeonCyan.copy(alpha = 0.35f),
                                cursorColor = NeonCyan
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .padding(bottom = 20.dp)
                        )
                    }

                    // Banner de acceso rápido a Metacritic Duel
                    if (uiState.searchQuery.isBlank()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                                    .padding(bottom = 24.dp)
                                    .clip(CutCornerShape(topStart = 20.dp, bottomEnd = 20.dp))
                                    .background(SurfaceDark)
                                    .border(
                                        2.dp,
                                        CyberGradient,
                                        CutCornerShape(topStart = 20.dp, bottomEnd = 20.dp)
                                    )
                                    .clickable { navController.navigate("duel") }
                                    .padding(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "MODO INTERACTIVO // RAWG API",
                                            color = NeonPink,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.5.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "METACRITIC DUEL",
                                            color = Color.White,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "¿Más alto o más bajo? Pon a prueba tu racha de Metascore.",
                                            color = TextMuted,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(CutCornerShape(12.dp))
                                            .background(NeonCyan)
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "JUGAR ▲▼",
                                            color = BgBlack,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Categorías con forma de botones Sci-Fi (incluye MI BÓVEDA / Favorites)
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(bottom = 28.dp)
                        ) {
                            items(categories) { category ->
                                val isSelected = uiState.selectedCategory == category
                                val isFavoritesTab = category == "Favorites"
                                val activeColor = if (isFavoritesTab) NeonPink else NeonCyan
                                val label = if (isFavoritesTab) {
                                    "★ BÓVEDA (${uiState.favoriteIds.size})"
                                } else {
                                    category.uppercase()
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(CutCornerShape(bottomEnd = 16.dp, topStart = 16.dp))
                                        .background(if (isSelected) activeColor else SurfaceDark)
                                        .border(
                                            width = 1.dp,
                                            color = if (isFavoritesTab && !isSelected) {
                                                NeonPink.copy(alpha = 0.5f)
                                            } else {
                                                Color.Transparent
                                            },
                                            shape = CutCornerShape(bottomEnd = 16.dp, topStart = 16.dp)
                                        )
                                        .clickable { viewModel.onCategorySelect(category) }
                                        .padding(horizontal = 20.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = label,
                                        color = when {
                                            isSelected && isFavoritesTab -> Color.White
                                            isSelected -> BgBlack
                                            isFavoritesTab -> NeonPink
                                            else -> TextMuted
                                        },
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }
                        }
                    }

                    // Carrusel de Destacados (Con bordes de neón)
                    if (uiState.selectedCategory == "All" &&
                        uiState.searchQuery.isBlank() &&
                        uiState.games.size > 5
                    ) {
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(20.dp),
                                modifier = Modifier.padding(bottom = 32.dp)
                            ) {
                                items(uiState.games.take(5)) { game ->
                                    val isFav = game.id in uiState.favoriteIds
                                    Box(
                                        modifier = Modifier
                                            .width(320.dp)
                                            .height(200.dp)
                                            .clip(CutCornerShape(topEnd = 30.dp, bottomStart = 30.dp))
                                            .border(
                                                2.dp,
                                                CyberGradient,
                                                CutCornerShape(topEnd = 30.dp, bottomStart = 30.dp)
                                            )
                                            .clickable { navController.navigate("detail/${game.id}") }
                                    ) {
                                        AsyncImage(
                                            model = game.thumbnail,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Brush.verticalGradient(
                                                        listOf(
                                                            Color.Transparent,
                                                            BgBlack.copy(alpha = 0.9f)
                                                        )
                                                    )
                                                )
                                        )

                                        IconButton(
                                            onClick = { viewModel.toggleFavorite(game.id) },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(10.dp)
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(BgBlack.copy(alpha = 0.65f))
                                        ) {
                                            Icon(
                                                imageVector = if (isFav) {
                                                    Icons.Filled.Favorite
                                                } else {
                                                    Icons.Outlined.FavoriteBorder
                                                },
                                                contentDescription = "Favorito",
                                                tint = if (isFav) NeonPink else Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Column(
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .padding(20.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .background(NeonPink, CutCornerShape(4.dp))
                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                                    .padding(bottom = 4.dp)
                                            ) {
                                                Text(
                                                    text = "FEATURED",
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = game.title.uppercase(),
                                                color = Color.White,
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Black,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Estado vacío si no hay resultados para la búsqueda o favoritos
                    if (chunkedGames.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 40.dp)
                                    .clip(CutCornerShape(16.dp))
                                    .background(SurfaceDark)
                                    .padding(28.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = if (uiState.selectedCategory == "Favorites") {
                                            "TU BÓVEDA ESTÁ VACÍA"
                                        } else {
                                            "SIN RESULTADOS"
                                        },
                                        color = NeonCyan,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 2.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (uiState.selectedCategory == "Favorites") {
                                            "Pulsa el ícono ♥ en cualquier juego para guardarlo en tu bóveda personal."
                                        } else {
                                            "No se encontraron juegos que coincidan con \"${uiState.searchQuery}\"."
                                        },
                                        color = TextMuted,
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    // Grilla de Catálogo
                    items(chunkedGames) { rowGames ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            for (game in rowGames) {
                                val isFav = game.id in uiState.favoriteIds
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(CutCornerShape(bottomEnd = 20.dp))
                                        .background(SurfaceDark)
                                        .clickable { navController.navigate("detail/${game.id}") }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(110.dp)
                                    ) {
                                        AsyncImage(
                                            model = game.thumbnail,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        IconButton(
                                            onClick = { viewModel.toggleFavorite(game.id) },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(6.dp)
                                                .size(30.dp)
                                                .clip(CircleShape)
                                                .background(BgBlack.copy(alpha = 0.65f))
                                        ) {
                                            Icon(
                                                imageVector = if (isFav) {
                                                    Icons.Filled.Favorite
                                                } else {
                                                    Icons.Outlined.FavoriteBorder
                                                },
                                                contentDescription = "Favorito",
                                                tint = if (isFav) NeonPink else Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(2.dp)
                                            .background(NeonCyan)
                                    )
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = game.title,
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = game.genre.uppercase(),
                                            color = NeonPink,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                            if (rowGames.size == 1) Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogErrorView(
    message: String,
    onRetry: () -> Unit,
    onPlayDuel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgBlack)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CutCornerShape(20.dp))
                .background(SurfaceDark)
                .border(1.dp, NeonPink, CutCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = null,
                tint = NeonPink,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "ERROR DE CONEXIÓN",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = TextMuted,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onRetry,
                shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = BgBlack
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("REINTENTAR CATÁLOGO", fontWeight = FontWeight.Black)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onPlayDuel,
                shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonPink,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.SportsEsports, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("JUGAR METACRITIC DUEL", fontWeight = FontWeight.Black)
            }
        }
    }
}
