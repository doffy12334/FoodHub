package com.example.foodhub_app.feature.customer.splash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_app.feature.shared.auth.data.RemoteAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds


sealed interface SplashDestination{
    data object Loading : SplashDestination
    data object Login : SplashDestination
    data object Home : SplashDestination
    data object Admin : SplashDestination
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository : RemoteAuthRepository
) : ViewModel() {
    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Loading)
    val destination : StateFlow<SplashDestination> = _destination.asStateFlow()
    init {
        checkAuthStatus()
    }

     fun checkAuthStatus() {
        viewModelScope.launch {
            _destination.value = SplashDestination.Loading
            val startTime = System.currentTimeMillis()
            val result = repository.checkSession()

            val elapsedTime = System.currentTimeMillis() - startTime
            if (elapsedTime < 1200){
                delay((1200 - elapsedTime).milliseconds)
            }

            result.onSuccess { user ->
                if (user.role.equals("ADMIN", ignoreCase = true)){
                    _destination.value = SplashDestination.Admin
                } else{
                    _destination.value = SplashDestination.Home
                }
            }.onFailure {
                _destination.value = SplashDestination.Home // Chế độ guest không cần login
            }
        }
    }


}












