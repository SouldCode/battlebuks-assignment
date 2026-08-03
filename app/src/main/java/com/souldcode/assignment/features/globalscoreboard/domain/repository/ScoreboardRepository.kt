package com.souldcode.assignment.features.globalscoreboard.domain.repository

import com.souldcode.assignment.features.globalscoreboard.domain.model.Player
import com.souldcode.assignment.features.globalscoreboard.domain.model.PlayerScoreActivity
import kotlinx.coroutines.flow.Flow

interface ScoreboardRepository {

    //get real time player from firebase and collect
    fun getTopPlayer(limit: Int): Flow<List<Player>>

    //using thi function will add dummyData to firebase
    suspend fun addDummyDataToFirebase()

    //will update top 10 player and 10 from anywhere between database
    suspend fun simulateScoreUpdates(currentTopPlayer: List<Player>): List<PlayerScoreActivity>

}