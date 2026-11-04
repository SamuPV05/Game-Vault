package com.example.gamevault.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gamevault.BgBlack
import com.example.gamevault.CyberGradient
import com.example.gamevault.NeonCyan
import com.example.gamevault.NeonPink
import com.example.gamevault.SurfaceDark
import com.example.gamevault.TextMuted
import com.example.gamevault.model.GameDuelItem
import com.example.gamevault.viewmodel.DuelUiState
import com.example.gamevault.viewmodel.DuelViewModel

private val MetacriticGreen = Color(0xFF00E676)
private val MetacriticYellow = Color(0xFFFFD600)
private val HigherButtonColor = Color(0xFF00C853)
private val LowerButtonColor = Color(0xFFFF1744)

@Composable
fun DuelScreen(
    modifier: Modifier = Modifier,
    viewModel: DuelViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = BgBlack
    ) {
        when {
            uiState.isLoading -> {
                DuelLoadingView()
            }

            uiState.isGameOver -> {
                DuelGameOverView(
                    uiState = uiState,
                    onRetry = { viewModel.loadGames() }
                )
            }

            uiState.currentGame != null && uiState.challengerGame != null -> {
                DuelArenaContent(
                    uiState = uiState,
                    onHigherClick = { viewModel.onGuess(guessHigher = true) },
                    onLowerClick = { viewModel.onGuess(guessHigher = false) }
                )
            }

            else -> {
                DuelEmptyErrorView(onRetry = { viewModel.loadGames() })
            }
        }
    }
}

@Composable
private fun DuelArenaContent(
    uiState: DuelUiState,
    onHigherClick: () -> Unit,
    onLowerClick: () -> Unit
) {
    val currentGame = uiState.currentGame ?: return
    val challengerGame = uiState.challengerGame ?: return

    Box(modifier = Modifier.fillMaxSize()) {
        // Pantalla dividida verticalmente al 50% (Column con weight(1f))
        Column(modifier = Modifier.fillMaxSize()) {
            TopGameCard(
                game = currentGame,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(CyberGradient)
            )

            BottomChallengerCard(
                game = challengerGame,
                isRevealed = uiState.isRevealed,
                onHigherClick = onHigherClick,
                onLowerClick = onLowerClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }

        // Centro: Badge flotante con la racha actual (ScoreBadge)
        ScoreBadge(
            score = uiState.score,
            highScore = uiState.highScore,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun TopGameCard(
    game: GameDuelItem,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        AsyncImage(
            model = game.backgroundImage,
            contentDescription = game.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Overlay oscuro para legibilidad
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            BgBlack.copy(alpha = 0.65f),
                            BgBlack.copy(alpha = 0.55f),
                            BgBlack.copy(alpha = 0.88f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "CAMPEÓN ACTUAL",
                color = NeonCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = game.name,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "tiene un Metascore de",
                color = TextMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Metascore numérico en verde destacado
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(BgBlack.copy(alpha = 0.75f))
                    .border(2.dp, MetacriticGreen, RoundedCornerShape(16.dp))
                    .padding(horizontal = 28.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = game.metacritic.toString(),
                    color = MetacriticGreen,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun BottomChallengerCard(
    game: GameDuelItem,
    isRevealed: Boolean,
    onHigherClick: () -> Unit,
    onLowerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedScore by animateIntAsState(
        targetValue = if (isRevealed) game.metacritic else 0,
        animationSpec = tween(durationMillis = 650),
        label = "challengerMetacriticAnimation"
    )

    Box(modifier = modifier) {
        AsyncImage(
            model = game.backgroundImage,
            contentDescription = game.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Overlay oscuro
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            BgBlack.copy(alpha = 0.88f),
                            BgBlack.copy(alpha = 0.60f),
                            BgBlack.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "RETADOR",
                color = NeonPink,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = game.name,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "¿Su Metascore es más alto o más bajo?",
                color = TextMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Indicador "?" o número animado si isRevealed == true
            AnimatedContent(
                targetState = isRevealed,
                transitionSpec = {
                    fadeIn(tween(250)) togetherWith fadeOut(tween(200))
                },
                label = "scoreRevealTransition"
            ) { revealed ->
                val accentColor = if (revealed) MetacriticGreen else MetacriticYellow
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(BgBlack.copy(alpha = 0.78f))
                        .border(2.dp, accentColor, RoundedCornerShape(16.dp))
                        .padding(horizontal = 28.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (revealed) animatedScore.toString() else "?",
                        color = accentColor,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dos botones de acción rápida ("MÁS ALTO" y "MÁS BAJO")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Button(
                    onClick = onHigherClick,
                    enabled = !isRevealed,
                    shape = CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HigherButtonColor,
                        contentColor = Color.White,
                        disabledContainerColor = HigherButtonColor.copy(alpha = 0.4f),
                        disabledContentColor = Color.White.copy(alpha = 0.6f)
                    ),
                    contentPadding = PaddingValues(vertical = 14.dp, horizontal = 12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MÁS ALTO ▲",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                }

                Button(
                    onClick = onLowerClick,
                    enabled = !isRevealed,
                    shape = CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LowerButtonColor,
                        contentColor = Color.White,
                        disabledContainerColor = LowerButtonColor.copy(alpha = 0.4f),
                        disabledContentColor = Color.White.copy(alpha = 0.6f)
                    ),
                    contentPadding = PaddingValues(vertical = 14.dp, horizontal = 12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MÁS BAJO ▼",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ScoreBadge(
    score: Int,
    highScore: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(SurfaceDark.copy(alpha = 0.96f))
            .border(2.dp, CyberGradient, CircleShape)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "RACHA: ",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = score.toString(),
                color = NeonCyan,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
        }

        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(NeonPink),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "VS",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = MetacriticYellow,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = highScore.toString(),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun DuelGameOverView(
    uiState: DuelUiState,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgBlack)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CutCornerShape(24.dp))
                .background(SurfaceDark)
                .border(2.dp, CyberGradient, CutCornerShape(24.dp))
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.SportsEsports,
                contentDescription = null,
                tint = NeonPink,
                modifier = Modifier.size(56.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "GAME OVER",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "¡Fallaste la predicción de Metascore!",
                color = TextMuted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Resumen del último duelo
            if (uiState.currentGame != null && uiState.challengerGame != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(BgBlack)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.currentGame.name,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${uiState.currentGame.metacritic}",
                            color = MetacriticGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.challengerGame.name,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${uiState.challengerGame.metacritic}",
                            color = NeonPink,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Tarjetas de Puntaje Final y Récord
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CutCornerShape(12.dp))
                        .background(BgBlack)
                        .border(1.dp, NeonCyan.copy(alpha = 0.5f), CutCornerShape(12.dp))
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "PUNTAJE FINAL",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = uiState.score.toString(),
                        color = NeonCyan,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CutCornerShape(12.dp))
                        .background(BgBlack)
                        .border(1.dp, MetacriticYellow.copy(alpha = 0.6f), CutCornerShape(12.dp))
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "RÉCORD SESIÓN",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = uiState.highScore.toString(),
                        color = MetacriticYellow,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onRetry,
                shape = CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = BgBlack
                ),
                contentPadding = PaddingValues(vertical = 16.dp, horizontal = 24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "JUGAR DE NUEVO",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}

@Composable
private fun DuelLoadingView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = NeonCyan)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "CARGANDO DUELO METACRITIC...",
                color = NeonCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
private fun DuelEmptyErrorView(onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgBlack)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No se pudieron cargar suficientes juegos con Metascore.",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = onRetry) {
                Text("REINTENTAR", color = NeonCyan)
            }
        }
    }
}
