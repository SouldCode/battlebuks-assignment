package com.souldcode.assignment.features.globalscoreboard.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.souldcode.assignment.ui.theme.ButtonOrange
import com.souldcode.assignment.ui.theme.ButtonPurple
import com.souldcode.assignment.ui.theme.CardGrey
import com.souldcode.assignment.ui.theme.TopBarColor

@Composable
fun ActionControlPanel(
    isSeeding: Boolean, isSimulating: Boolean, onSeedClick: () -> Unit, onSimulateClick: () -> Unit
) {
    val surfaceColor = CardGrey // Card BG
    val primaryGradient = Brush.horizontalGradient(
        colors = listOf(ButtonPurple, ButtonOrange)
    )

    Row(modifier = Modifier
        .fillMaxWidth()
        .background(color = TopBarColor)) {
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
                        Text("Simulate updates", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }


}
