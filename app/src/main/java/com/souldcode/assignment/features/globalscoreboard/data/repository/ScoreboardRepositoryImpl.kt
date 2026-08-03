package com.souldcode.assignment.features.globalscoreboard.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.souldcode.assignment.features.globalscoreboard.domain.model.Player
import com.souldcode.assignment.features.globalscoreboard.domain.model.PlayerScoreActivity
import com.souldcode.assignment.features.globalscoreboard.domain.repository.ScoreboardRepository
import com.souldcode.assignment.features.globalscoreboard.ui.util.FIREBASE_DOC_PATH_PREFIX
import com.souldcode.assignment.features.globalscoreboard.ui.util.FIREBASE_PLAYER_COLLECTION
import com.souldcode.assignment.features.globalscoreboard.ui.util.TOT_PLAYER_LIMIT
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class ScoreboardRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ScoreboardRepository {


    override fun getTopPlayer(limit: Int): Flow<List<Player>> = callbackFlow {

        //building query to get top player with Descending
        val query = firestore.collection(FIREBASE_PLAYER_COLLECTION)
            .orderBy("score", Query.Direction.DESCENDING).limit(limit.toLong())

        val listener = query.addSnapshotListener { snapshots, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshots != null) {
                //parsing snapshot into data class
                val players = snapshots.toObjects(Player::class.java)
                trySend(players)
            }
        }
        // Remove listener when flow collection is closed
        awaitClose { listener }


    }

    override suspend fun addDummyDataToFirebase() {
        val collection = firestore.collection(FIREBASE_PLAYER_COLLECTION)

        // Firestore batch limit is 500 operations per batch so wil create 2 batch
        for (batchIndex in 0..1) {
            val batch = firestore.batch()
            for (i in 1..TOT_PLAYER_LIMIT) {
                val playerNumber = batchIndex * TOT_PLAYER_LIMIT + i
                val doc_ref = collection.document(FIREBASE_DOC_PATH_PREFIX + "_$playerNumber")
                val player = Player(
                    id = FIREBASE_DOC_PATH_PREFIX + "_$playerNumber",
                    name = "$FIREBASE_DOC_PATH_PREFIX $playerNumber",
                    score = Random.nextInt(0, 10)
                )
                batch.set(doc_ref, player)
            }
            batch.commit().await()
        }

    }

    override suspend fun simulateScoreUpdates(currentTopPlayer: List<Player>): List<PlayerScoreActivity> {

        val collection = firestore.collection(FIREBASE_PLAYER_COLLECTION)
        val batch = firestore.batch()
        val activities = mutableListOf<PlayerScoreActivity>()

        // pick 10 player from top 50 if list is available
        val selectedTopPlayer = if (currentTopPlayer.isNotEmpty()) {
            currentTopPlayer.shuffled().take(10)
        } else {
            emptyList()
        }

        // 2. Pick remaining players from ranks 51 to 1,000 (player_51 to player_1000)
        val needFromBottom = 20 - selectedTopPlayer.size
        val selectedBottomIds = mutableSetOf<String>()
        while (selectedBottomIds.size < needFromBottom) {
            val randomNum = Random.nextInt(21, TOT_PLAYER_LIMIT)
            val id = "$FIREBASE_DOC_PATH_PREFIX" + "_$randomNum"
            if (selectedTopPlayer.none() { it.id == id }) {
                selectedBottomIds.add(id)
            }
        }

        // 3. Queue increments into the write batch and log activities
        for (player in selectedTopPlayer) {
            val docRef = collection.document(player.id)
            val increment = Random.nextInt(1, 10)
            batch.update(docRef, "score", FieldValue.increment(increment.toLong()))
            activities.add(PlayerScoreActivity(playerName = player.name, increment = increment))
        }

        for (playerIds in selectedBottomIds) {
            val docRef = collection.document(playerIds)
            val increment = Random.nextInt(1, 10)
            batch.update(docRef, "score", FieldValue.increment(increment.toLong()))
            val formattedName = playerIds.replace("player_", "Player ")
            activities.add(PlayerScoreActivity(playerName = formattedName, increment = increment))
        }

        // 4. Commit all 20 updates in a single transaction (atomic write)
        batch.commit().await()
        return activities
    }

    override suspend fun getPlayersPage(
        limit: Int, lastPlayerId: String?
    ): List<Player> {

        var query = firestore.collection(FIREBASE_PLAYER_COLLECTION)
            .orderBy("score", Query.Direction.DESCENDING)
            .limit(limit.toLong())
        if (lastPlayerId != null) {
            // fetch last visible document
            val lastDoc = firestore.collection(FIREBASE_PLAYER_COLLECTION)
                .document(lastPlayerId)
                .get()
                .await()
            if (lastDoc.exists()) {
                // 2. start query after last visible doc
                query = query.startAfter(lastDoc)
            }
        }

        // 3. One-time database fetch
        val snapshot = query.get().await()
        return snapshot.toObjects(Player::class.java)

    }
}