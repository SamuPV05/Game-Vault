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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.gamevault.Game
import com.example.gamevault.GameViewModel
import com.example.gamevault.model.BacklogEntry
import com.example.gamevault.model.BacklogStatus
import com.example.gamevault.ui.components.BgBlack
import com.example.gamevault.ui.components.CyberBottomNavigation
import com.example.gamevault.ui.components.CyberGradient
import com.example.gamevault.ui.components.NeonCyan
import com.example.gamevault.ui.components.NeonGreen
import com.example.gamevault.ui.components.NeonPink
import com.example.gamevault.ui.components.NeonYellow
import com.example.gamevault.ui.components.SurfaceDark
import com.example.gamevault.ui.components.TextMuted
import com.example.gamevault.ui.components.statusColor
import kotlinx.coroutines.delay

@Composable
fun BacklogScreen(
    navController: NavHostController,
    viewModel: GameViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current

    var selectedStatusFilter by remember { mutableStateOf<BacklogStatus?>(null) }
    var showHackRandomDialog by remember { mutableStateOf(false) }

    val allBacklogGames = uiState.backlogGames
    val filteredBacklog = if (selectedStatusFilter == null) {
        allBacklogGames
    } else {
        allBacklogGames.filter { it.second.status == selectedStatusFilter }
    }

    val planCount = allBacklogGames.count { it.second.status == BacklogStatus.PLAN_TO_PLAY }
    val playingCount = allBacklogGames.count { it.second.status == BacklogStatus.PLAYING }
    val completedCount = allBacklogGames.count { it.second.status == BacklogStatus.COMPLETED }

    // Candidatos ideales para la ruleta "HACK RANDOM" (En Cola o Jugando; si está vacío, cualquiera del backlog o catálogo)
    val randomCandidates = remember(allBacklogGames, uiState.games) {
        val activeBacklog = allBacklogGames
            .filter {
                it.second.status == BacklogStatus.PLAN_TO_PLAY ||
                    it.second.status == BacklogStatus.PLAYING
            }
            .map { it.first }
        when {
            activeBacklog.isNotEmpty() -> activeBacklog
            allBacklogGames.isNotEmpty() -> allBacklogGames.map { it.first }
            else -> uiState.games
        }
    }

    if (showHackRandomDialog && randomCandidates.isNotEmpty()) {
        HackRandomDialog(
            candidates = randomCandidates,
            isFromVault = allBacklogGames.isNotEmpty(),
            onDismiss = { showHackRandomDialog = false },
            onOpenDetail = { chosenGame ->
                showHackRandomDialog = false
                navController.navigate("detail/${chosenGame.id}")
            },
            onStartPlayingNow = { chosenGame ->
                viewModel.updateBacklogStatus(chosenGame.id, BacklogStatus.PLAYING)
                showHackRandomDialog = false
                navController.navigate("detail/${chosenGame.id}")
            }
        )
    }

    Scaffold(
        containerColor = BgBlack,
        bottomBar = {
            CyberBottomNavigation(
                currentRoute = "backlog",
                vaultCount = uiState.favoriteIds.size,
                onNavigate = { route ->
                    if (route != "backlog") {
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PERSONAL VAULT // MATRIX",
                            color = NeonYellow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "MI BÓVEDA",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Botón HACK RANDOM (Decide por mí)
                    if (randomCandidates.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .clip(CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp))
                                .background(CyberGradient)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    showHackRandomDialog = true
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Casino,
                                    contentDescription = "Hack Random",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "DECIDE POR MÍ",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }

            // Dashboard de métricas de la bóveda
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MatrixMetricCard(
                        label = "EN COLA",
                        count = planCount,
                        accentColor = NeonYellow,
                        modifier = Modifier.weight(1f)
                    )
                    MatrixMetricCard(
                        label = "JUGANDO",
                        count = playingCount,
                        accentColor = NeonCyan,
                        modifier = Modifier.weight(1f)
                    )
                    MatrixMetricCard(
                        label = "COMPLETADOS",
                        count = completedCount,
                        accentColor = NeonGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Filtros por Estado
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    item {
                        val isAllSelected = selectedStatusFilter == null
                        Box(
                            modifier = Modifier
                                .clip(CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp))
                                .background(if (isAllSelected) Color.White else SurfaceDark)
                                .clickable { selectedStatusFilter = null }
                                .padding(horizontal = 18.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "TODOS (${allBacklogGames.size})",
                                color = if (isAllSelected) BgBlack else TextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    items(BacklogStatus.entries) { status ->
                        val isSelected = selectedStatusFilter == status
                        val color = status.statusColor()
                        val count = allBacklogGames.count { it.second.status == status }

                        Box(
                            modifier = Modifier
                                .clip(CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp))
                                .background(if (isSelected) color else SurfaceDark)
                                .border(
                                    1.dp,
                                    if (isSelected) color else color.copy(alpha = 0.4f),
                                    CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp)
                                )
                                .clickable { selectedStatusFilter = status }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "${status.label} ($count)",
                                color = if (isSelected) BgBlack else color,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }

            // Estado vacío si no hay juegos en el filtro
            if (filteredBacklog.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 24.dp)
                            .clip(CutCornerShape(20.dp))
                            .background(SurfaceDark)
                            .border(1.dp, NeonCyan.copy(alpha = 0.35f), CutCornerShape(20.dp))
                            .padding(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (allBacklogGames.isEmpty()) {
                                    "TU BACKLOG MATRIX ESTÁ VACÍO"
                                } else {
                                    "SIN JUEGOS EN '${selectedStatusFilter?.label}'"
                                },
                                color = NeonCyan,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (allBacklogGames.isEmpty()) {
                                    "Explora el catálogo o usa 'DECIDE POR MÍ' para descubrir a qué jugar hoy."
                                } else {
                                    "Cambia el estado de tus juegos con un toque desde sus tarjetas."
                                },
                                color = TextMuted,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(
                                    onClick = { navController.navigate("home") },
                                    shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NeonCyan,
                                        contentColor = BgBlack
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Explore,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("IR AL CATÁLOGO", fontWeight = FontWeight.Black, fontSize = 12.sp)
                                }

                                if (randomCandidates.isNotEmpty()) {
                                    OutlinedButton(
                                        onClick = { showHackRandomDialog = true },
                                        shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Casino,
                                            contentDescription = null,
                                            tint = NeonPink,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "RULETA",
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Lista de Juegos del Backlog
            items(filteredBacklog, key = { it.first.id }) { (game, entry) ->
                BacklogGameCard(
                    game = game,
                    entry = entry,
                    onClick = { navController.navigate("detail/${game.id}") },
                    onStatusChange = { newStatus ->
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.updateBacklogStatus(game.id, newStatus)
                    },
                    onRatingChange = { newRating ->
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.updatePersonalRating(game.id, newRating)
                    },
                    onRemove = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.removeFromBacklog(game.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun MatrixMetricCard(
    label: String,
    count: Int,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(CutCornerShape(12.dp))
            .background(SurfaceDark)
            .border(1.dp, accentColor.copy(alpha = 0.5f), CutCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            color = accentColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = TextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun BacklogGameCard(
    game: Game,
    entry: BacklogEntry,
    onClick: () -> Unit,
    onStatusChange: (BacklogStatus) -> Unit,
    onRatingChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    val statusColor = entry.status.statusColor()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .clip(CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp))
            .background(SurfaceDark)
            .border(
                1.dp,
                statusColor.copy(alpha = 0.55f),
                CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp)
            )
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = game.thumbnail,
                contentDescription = game.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(96.dp)
                    .height(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${game.genre.uppercase()} • ${game.platform}",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Quitar de la bóveda",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Selector rápido de estados (4 chips interactivos)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BacklogStatus.entries.forEach { status ->
                val isSelected = entry.status == status
                val chipColor = status.statusColor()

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) chipColor else BgBlack)
                        .border(
                            1.dp,
                            if (isSelected) chipColor else chipColor.copy(alpha = 0.3f),
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { onStatusChange(status) }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = status.badgeText,
                        color = if (isSelected) BgBlack else chipColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Calificación personal de 1 a 5 estrellas (equivalente a 2..10 pts)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (entry.personalRating > 0) {
                    "TU NOTA: ${entry.personalRating}/10"
                } else {
                    "CALIFICAR:"
                },
                color = if (entry.personalRating > 0) NeonYellow else TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                for (star in 1..5) {
                    val starScore = star * 2
                    val filled = entry.personalRating >= starScore
                    Icon(
                        imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Calificar $starScore",
                        tint = if (filled) NeonYellow else TextMuted,
                        modifier = Modifier
                            .size(22.dp)
                            .clickable { onRatingChange(starScore) }
                            .padding(2.dp)
                    )
                }
            }
        }
    }
}

/**
 * Modal interactivo estilo terminal Cyberpunk "HACK RANDOM" (Decide por mí).
 * Cicla rápidamente entre los juegos disponibles y selecciona un ganador al azar.
 */
@Composable
private fun HackRandomDialog(
    candidates: List<Game>,
    isFromVault: Boolean,
    onDismiss: () -> Unit,
    onOpenDetail: (Game) -> Unit,
    onStartPlayingNow: (Game) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var isSpinning by remember { mutableStateOf(true) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var spinKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(spinKey) {
        isSpinning = true
        val steps = 16
        for (i in 0 until steps) {
            currentIndex = candidates.indices.random()
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            delay(50L + (i * 12L))
        }
        isSpinning = false
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    val currentGame = candidates.getOrElse(currentIndex) { candidates.first() }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CutCornerShape(24.dp))
                .background(SurfaceDark)
                .border(2.dp, CyberGradient, CutCornerShape(24.dp))
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isSpinning) {
                        "ESCANEANDO MATRIZ..."
                    } else {
                        "OBJETIVO SELECCIONADO"
                    },
                    color = if (isSpinning) NeonCyan else NeonYellow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(BgBlack)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(CutCornerShape(topEnd = 20.dp, bottomStart = 20.dp))
                    .border(
                        2.dp,
                        if (isSpinning) NeonCyan else NeonPink,
                        CutCornerShape(topEnd = 20.dp, bottomStart = 20.dp)
                    )
            ) {
                AsyncImage(
                    model = currentGame.thumbnail,
                    contentDescription = currentGame.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, BgBlack.copy(alpha = 0.85f))
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .background(NeonPink, CutCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = currentGame.genre.uppercase(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = currentGame.title.uppercase(),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isFromVault) {
                    "Seleccionado de tu Bóveda Personal"
                } else {
                    "Sugerencia aleatoria del Catálogo Global"
                },
                color = TextMuted,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { onStartPlayingNow(currentGame) },
                enabled = !isSpinning,
                shape = CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = BgBlack
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "MARCAR 'EN CURSO' Y ABRIR",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { spinKey++ },
                    enabled = !isSpinning,
                    shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "OTRO GIRO",
                        color = NeonPink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                OutlinedButton(
                    onClick = { onOpenDetail(currentGame) },
                    enabled = !isSpinning,
                    shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "VER FICHA",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
