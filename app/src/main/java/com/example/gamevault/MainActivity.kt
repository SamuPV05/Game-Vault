package com.example.gamevault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

// --- PALETA DE COLORES (Cyberpunk / Sci-Fi) ---
val BgBlack = Color(0xFF05070A) // Negro profundo
val SurfaceDark = Color(0xFF10141D) // Gris azulado para tarjetas
val NeonCyan = Color(0xFF00F0FF) // Cian eléctrico
val NeonPink = Color(0xFFFF0055) // Rosa/Magenta eléctrico
val TextMuted = Color(0xFF8B95A6)

// Gradiente icónico para luces LED y botones
val CyberGradient = Brush.horizontalGradient(listOf(NeonCyan, NeonPink))

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
fun GameCatalogNavGraph(viewModel: GameViewModel = viewModel()) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { GameSplashScreen(navController) }
        composable("home") { GameHomeScreen(navController, viewModel) }
        composable("detail/{id}") { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            val game = viewModel.games.find { it.id == gameId }
            if (game != null) GameDetailScreen(navController, game)
        }
    }
}

// ==========================================
// 1. SPLASH SCREEN (Cyber Interface)
// ==========================================
@Composable
fun GameSplashScreen(navController: NavHostController) {
    LaunchedEffect(Unit) {
        delay(2500)
        navController.navigate("home") { popUpTo("splash") { inclusive = true } }
    }
    Box(modifier = Modifier.fillMaxSize().background(BgBlack), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Icono enmarcado en hexágono simulado (CutCorner)
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CutCornerShape(24.dp))
                    .border(2.dp, CyberGradient, CutCornerShape(24.dp))
                    .background(SurfaceDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.SportsEsports, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(60.dp))
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text("GAME VAULT", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Black, letterSpacing = 6.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.height(2.dp).width(100.dp).background(CyberGradient))
            Spacer(modifier = Modifier.height(8.dp))
            Text("SYSTEM INITIALIZING...", color = NeonPink, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
        }
    }
}

