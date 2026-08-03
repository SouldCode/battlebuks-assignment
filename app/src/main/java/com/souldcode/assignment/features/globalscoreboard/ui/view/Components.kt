package com.souldcode.assignment.features.globalscoreboard.ui.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.souldcode.assignment.features.globalscoreboard.domain.model.Player
import com.souldcode.assignment.ui.theme.ACCENT_BRONZE
import com.souldcode.assignment.ui.theme.ACCENT_GOLD
import com.souldcode.assignment.ui.theme.ACCENT_SILVER
import com.souldcode.assignment.ui.theme.ButtonOrange
import com.souldcode.assignment.ui.theme.ButtonPurple
import com.souldcode.assignment.ui.theme.COLOR_FLASH_GREEN
import com.souldcode.assignment.ui.theme.CardGrey
import com.souldcode.assignment.ui.theme.DEFAULT_BORDER_COLOR
import com.souldcode.assignment.ui.theme.Row_ITEM_SURFACE
import com.souldcode.assignment.ui.theme.TopBarColor
import kotlinx.coroutines.delay

@Composable
fun ActionControlPanel(
    isSeeding: Boolean, isSimulating: Boolean, onSeedClick: () -> Unit, onSimulateClick: () -> Unit
) {
    val surfaceColor = CardGrey // Card BG
    val primaryGradient = Brush.horizontalGradient(
        colors = listOf(ButtonPurple, ButtonOrange)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = TopBarColor)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TopBarColor),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF2C2D4A), RoundedCornerShape(16.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Button 1: Seed 1,000 Players
                Button(
                    onClick = onSeedClick,
                    enabled = !isSeeding,
                    colors = ButtonDefaults.buttonColors(containerColor = CardGrey),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isSeeding) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp), color = Color.White
                        )
                    } else {
                        Text("Add Players", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Button 2: Simulate updates (Gradient background)
                Button(
                    onClick = onSimulateClick,
                    enabled = !isSimulating,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .weight(1f)
                        .background(primaryGradient, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isSimulating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp), color = Color.White
                        )
                    } else {
                        Text(
                            "Simulate updates",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }


}


@Composable
fun TableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "RANK",
            color = Color.Gray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(50.dp),
            textAlign = TextAlign.Center
        )
        Text(
            text = "PLAYER NAME",
            color = Color.Gray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "SCORE",
            color = Color.Gray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.End
        )

    }
}

@Composable
fun PlayerRowItem(
    player: Player,
    rank: Int,
    modifier: Modifier = Modifier
) {

    var lastScore by remember { mutableStateOf(player.score) }
    var flashTrigger by remember { mutableStateOf(false) }

    LaunchedEffect(player.score) {
        if (player.score > lastScore) {
            flashTrigger = true
            delay(600) // Pulse duration
            flashTrigger = false
        }
        lastScore = player.score
    }

// 2. Pulse the border width from 1.dp to 2.dp
    val borderWidth by animateDpAsState(
        targetValue = if (flashTrigger) 2.dp else 1.dp,
        animationSpec = tween(durationMillis = 200),
        label = "BorderWidthPulse"
    )
    // 3. Pulse the border color from dark slate to neon green
    val borderColor by animateColorAsState(
        targetValue = if (flashTrigger) COLOR_FLASH_GREEN else DEFAULT_BORDER_COLOR,
        animationSpec = tween(durationMillis = 200),
        label = "BorderColorPulse"
    )
    // 4. Smoothly transition score text color
    val scoreTextColor by animateColorAsState(
        targetValue = if (flashTrigger) COLOR_FLASH_GREEN else Color.White,
        animationSpec = tween(durationMillis = 150),
        label = "ScoreTextFlash"
    )
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Row_ITEM_SURFACE), // Static premium BG
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = borderWidth, // <-- Animated Width
                color = borderColor, // <-- Animated Color
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Badge Column
            Box(
                modifier = Modifier
                    .width(50.dp)
                    .align(Alignment.CenterVertically),
                contentAlignment = Alignment.Center
            ) {
                val badgeColor = when (rank) {
                    1 -> ACCENT_GOLD
                    2 -> ACCENT_SILVER
                    3 -> ACCENT_BRONZE
                    else -> Color.Transparent
                }
                if (badgeColor != Color.Transparent) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(badgeColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = rank.toString(),
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    Text(
                        text = "#$rank",
                        color = Color.LightGray,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
            // Player Name Column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = player.name,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
            // Score Column
            Text(
                text = player.score.toString(),
                color = scoreTextColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.width(80.dp)
            )
        }
    }
}