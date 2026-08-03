package com.souldcode.assignment.features.globalscoreboard.ui.contract

import com.souldcode.assignment.features.globalscoreboard.domain.model.Player
import com.souldcode.assignment.features.globalscoreboard.domain.model.PlayerScoreActivity


//contain everything from screen
data class GlobalScoreboardState(
    val players: List<Player> = emptyList(),
    val playerScoreActivities: List<PlayerScoreActivity> = emptyList(),
    val isSeeding: Boolean = false,
    val isSimulating: Boolean = false,
    val isLoadingNextPage: Boolean = false,
    val isLastPageReached: Boolean = false
)