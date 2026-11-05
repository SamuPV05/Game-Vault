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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.gamevault.ui.components.BgBlack
import com.example.gamevault.ui.components.CyberBottomNavigation
import com.example.gamevault.ui.components.CyberGradient
import com.example.gamevault.ui.components.NeonCyan
import com.example.gamevault.ui.components.NeonGreen
import com.example.gamevault.ui.components.NeonPink
import com.example.gamevault.ui.components.NeonYellow
import com.example.gamevault.ui.components.SurfaceDark
import com.example.gamevault.ui.components.TextMuted
import com.example.gamevault.viewmodel.CoverQuizUiState
import com.example.gamevault.viewmodel.CoverQuizViewModel
import com.example.gamevault.viewmodel.DuelViewModel

@Composable
fun ArcadeScreen(
    navController: NavHostController,
    vaultCount: Int,
    duelViewModel: DuelViewModel = viewModel(),
    coverQuizViewModel: CoverQuizViewModel = viewModel()
) {
    // 0 = Metacritic Duel, 1 = Mystery Cover (Adivina la Portada)
    var selectedGameMode by remember { mutableIntStateOf(0) }
    val haptic = LocalHapticFeedback.current

    Scaffold(
        containerColor = BgBlack,
        topBar = {
            ArcadeModeSelectorBar(
                selectedMode = selectedGameMode,
                onSelectMode = { mode ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    selectedGameMode = mode
                }
            )
        },
        bottomBar = {
            CyberBottomNavigation(
                currentRoute = "duel",
                vaultCount = vaultCount,
                onNavigate = { route ->
                    if (route != "duel") {
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
        if (selectedGameMode == 0) {
            DuelScreen(
                modifier = Modifier.padding(paddingValues),
                viewModel = duelViewModel
            )
        } else {
            MysteryCoverScreen(
                modifier = Modifier.padding(paddingValues),
                viewModel = coverQuizViewModel
            )
        }
    }
}

@Composable
private fun ArcadeModeSelectorBar(
    selectedMode: Int,
    onSelectMode: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val isDuel = selectedMode == 0
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp))
                .background(if (isDuel) NeonPink else BgBlack)
                .border(
                    1.dp,
                    if (isDuel) NeonPink else NeonPink.copy(alpha = 0.4f),
                    CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp)
                )
                .clickable { onSelectMode(0) }
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = null,
                    tint = if (isDuel) Color.White else NeonPink,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "METACRITIC DUEL",
                    color = if (isDuel) Color.White else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }

        val isCover = selectedMode == 1
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp))
                .background(if (isCover) NeonCyan else BgBlack)
                .border(
                    1.dp,
                    if (isCover) NeonCyan else NeonCyan.copy(alpha = 0.4f),
                    CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp)
                )
                .clickable { onSelectMode(1) }
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    tint = if (isCover) BgBlack else NeonCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "MYSTERY COVER",
                    color = if (isCover) BgBlack else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun MysteryCoverScreen(
    modifier: Modifier = Modifier,
    viewModel: CoverQuizViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgBlack)
    ) {
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NeonCyan)
                }
            }

            uiState.isGameOver -> {
                CoverQuizGameOverView(
                    uiState = uiState,
                    onRetry = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.startNewGame()
                    }
                )
            }

            uiState.targetGame != null -> {
                CoverQuizArena(
                    uiState = uiState,
                    onEnhanceClarity = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.enhanceClarity()
                    },
                    onSelectOption = { gameId ->
                        val isCorrect = gameId == uiState.targetGame?.id
                        haptic.performHapticFeedback(
                            if (isCorrect) HapticFeedbackType.TextHandleMove else HapticFeedbackType.LongPress
                        )
                        viewModel.onSelectOption(gameId)
                    }
                )
            }
        }
    }
}

