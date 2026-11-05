package com.example.gamevault.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gamevault.model.BacklogStatus

// --- PALETA DE COLORES (Cyberpunk / Sci-Fi) ---
val BgBlack = Color(0xFF05070A) // Negro profundo
val SurfaceDark = Color(0xFF10141D) // Gris azulado para tarjetas
val NeonCyan = Color(0xFF00F0FF) // Cian eléctrico
val NeonPink = Color(0xFFFF0055) // Rosa/Magenta eléctrico
val NeonGreen = Color(0xFF00E676) // Verde neón para completados
val NeonYellow = Color(0xFFFFD600) // Amarillo neón para en cola / rating
val TextMuted = Color(0xFF8B95A6)

// Gradiente icónico para luces LED y botones
val CyberGradient = Brush.horizontalGradient(listOf(NeonCyan, NeonPink))

fun BacklogStatus.statusColor(): Color = when (this) {
    BacklogStatus.PLAN_TO_PLAY -> NeonYellow
    BacklogStatus.PLAYING -> NeonCyan
    BacklogStatus.COMPLETED -> NeonGreen
    BacklogStatus.DROPPED -> NeonPink
}

@Composable
fun CyberBottomNavigation(
    currentRoute: String = "home",
    vaultCount: Int = 0,
    onNavigate: (String) -> Unit = {}
) {
    NavigationBar(containerColor = SurfaceDark, tonalElevation = 8.dp) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { onNavigate("home") },
            icon = { Icon(Icons.Outlined.Home, contentDescription = "Catálogo") },
            label = { Text("CATÁLOGO", fontSize = 8.sp, fontWeight = FontWeight.Bold, maxLines = 1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonCyan,
                selectedTextColor = NeonCyan,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = currentRoute == "loot",
            onClick = { onNavigate("loot") },
            icon = { Icon(Icons.Default.CardGiftcard, contentDescription = "Loot Radar") },
            label = { Text("LOOT", fontSize = 8.sp, fontWeight = FontWeight.Bold, maxLines = 1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonGreen,
                selectedTextColor = NeonGreen,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = currentRoute == "duel",
            onClick = { onNavigate("duel") },
            icon = { Icon(Icons.Default.SportsEsports, contentDescription = "Arcade Vault") },
            label = { Text("ARCADE", fontSize = 8.sp, fontWeight = FontWeight.Bold, maxLines = 1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonPink,
                selectedTextColor = NeonPink,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = currentRoute == "backlog",
            onClick = { onNavigate("backlog") },
            icon = {
                BadgedBox(
                    badge = {
                        if (vaultCount > 0) {
                            Badge(
                                containerColor = NeonPink,
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = vaultCount.toString(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Inventory2, contentDescription = "Backlog Matrix")
                }
            },
            label = { Text("BÓVEDA", fontSize = 8.sp, fontWeight = FontWeight.Bold, maxLines = 1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonYellow,
                selectedTextColor = NeonYellow,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = { onNavigate("profile") },
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil Netrunner") },
            label = { Text("PERFIL", fontSize = 8.sp, fontWeight = FontWeight.Bold, maxLines = 1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonCyan,
                selectedTextColor = NeonCyan,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = Color.Transparent
            )
        )
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
        Text(
            text = label,
            color = TextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value.uppercase(),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
