package me.horaciocome.jw30s.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jw30s.composeapp.generated.resources.Res
import jw30s.composeapp.generated.resources.app_description
import jw30s.composeapp.generated.resources.app_subtitle
import jw30s.composeapp.generated.resources.app_title
import jw30s.composeapp.generated.resources.leave_game_content_description
import jw30s.composeapp.generated.resources.start_game
import kotlinx.coroutines.launch
import me.horaciocome.jw30s.data.SettingsRepository
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    settingsRepository: SettingsRepository,
    onStartGame: (numberOfTeams: Int, roundDurationSeconds: Int) -> Unit,
    onLanguageChanged: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showSetupSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    val setupSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val defaultTeams by settingsRepository.defaultNumberOfTeams.collectAsState(
        initial = SettingsRepository.DEFAULT_NUMBER_OF_TEAMS,
    )
    val defaultDuration by settingsRepository.defaultRoundDurationSeconds.collectAsState(
        initial = SettingsRepository.DEFAULT_ROUND_DURATION_SECONDS,
    )



    // Game setup bottom sheet
    if (showSetupSheet) {
        GameSetupBottomSheet(
            sheetState = setupSheetState,
            defaultNumberOfTeams = defaultTeams,
            defaultRoundDurationSeconds = defaultDuration,
            onStartGame = { teams, duration ->
                scope.launch {
                    setupSheetState.hide()
                    showSetupSheet = false
                    onStartGame(teams, duration)
                }
            },
            onDismiss = { showSetupSheet = false },
        )
    }

    // Settings bottom sheet
    if (showSettingsSheet) {
        SettingsBottomSheet(
            sheetState = settingsSheetState,
            settingsRepository = settingsRepository,
            onDismiss = { showSettingsSheet = false },
            onLanguageChanged = onLanguageChanged,
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                },
                actions = {
                    IconButton(
                        onClick = { showSettingsSheet = true },
                    ) {
                        Text(
                            text = "\u2699",
                            fontSize = 28.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        )
                    }
                },
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .safeContentPadding(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(Res.string.app_title),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(Res.string.app_subtitle),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                )

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = stringResource(Res.string.app_description),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(64.dp))

                Button(
                    onClick = { showSetupSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Text(
                        text = stringResource(Res.string.start_game),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}