package me.horaciocome.jw30s

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import me.horaciocome.jw30s.data.SettingsRepository
import me.horaciocome.jw30s.navigation.Game
import me.horaciocome.jw30s.navigation.Home
import me.horaciocome.jw30s.presentation.GameScreen
import me.horaciocome.jw30s.presentation.GameViewModel
import me.horaciocome.jw30s.presentation.HomeScreen
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun App(
    onLanguageChanged: () -> Unit = {},
) {
    MaterialTheme {
        val navController = rememberNavController()
        val settingsRepository: SettingsRepository = koinInject()

        NavHost(
            navController = navController,
            startDestination = Home,
        ) {
            composable<Home> {
                HomeScreen(
                    settingsRepository = settingsRepository,
                    onStartGame = { numberOfTeams, roundDurationSeconds ->
                        navController.navigate(
                            Game(
                                numberOfTeams = numberOfTeams,
                                roundDurationSeconds = roundDurationSeconds,
                            ),
                        ) {
                            launchSingleTop = true
                        }
                    },
                    onLanguageChanged = onLanguageChanged,
                )
            }
            composable<Game> { backStackEntry ->
                val route = backStackEntry.toRoute<Game>()
                val viewModel: GameViewModel = koinViewModel {
                    parametersOf(route.numberOfTeams, route.roundDurationSeconds)
                }
                val feedback: GameFeedback = koinInject()
                GameScreen(
                    viewModel = viewModel,
                    feedback = feedback,
                    onNavigateHome = {
                        navController.popBackStack(Home, inclusive = false)
                    },
                )
            }
        }
    }
}
