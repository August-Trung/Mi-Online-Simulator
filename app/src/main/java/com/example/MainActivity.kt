package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.presentation.screens.*
import com.example.presentation.viewmodel.GameViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by viewModel.gameSettings.collectAsState(initial = null)
            val isDarkMode = settings?.darkModeEnabled == true

            MyApplicationTheme(darkTheme = isDarkMode) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val currentScreen by viewModel.currentScreen.collectAsState()

                    // Type-safe Screen router that feeds unified Viewmodel state to all 24 sub-games
                    when (currentScreen) {
                        "splash" -> SplashScreen(viewModel = viewModel)
                        "login" -> LoginScreen(viewModel = viewModel)
                        
                        // Main Dashboard containing HomeScreen, ProfileScreen, SettingsScreen, Shop, and Inventory sub-tabs
                        "home", "shop", "inventory", "social", "settings" -> {
                            HomeScreen(
                                viewModel = viewModel,
                                bottomNavSelection = currentScreen,
                                onBottomNavChange = { viewModel.navigateTo(it) }
                            )
                        }
                        
                        // Single Player cooking simulation and results card screens
                        "single_player" -> SinglePlayerScreen(viewModel = viewModel)
                        "cooking" -> CookingScreen(viewModel = viewModel)
                        "result" -> ResultScreen(viewModel = viewModel)
                        
                        // Online multiplayer matches lobby and Co-op rooms
                        "online" -> OnlineScreen(viewModel = viewModel)
                        "co_op" -> CoOpScreen(viewModel = viewModel)
                        "party" -> PartyScreen(viewModel = viewModel)
                        "chat_screen" -> ChatScreen(viewModel = viewModel)
                        
                        // Restaurant decor upgrades and Gacha pool galls
                        "restaurant" -> RestaurantScreen(viewModel = viewModel)
                        "gacha" -> GachaScreen(viewModel = viewModel)
                        
                        else -> SplashScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