@Composable
private fun CoverQuizArena(
    uiState: CoverQuizUiState,
    onEnhanceClarity: () -> Unit,
    onSelectOption: (Int) -> Unit
) {
    val target = uiState.targetGame ?: return
    val blurDp by animateFloatAsState(
        targetValue = when (uiState.blurLevel) {
            3 -> 26f
            2 -> 14f
            1 -> 6f
            else -> 0f
        },
        animationSpec = tween(350),
        label = "coverBlurAnim"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Barra de Estado Superior: Vidas, Multiplicador y Puntos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceDark)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                for (i in 1..3) {
                    Icon(
                        imageVector = if (i <= uiState.lives) {
                            Icons.Default.Favorite
                        } else {
                            Icons.Default.FavoriteBorder
                        },
                        contentDescription = null,
                        tint = if (i <= uiState.lives) NeonPink else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                }
            }

            Text(
                text = "PUNTOS: ${uiState.score}",
                color = NeonCyan,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = NeonYellow,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = uiState.highScore.toString(),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Visor de Portada Encriptada / Desenfocada
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(CutCornerShape(topStart = 24.dp, bottomEnd = 24.dp))
                .border(2.dp, CyberGradient, CutCornerShape(topStart = 24.dp, bottomEnd = 24.dp))
        ) {
            AsyncImage(
                model = target.backgroundImage,
                contentDescription = "Mystery Cover",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(blurDp.dp)
            )

            // Escáner de oscurecimiento suave cuando está muy distorsionada
            if (!uiState.isRevealed) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            BgBlack.copy(
                                alpha = when (uiState.blurLevel) {
                                    3 -> 0.35f
                                    2 -> 0.18f
                                    else -> 0.05f
                                }
                            )
                        )
                )
            }

            // Etiqueta de Multiplicador Actual
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BgBlack.copy(alpha = 0.82f))
                    .border(1.dp, NeonYellow, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = if (uiState.isRevealed) {
                        "SEÑAL DECODIFICADA"
                    } else {
                        "BONO DE LENTE: +${uiState.blurLevel} PTS"
                    },
                    color = NeonYellow,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Botón para Enfocar Lente (pista)
            if (!uiState.isRevealed && uiState.blurLevel > 1) {
                OutlinedButton(
                    onClick = onEnhanceClarity,
                    shape = CutCornerShape(10.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .background(BgBlack.copy(alpha = 0.75f), CutCornerShape(10.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.CenterFocusStrong,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ENFOCAR LENTE (-1 PT)",
                        color = NeonCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "¿QUÉ JUEGO SE OCULTA EN LA CAPTURA?",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 4 Opciones de Respuesta
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            uiState.options.forEach { option ->
                val isSelected = uiState.selectedOptionId == option.id
                val isCorrectOption = option.id == target.id

                val containerColor = when {
                    !uiState.isRevealed -> SurfaceDark
                    isCorrectOption -> NeonGreen
                    isSelected -> NeonPink
                    else -> SurfaceDark.copy(alpha = 0.5f)
                }

                val textColor = when {
                    !uiState.isRevealed -> Color.White
                    isCorrectOption -> BgBlack
                    isSelected -> Color.White
                    else -> TextMuted
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp))
                        .background(containerColor)
                        .border(
                            1.dp,
                            if (uiState.isRevealed && isCorrectOption) {
                                NeonGreen
                            } else {
                                NeonCyan.copy(alpha = 0.35f)
                            },
                            CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp)
                        )
                        .clickable(enabled = !uiState.isRevealed) {
                            onSelectOption(option.id)
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = option.name.uppercase(),
                        color = textColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun CoverQuizGameOverView(
    uiState: CoverQuizUiState,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CutCornerShape(24.dp))
                .background(SurfaceDark)
                .border(2.dp, CyberGradient, CutCornerShape(24.dp))
                .padding(26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Visibility,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(52.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "SEÑAL PERDIDA",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Agotaste tus 3 intentos de reconocimiento visual.",
                color = TextMuted,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CutCornerShape(12.dp))
                        .background(BgBlack)
                        .padding(vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "PUNTUACIÓN",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = uiState.score.toString(),
                        color = NeonCyan,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CutCornerShape(12.dp))
                        .background(BgBlack)
                        .padding(vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "MEJOR MARCA",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = uiState.highScore.toString(),
                        color = NeonYellow,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRetry,
                shape = CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = BgBlack
                ),
                contentPadding = PaddingValues(vertical = 14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "REINICIAR ESCÁNER",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    letterSpacing = 1.5.sp
                )
            }
        }
    }
}
