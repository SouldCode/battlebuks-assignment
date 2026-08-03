package com.souldcode.assignment.features.globalscoreboard.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.souldcode.assignment.features.globalscoreboard.ui.contract.GlobalScoreboardIntent
import com.souldcode.assignment.features.globalscoreboard.ui.viewmodel.GlobalScoreboardViewModel
import com.souldcode.assignment.ui.theme.TopBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalScoreboardScreen(
    viewModel: GlobalScoreboardViewModel, modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "GLOBAL SCOREBOARD",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = Color.White
                    )
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TopBarColor
                )
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = TopBarColor)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            ActionControlPanel(
                isSeeding = state.isSeeding,
                isSimulating = state.isSimulating,
                onSeedClick = { viewModel.sendIntent(GlobalScoreboardIntent.SeedData) },
                onSimulateClick = { viewModel.sendIntent(GlobalScoreboardIntent.SimulateUpdates) })
            Spacer(modifier = Modifier.height(16.dp))
            TableHeader()
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (state.players.isEmpty()) {
                    // Show message if DB is empty
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No players found.\nPlease click 'Seed 1,000 Players' to initialize.",
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(32.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = state.players,
                            key = { player -> player.id }
                        ) { player ->
                            val rank = state.players.indexOf(player) + 1
                            // 3. We will build the individual list items inside here next!
                            PlayerRowItem(player = player, rank = rank)
                        }
                    }
                }
            }
        }
    }
}
