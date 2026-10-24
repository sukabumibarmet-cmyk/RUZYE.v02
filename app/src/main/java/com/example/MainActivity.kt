package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.chat.ChatScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.theme.RuzieAiTheme
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.ChatViewModel
import com.example.ui.viewmodel.SettingsViewModel

enum class AppScreen {
    SPLASH,
    AUTH,
    CHAT,
    SETTINGS
}

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by settingsViewModel.themeMode.collectAsState()
            val currentUser by authViewModel.currentUser.collectAsState()

            var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }

            RuzieAiTheme(themeMode = themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Crossfade(
                        targetState = currentScreen,
                        animationSpec = tween(300),
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            AppScreen.SPLASH -> {
                                SplashScreen(
                                    isLoggedIn = currentUser != null,
                                    onNavigateNext = { hasSession ->
                                        currentScreen = if (hasSession) AppScreen.CHAT else AppScreen.AUTH
                                    }
                                )
                            }

                            AppScreen.AUTH -> {
                                AuthScreen(
                                    authViewModel = authViewModel,
                                    onAuthSuccess = {
                                        currentScreen = AppScreen.CHAT
                                    }
                                )
                            }

                            AppScreen.CHAT -> {
                                currentUser?.let { user ->
                                    ChatScreen(
                                        user = user,
                                        chatViewModel = chatViewModel,
                                        onNavigateSettings = {
                                            currentScreen = AppScreen.SETTINGS
                                        }
                                    )
                                } ?: run {
                                    // Fallback if user is null
                                    currentScreen = AppScreen.AUTH
                                }
                            }

                            AppScreen.SETTINGS -> {
                                BackHandler {
                                    currentScreen = AppScreen.CHAT
                                }
                                currentUser?.let { user ->
                                    SettingsScreen(
                                        user = user,
                                        settingsViewModel = settingsViewModel,
                                        chatViewModel = chatViewModel,
                                        onNavigateBack = {
                                            currentScreen = AppScreen.CHAT
                                        },
                                        onLogout = {
                                            authViewModel.logout()
                                            currentScreen = AppScreen.AUTH
                                        }
                                    )
                                } ?: run {
                                    currentScreen = AppScreen.AUTH
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
