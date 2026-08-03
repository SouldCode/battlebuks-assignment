package com.souldcode.assignment.features.globalscoreboard.ui.contract

sealed interface GlobalScoreboardEffect {

    data class ShowToast(val message: String) : GlobalScoreboardEffect

}