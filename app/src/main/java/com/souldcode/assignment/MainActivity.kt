package com.souldcode.assignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.souldcode.assignment.features.globalscoreboard.data.repository.ScoreboardRepositoryImpl
import com.souldcode.assignment.features.globalscoreboard.ui.view.GlobalScoreboardScreen
import com.souldcode.assignment.features.globalscoreboard.ui.viewmodel.GlobalScoreboardViewModel
import com.souldcode.assignment.ui.theme.AssignmentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize Firebase (if not automatically initialized)
        FirebaseApp.initializeApp(this)

        // 2. Initialize the Firestore Repository
        val repository = ScoreboardRepositoryImpl(FirebaseFirestore.getInstance())

        // 3. Create the Custom ViewModel Factory to pass the repository dependency manually
        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(GlobalScoreboardViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return GlobalScoreboardViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        // 4. Retrieve the ViewModel using the factory
        val viewModel: GlobalScoreboardViewModel by viewModels { viewModelFactory }
        setContent {
            AssignmentTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GlobalScoreboardScreen(viewModel = viewModel)
                }
            }
        }
    }
}