// ==========================================
// 2. HOME SCREEN (Launcher Style)
// ==========================================
@Composable
fun GameHomeScreen(navController: NavHostController, viewModel: GameViewModel) {
    val categories = listOf("All", "Shooter", "MMORPG", "Strategy", "Racing", "Sports", "Fighting")
    var selectedCategory by remember { mutableStateOf("All") }

    Scaffold(
        containerColor = BgBlack,
        bottomBar = { CyberBottomNavigation() }
    ) { paddingValues ->
        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonCyan)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues).fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Header Tecnológico
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("USER_SAMUEL", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                            Text("DATABASE", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                        }
                        Box(
                            modifier = Modifier.size(45.dp).clip(CutCornerShape(12.dp)).background(SurfaceDark).border(1.dp, NeonPink, CutCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
                        }
                    }
                }

                // Categorías con forma de botones Sci-Fi
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(bottom = 28.dp)
                    ) {
                        items(categories) { category ->
                            val isSelected = selectedCategory == category
                            Box(
                                modifier = Modifier
                                    .clip(CutCornerShape(bottomEnd = 16.dp, topStart = 16.dp))
                                    .background(if (isSelected) NeonCyan else SurfaceDark)
                                    .clickable { selectedCategory = category }
                                    .padding(horizontal = 24.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = category.uppercase(),
                                    color = if (isSelected) BgBlack else TextMuted,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }

                // Carrusel de Destacados (Con bordes de neón)
                if (selectedCategory == "All" && viewModel.games.size > 5) {
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            modifier = Modifier.padding(bottom = 32.dp)
                        ) {
                            items(viewModel.games.take(5)) { game ->
                                Box(
                                    modifier = Modifier
                                        .width(320.dp)
                                        .height(200.dp)
                                        .clip(CutCornerShape(topEnd = 30.dp, bottomStart = 30.dp))
                                        .border(2.dp, CyberGradient, CutCornerShape(topEnd = 30.dp, bottomStart = 30.dp))
                                        .clickable { navController.navigate("detail/${game.id}") }
                                ) {
                                    AsyncImage(
                                        model = game.thumbnail, contentDescription = null,
                                        contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
                                    )
                                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, BgBlack.copy(alpha = 0.9f)))))

                                    Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                                        Box(modifier = Modifier.background(NeonPink, CutCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 2.dp).padding(bottom = 4.dp)) {
                                            Text("FEATURED", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(game.title.uppercase(), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                        }
                    }
                }

                val filteredGames = if (selectedCategory == "All") viewModel.games.drop(5) else viewModel.games.filter { it.genre.contains(selectedCategory, ignoreCase = true) }
                val chunkedGames = filteredGames.chunked(2)

                // Grilla de Catálogo
                items(chunkedGames) { rowGames ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        for (game in rowGames) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(CutCornerShape(bottomEnd = 20.dp))
                                    .background(SurfaceDark)
                                    .clickable { navController.navigate("detail/${game.id}") }
                            ) {
                                AsyncImage(
                                    model = game.thumbnail, contentDescription = null,
                                    contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(110.dp)
                                )
                                // Borde superior decorativo
                                Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(NeonCyan))
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(game.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(game.genre.uppercase(), color = NeonPink, fontSize = 10.sp, fontWeight = FontWeight.Black)
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

// ==========================================
// Bottom Navigation (Cyber Style)
// ==========================================
@Composable
fun CyberBottomNavigation() {
    NavigationBar(containerColor = SurfaceDark, tonalElevation = 8.dp) {
        NavigationBarItem(
            selected = true, onClick = { },
            icon = { Icon(Icons.Outlined.Home, contentDescription = null) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = NeonCyan, unselectedIconColor = TextMuted, indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = false, onClick = { },
            icon = { Icon(Icons.Default.Explore, contentDescription = null) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = NeonCyan, unselectedIconColor = TextMuted, indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = false, onClick = { },
            icon = { Icon(Icons.Outlined.Person, contentDescription = null) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = NeonCyan, unselectedIconColor = TextMuted, indicatorColor = Color.Transparent)
        )
    }
}

// ==========================================
// 3. DETAIL SCREEN (Ficha Técnica)
// ==========================================
@Composable
fun GameDetailScreen(navController: NavHostController, game: Game) {
    Scaffold(
        containerColor = BgBlack,
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .clip(CutCornerShape(topStart = 20.dp, bottomEnd = 20.dp))
                    .background(CyberGradient)
                    .clickable { /* Jugar */ }
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PowerSettingsNew, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("INITIATE", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp, letterSpacing = 2.sp)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                    AsyncImage(
                        model = game.thumbnail, contentDescription = null,
                        contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
                    )
                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, BgBlack))))

                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(16.dp).clip(CutCornerShape(12.dp)).background(BgBlack.copy(alpha = 0.6f))
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Back", tint = NeonCyan, modifier = Modifier.size(32.dp))
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(game.title.uppercase(), color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Black, lineHeight = 40.sp)
                    Text("DEV // ${game.developer.uppercase()}", color = NeonPink, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp))

                    // Estadísticas estilo Dashboard
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        CyberStat("GENRE", game.genre)
                        CyberStat("RELEASE", game.releaseDate)
                        CyberStat("PLATFORM", game.platform.take(10))
                    }

                    Text("TRANSMISSION LOG", color = NeonCyan, fontSize = 16.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(bottom = 12.dp, top = 16.dp), letterSpacing = 1.sp)
                    Text(game.shortDescription, color = TextMuted, fontSize = 15.sp, lineHeight = 24.sp, modifier = Modifier.padding(bottom = 100.dp))
                }
            }
        }
    }
}

@Composable
fun CyberStat(label: String, value: String) {
    Column(
        modifier = Modifier
            .border(1.dp, SurfaceDark, CutCornerShape(8.dp))
            .background(BgBlack)
            .padding(12.dp)
            .width(90.dp)
    ) {
        Text(label, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value.uppercase(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}