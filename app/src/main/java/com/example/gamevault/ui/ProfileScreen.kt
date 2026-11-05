package com.example.gamevault.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gamevault.GameViewModel
import com.example.gamevault.model.CyberAchievement
import com.example.gamevault.model.CyberAvatar
import com.example.gamevault.ui.components.BgBlack
import com.example.gamevault.ui.components.CyberBottomNavigation
import com.example.gamevault.ui.components.CyberGradient
import com.example.gamevault.ui.components.NeonCyan
import com.example.gamevault.ui.components.NeonGreen
import com.example.gamevault.ui.components.NeonPink
import com.example.gamevault.ui.components.NeonYellow
import com.example.gamevault.ui.components.SurfaceDark
import com.example.gamevault.ui.components.TextMuted

@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: GameViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current

    // Sincronizar estadísticas al abrir la pantalla de perfil
    LaunchedEffect(Unit) {
        viewModel.refreshProfileStats()
    }

    val profile = uiState.profileSummary
    val animatedProgress by animateFloatAsState(
        targetValue = profile.levelProgress,
        animationSpec = tween(durationMillis = 700),
        label = "xpBarAnimation"
    )

    Scaffold(
        containerColor = BgBlack,
        bottomBar = {
            CyberBottomNavigation(
                currentRoute = "profile",
                vaultCount = uiState.favoriteIds.size,
                onNavigate = { route ->
                    if (route != "profile") {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 28.dp)
        ) {
            // Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 16.dp)
                ) {
                    Text(
                        text = "TERMINAL DE IDENTIDAD // NETRUNNER",
                        color = NeonGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "MI PERFIL GAMER",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Tarjeta Principal de Nivel y XP
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .clip(CutCornerShape(topStart = 24.dp, bottomEnd = 24.dp))
                        .background(SurfaceDark)
                        .border(
                            2.dp,
                            CyberGradient,
                            CutCornerShape(topStart = 24.dp, bottomEnd = 24.dp)
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar Cyberpunk Activo
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CutCornerShape(16.dp))
                                .background(BgBlack)
                                .border(2.dp, NeonCyan, CutCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = profile.avatar.icon(),
                                contentDescription = profile.avatar.alias,
                                tint = NeonCyan,
                                modifier = Modifier.size(38.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(NeonPink)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "LVL ${profile.level}",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                Text(
                                    text = profile.avatar.alias,
                                    color = NeonCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = profile.username,
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = profile.levelTitle,
                                color = NeonYellow,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Barra de progreso de XP hacia el siguiente nivel
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "XP TOTAL: ${profile.totalXp} PTS",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${profile.currentLevelXp} / ${profile.nextLevelXp} XP",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(50))
                            .background(BgBlack)
                            .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(50))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(animatedProgress)
                                .clip(RoundedCornerShape(50))
                                .background(CyberGradient)
                        )
                    }
                }
            }

            // Selector de Clase / Avatar Cyberpunk
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    Text(
                        text = "CLASE OPERATIVA // AVATAR",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(CyberAvatar.entries) { avatarItem ->
                            val isSelected = profile.avatar == avatarItem
                            Column(
                                modifier = Modifier
                                    .width(135.dp)
                                    .clip(CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp))
                                    .background(if (isSelected) SurfaceDark else BgBlack)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) NeonCyan else TextMuted.copy(alpha = 0.3f),
                                        shape = CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp)
                                    )
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        viewModel.selectAvatar(avatarItem)
                                    }
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = avatarItem.icon(),
                                    contentDescription = avatarItem.alias,
                                    tint = if (isSelected) NeonCyan else TextMuted,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = avatarItem.alias,
                                    color = if (isSelected) Color.White else TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    maxLines = 1
                                )
                                Text(
                                    text = avatarItem.roleLabel,
                                    color = if (isSelected) NeonPink else TextMuted.copy(alpha = 0.7f),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // Dashboard de Estadísticas Globales
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = "TELEMETRÍA DE JUGADOR",
                        color = NeonPink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProfileStatBox(
                            label = "MEJOR RACHA DUEL",
                            value = profile.duelHighScore.toString(),
                            accentColor = NeonYellow,
                            modifier = Modifier.weight(1f)
                        )
                        ProfileStatBox(
                            label = "ACIERTOS DUEL",
                            value = profile.duelCorrectGuesses.toString(),
                            accentColor = NeonGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProfileStatBox(
                            label = "JUEGOS EN BÓVEDA",
                            value = profile.vaultTotalCount.toString(),
                            accentColor = NeonCyan,
                            modifier = Modifier.weight(1f)
                        )
                        ProfileStatBox(
                            label = "GÉNERO FAVORITO",
                            value = profile.favoriteGenre,
                            accentColor = NeonPink,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Sección de Insignias / Logros Desbloqueables
            item {
                val unlockedCount = profile.achievements.count { it.isUnlocked }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "INSIGNIAS DESBLOQUEADAS",
                        color = NeonYellow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "$unlockedCount / ${profile.achievements.size}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            items(profile.achievements, key = { it.id }) { achievement ->
                AchievementCard(achievement = achievement)
            }
        }
    }
}

@Composable
private fun ProfileStatBox(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(CutCornerShape(14.dp))
            .background(SurfaceDark)
            .border(1.dp, accentColor.copy(alpha = 0.45f), CutCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            color = accentColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AchievementCard(achievement: CyberAchievement) {
    val borderColor = if (achievement.isUnlocked) NeonGreen else TextMuted.copy(alpha = 0.25f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .clip(CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp))
            .background(SurfaceDark)
            .border(
                1.dp,
                borderColor,
                CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp)
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (achievement.isUnlocked) NeonGreen.copy(alpha = 0.18f) else BgBlack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (achievement.isUnlocked) {
                    Icons.Default.CheckCircle
                } else {
                    Icons.Default.Lock
                },
                contentDescription = null,
                tint = if (achievement.isUnlocked) NeonGreen else TextMuted,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = achievement.title,
                color = if (achievement.isUnlocked) Color.White else TextMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = achievement.description,
                color = TextMuted,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "+${achievement.xpReward} XP",
                color = if (achievement.isUnlocked) NeonYellow else TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = achievement.progressText,
                color = if (achievement.isUnlocked) NeonGreen else NeonCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun CyberAvatar.icon(): ImageVector = when (this) {
    CyberAvatar.NETRUNNER -> Icons.Default.Terminal
    CyberAvatar.SAMURAI -> Icons.Default.Bolt
    CyberAvatar.HACKER -> Icons.Default.Code
    CyberAvatar.SYNTH -> Icons.Default.Memory
}
