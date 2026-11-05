package com.example.gamevault.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gamevault.ui.components.BgBlack
import com.example.gamevault.ui.components.CyberGradient
import com.example.gamevault.ui.components.NeonCyan
import com.example.gamevault.ui.components.NeonPink
import com.example.gamevault.ui.components.SurfaceDark
import kotlinx.coroutines.delay

@Composable
fun GameSplashScreen(navController: NavHostController) {
    LaunchedEffect(Unit) {
        delay(2500L)
        navController.navigate("home") { popUpTo("splash") { inclusive = true } }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CutCornerShape(24.dp))
                    .border(2.dp, CyberGradient, CutCornerShape(24.dp))
                    .background(SurfaceDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(60.dp)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "GAME VAULT",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 6.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .height(2.dp)
                    .width(100.dp)
                    .background(CyberGradient)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "SYSTEM INITIALIZING...",
                color = NeonPink,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )
        }
    }
}
