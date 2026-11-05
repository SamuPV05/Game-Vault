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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.gamevault.Game
import com.example.gamevault.model.BacklogEntry
import com.example.gamevault.model.BacklogStatus
import com.example.gamevault.ui.components.BgBlack
import com.example.gamevault.ui.components.CyberGradient
import com.example.gamevault.ui.components.CyberStat
import com.example.gamevault.ui.components.NeonCyan
import com.example.gamevault.ui.components.NeonPink
import com.example.gamevault.ui.components.NeonYellow
import com.example.gamevault.ui.components.SurfaceDark
import com.example.gamevault.ui.components.TextMuted
import com.example.gamevault.ui.components.statusColor

@Composable
fun GameDetailScreen(
    navController: NavHostController,
    game: Game,
    backlogEntry: BacklogEntry? = null,
    onToggleFavorite: () -> Unit = {},
    onStatusChange: (BacklogStatus) -> Unit = {},
    onRatingChange: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val isFavorite = backlogEntry != null

    Scaffold(
        containerColor = BgBlack,
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .clip(CutCornerShape(topStart = 20.dp, bottomEnd = 20.dp))
                    .background(CyberGradient)
                    .clickable {
                        runCatching {
                            val url = game.gameUrl.trim()
                            if (url.isNotEmpty()) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Enlace no disponible para este juego",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }.onFailure {
                            Toast.makeText(
                                context,
                                "No se pudo abrir el enlace del juego",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "INITIATE",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
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
                            .background(Brush.verticalGradient(listOf(Color.Transparent, BgBlack)))
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .clip(CutCornerShape(12.dp))
                                .background(BgBlack.copy(alpha = 0.65f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Back",
                                tint = NeonCyan,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier
                                .clip(CutCornerShape(12.dp))
                                .background(BgBlack.copy(alpha = 0.65f))
                                .border(
                                    1.dp,
                                    if (isFavorite) NeonPink else NeonCyan.copy(alpha = 0.5f),
                                    CutCornerShape(12.dp)
                                )
                        ) {
                            Icon(
                                imageVector = if (isFavorite) {
                                    Icons.Filled.Favorite
                                } else {
                                    Icons.Outlined.FavoriteBorder
                                },
                                contentDescription = "Guardar en Bóveda",
                                tint = if (isFavorite) NeonPink else Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(
                        text = game.title.uppercase(),
                        color = Color.White,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 38.sp
                    )
                    Text(
                        text = "DEV // ${game.developer.uppercase()} • PUB // ${game.publisher.uppercase()}",
                        color = NeonPink,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    // Panel interactivo de Backlog Matrix dentro del Detalle
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                            .clip(CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp))
                            .background(SurfaceDark)
                            .border(
                                1.dp,
                                backlogEntry?.status?.statusColor() ?: NeonCyan.copy(alpha = 0.35f),
                                CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp)
                            )
                            .padding(14.dp)
                    ) {
                        Text(
                            text = if (backlogEntry != null) {
                                "ESTADO EN TU BÓVEDA // ${backlogEntry.status.label}"
                            } else {
                                "CLASIFICAR EN TU BACKLOG MATRIX"
                            },
                            color = backlogEntry?.status?.statusColor() ?: NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            BacklogStatus.entries.forEach { status ->
                                val isSelected = backlogEntry?.status == status
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
                                        .padding(vertical = 8.dp),
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

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val currentRating = backlogEntry?.personalRating ?: 0
                            Text(
                                text = if (currentRating > 0) {
                                    "TU CALIFICACIÓN: $currentRating/10"
                                } else {
                                    "CALIFICA ESTE JUEGO:"
                                },
                                color = if (currentRating > 0) NeonYellow else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                for (star in 1..5) {
                                    val score = star * 2
                                    val filled = currentRating >= score
                                    Icon(
                                        imageVector = if (filled) {
                                            Icons.Filled.Star
                                        } else {
                                            Icons.Outlined.StarBorder
                                        },
                                        contentDescription = null,
                                        tint = if (filled) NeonYellow else TextMuted,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clickable { onRatingChange(score) }
                                            .padding(2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CyberStat("GENRE", game.genre)
                        CyberStat("RELEASE", game.releaseDate)
                        CyberStat("PLATFORM", game.platform.take(10))
                    }

                    Text(
                        text = "TRANSMISSION LOG",
                        color = NeonCyan,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(bottom = 12.dp, top = 12.dp),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = game.shortDescription,
                        color = TextMuted,
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(bottom = 100.dp)
                    )
                }
            }
        }
    }
}
