package com.example.gamevault.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.gamevault.model.LootGiveaway
import com.example.gamevault.ui.components.BgBlack
import com.example.gamevault.ui.components.CyberBottomNavigation
import com.example.gamevault.ui.components.CyberGradient
import com.example.gamevault.ui.components.NeonCyan
import com.example.gamevault.ui.components.NeonGreen
import com.example.gamevault.ui.components.NeonYellow
import com.example.gamevault.ui.components.SurfaceDark
import com.example.gamevault.ui.components.TextMuted
import com.example.gamevault.viewmodel.LootViewModel
import java.util.Locale

@Composable
fun LootScreen(
    navController: NavHostController,
    vaultCount: Int,
    viewModel: LootViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val platforms = listOf("TODOS", "Steam", "Epic Games", "GOG", "PC", "DLC")

    Scaffold(
        containerColor = BgBlack,
        bottomBar = {
            CyberBottomNavigation(
                currentRoute = "loot",
                vaultCount = vaultCount,
                onNavigate = { route ->
                    if (route != "loot") {
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
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = NeonGreen)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "RASTREANDO JUEGOS GRATIS Y BOTÍN...",
                        color = NeonGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
            }
        } else {
            val filtered = uiState.filteredGiveaways
            val totalValueFormatted = String.format(Locale.US, "$%.0f USD", uiState.totalAvailableValueUsd)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 28.dp)
            ) {
                // Header
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
                                text = "SCANNER ACTIVO // 100% OFF",
                                color = NeonGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "LOOT RADAR",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        IconButton(
                            onClick = { viewModel.loadGiveaways() },
                            modifier = Modifier
                                .clip(CutCornerShape(12.dp))
                                .background(SurfaceDark)
                                .border(1.dp, NeonGreen.copy(alpha = 0.5f), CutCornerShape(12.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Actualizar radar",
                                tint = NeonGreen
                            )
                        }
                    }
                }

                // Panel Resumen de Botín Disponible
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 20.dp)
                            .clip(CutCornerShape(topStart = 18.dp, bottomEnd = 18.dp))
                            .background(SurfaceDark)
                            .border(2.dp, CyberGradient, CutCornerShape(topStart = 18.dp, bottomEnd = 18.dp))
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Radar,
                                    contentDescription = null,
                                    tint = NeonGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${uiState.giveaways.size} RECOMPENSAS DETECTADAS",
                                    color = NeonGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Reclama juegos gratis y gana +30 XP para tu Perfil Netrunner.",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "VALOR TOTAL",
                                color = TextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = totalValueFormatted,
                                color = NeonYellow,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                // Filtros por Tienda / Plataforma
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {
                        items(platforms) { platform ->
                            val isSelected = uiState.selectedPlatform == platform
                            Box(
                                modifier = Modifier
                                    .clip(CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp))
                                    .background(if (isSelected) NeonGreen else SurfaceDark)
                                    .clickable { viewModel.selectPlatform(platform) }
                                    .padding(horizontal = 18.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = platform.uppercase(),
                                    color = if (isSelected) BgBlack else TextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }

                items(filtered, key = { it.id }) { giveaway ->
                    val isClaimed = giveaway.id in uiState.claimedIds
                    LootGiveawayCard(
                        giveaway = giveaway,
                        isClaimed = isClaimed,
                        onClaimClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.markAsClaimed(giveaway.id)
                            runCatching {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(giveaway.claimUrl))
                                context.startActivity(intent)
                            }.onFailure {
                                Toast.makeText(
                                    context,
                                    "No se pudo abrir el enlace de la promoción",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LootGiveawayCard(
    giveaway: LootGiveaway,
    isClaimed: Boolean,
    onClaimClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp)
            .clip(CutCornerShape(topStart = 20.dp, bottomEnd = 20.dp))
            .background(SurfaceDark)
            .border(
                width = 1.dp,
                color = if (isClaimed) NeonCyan.copy(alpha = 0.5f) else NeonGreen.copy(alpha = 0.65f),
                shape = CutCornerShape(topStart = 20.dp, bottomEnd = 20.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(165.dp)
        ) {
            AsyncImage(
                model = giveaway.imageUrl,
                contentDescription = giveaway.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, BgBlack.copy(alpha = 0.92f))
                        )
                    )
            )

            // Badge de Plataforma arriba a la izquierda
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(BgBlack.copy(alpha = 0.8f))
                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = giveaway.platforms.uppercase(),
                    color = NeonCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1
                )
            }

            // Badge de Precio Tachado -> GRATIS arriba a la derecha
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(NeonGreen)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (giveaway.worth != "GRATIS") {
                    Text(
                        text = giveaway.worth,
                        color = BgBlack.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
                Text(
                    text = "100% GRATIS",
                    color = BgBlack,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = giveaway.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = giveaway.description,
                color = TextMuted,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = NeonYellow,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "EXPIRA: ${giveaway.endDate}",
                        color = NeonYellow,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onClaimClick,
                    shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isClaimed) SurfaceDark else NeonGreen,
                        contentColor = if (isClaimed) NeonCyan else BgBlack
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = if (isClaimed) {
                            Icons.Default.CheckCircle
                        } else {
                            Icons.AutoMirrored.Filled.OpenInNew
                        },
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isClaimed) "RECLAMADO (+30 XP)" else "RECLAMAR BOTÍN",
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
