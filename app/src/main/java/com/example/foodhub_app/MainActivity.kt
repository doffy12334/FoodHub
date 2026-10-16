package com.example.foodhub_app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.foodhub_app.navigation.AppNavGraph
import com.example.foodhub_app.ui.theme.Foodhub_AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var pendingDeepLink by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        pendingDeepLink = intent?.data
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Foodhub_AppTheme {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    deepLinkUri = pendingDeepLink,
                    onDeepLinkConsumed = { pendingDeepLink = null }
                )
            }
        }
    }
}

