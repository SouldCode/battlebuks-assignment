package com.souldcode.assignment.features.globalscoreboard.domain.model

data class PlayerScoreActivity(
    val playerName: String,
    val increment: Int,
    val timestamp: Long = System.currentTimeMillis()
)
