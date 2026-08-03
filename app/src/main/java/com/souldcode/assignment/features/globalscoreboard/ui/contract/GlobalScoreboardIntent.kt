package com.souldcode.assignment.features.globalscoreboard.ui.contract

sealed interface GlobalScoreboardIntent {

    object SeedData : GlobalScoreboardIntent
    object SimulateUpdates : GlobalScoreboardIntent

    object LoadNextPage : GlobalScoreboardIntent

}