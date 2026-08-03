package com.souldcode.assignment.features.globalscoreboard.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souldcode.assignment.features.globalscoreboard.domain.repository.ScoreboardRepository
import com.souldcode.assignment.features.globalscoreboard.ui.contract.GlobalScoreboardEffect
import com.souldcode.assignment.features.globalscoreboard.ui.contract.GlobalScoreboardIntent
import com.souldcode.assignment.features.globalscoreboard.ui.contract.GlobalScoreboardState
import com.souldcode.assignment.features.globalscoreboard.ui.util.PAGE_SIZE
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GlobalScoreboardViewModel(private val repository: ScoreboardRepository) : ViewModel() {


    // 1. Single source of truth for the screen State
    private val _state = MutableStateFlow(GlobalScoreboardState())
    val state: StateFlow<GlobalScoreboardState> = _state.asStateFlow()

    // 2. Channel for one-off Side Effects (e.g. Toasts)
    private val _effect = Channel<GlobalScoreboardEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        // Automatically start observing the live Firestore top 50 players stream
        observeTopPlayers()
    }


    // 3. Interface to receive user actions (Intents) from the Compose UI
    fun sendIntent(intent: GlobalScoreboardIntent) {
        viewModelScope.launch {
            when (intent) {
                is GlobalScoreboardIntent.SeedData -> handleSeedData()
                is GlobalScoreboardIntent.SimulateUpdates -> handleSimulateUpdates()
                is GlobalScoreboardIntent.LoadNextPage -> handleLoadNextPage()
            }
        }
    }

    private suspend fun handleLoadNextPage() {
        val currentState = _state.value
        if (currentState.isLoadingNextPage || currentState.isLastPageReached || currentState.players.isEmpty()) return
        _state.update { it.copy(isLoadingNextPage = true) }

        try {
            val lastPlayerId = currentState.players.lastOrNull()?.id
            val nextPagePlayers = repository.getPlayersPage(limit = PAGE_SIZE, lastPlayerId = lastPlayerId)
            _state.update { state ->
                val updatedList = (state.players + nextPagePlayers)
                    .distinctBy { it.id }
                    .sortedByDescending { it.score }
                state.copy(
                    players = updatedList,
                    isLastPageReached = nextPagePlayers.size < PAGE_SIZE,
                    isLoadingNextPage = false
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
            _effect.send(GlobalScoreboardEffect.ShowToast("Failed to load next page: ${e.localizedMessage}"))
            _state.update { it.copy(isLoadingNextPage = false) }
        }

    }

    // 4. Observe real-time database flow
    private fun observeTopPlayers() {
        viewModelScope.launch {
            repository.getTopPlayer(50).collect { livePlayers ->
                _state.update { currentState ->
                    val currentList = currentState.players.toMutableList()
                    if (currentList.size >= livePlayers.size) {
                        for (i in livePlayers.indices) {
                            currentList[i] = livePlayers[i]
                        }
                    } else {
                        currentList.clear()
                        currentList.addAll(livePlayers)
                    }
                    val sortedList = currentList.distinctBy { it.id }.sortedByDescending { it.score }
                    currentState.copy(players = sortedList)
                }
            }
        }
    }

    // 5. Handle Seeding Intent
    private suspend fun handleSeedData() {
        if (_state.value.isSeeding) return

        _state.update { it.copy(isSeeding = true) }
        try {
            repository.addDummyDataToFirebase()
            _effect.send(GlobalScoreboardEffect.ShowToast("Players seeded successfully!"))
        } catch (e: Exception) {
            e.printStackTrace()
            _effect.send(GlobalScoreboardEffect.ShowToast("Seeding failed: ${e.localizedMessage}"))
        } finally {
            _state.update { it.copy(isSeeding = false) }
        }
    }

    // 6. Handle Score Simulation Intent
    private suspend fun handleSimulateUpdates() {
        if (_state.value.isSimulating) return

        _state.update { it.copy(isSimulating = true) }
        try {
            // Pass current top players to enable biased random updates on visible players
            val newScoreActivities = repository.simulateScoreUpdates(_state.value.players)

            _state.update { currentState ->
                // Prepend new activities and keep only the latest 15 logs
                val playerScoreUpdatedActivities =
                    (newScoreActivities + currentState.playerScoreActivities).take(15)
                currentState.copy(playerScoreActivities = playerScoreUpdatedActivities)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _effect.send(GlobalScoreboardEffect.ShowToast("Simulation failed: ${e.localizedMessage}"))
        } finally {
            _state.update { it.copy(isSimulating = false) }
        }
    }
}