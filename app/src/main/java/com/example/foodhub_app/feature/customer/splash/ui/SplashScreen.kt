package com.example.foodhub_app.feature.customer.splash.ui

import androidx.compose.foundation.Image
import com.example.foodhub_app.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodhub_app.feature.customer.splash.viewmodel.SplashDestination
import com.example.foodhub_app.feature.customer.splash.viewmodel.SplashViewModel
import com.example.foodhub_app.ui.theme.AppBackground
import com.example.foodhub_app.ui.theme.Brand
import com.example.foodhub_app.ui.theme.Foodhub_AppTheme


@Composable
fun SplashRoute(
    onNavigateToHome: () -> Unit,
    onNavigateToAdmin: ()->Unit,
    onNavigateToLogin:()-> Unit,
    viewModel : SplashViewModel = hiltViewModel()
){
    val destination by viewModel.destination.collectAsStateWithLifecycle()

    LaunchedEffect(destination) {
        when(destination){
            SplashDestination.Home -> onNavigateToHome()
            SplashDestination.Admin -> onNavigateToAdmin()
            SplashDestination.Login -> onNavigateToLogin()
            SplashDestination.Loading -> Unit
        }
    }
    SplashScreen()
}


@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.foodhub_logo),
                contentDescription = "Foodhub Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Foodhub",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Brand
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Thưởng thức món ngon mỗi ngày",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                color = Brand,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SplashScreenPreview() {
    Foodhub_AppTheme {
        SplashScreen()
    }
}


