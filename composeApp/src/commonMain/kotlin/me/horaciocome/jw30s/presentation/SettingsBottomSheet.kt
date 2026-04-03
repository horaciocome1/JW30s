package me.horaciocome.jw30s.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jw30s.composeapp.generated.resources.Res
import jw30s.composeapp.generated.resources.cards_in_portuguese
import jw30s.composeapp.generated.resources.default_round_duration
import jw30s.composeapp.generated.resources.default_teams
import jw30s.composeapp.generated.resources.english
import jw30s.composeapp.generated.resources.language
import jw30s.composeapp.generated.resources.portuguese
import jw30s.composeapp.generated.resources.seconds_format
import jw30s.composeapp.generated.resources.settings
import jw30s.composeapp.generated.resources.teams_format
import kotlinx.coroutines.launch
import me.horaciocome.jw30s.data.SettingsRepository
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    sheetState: SheetState,
    settingsRepository: SettingsRepository,
    onDismiss: () -> Unit,
    onLanguageChanged: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val language by settingsRepository.language.collectAsState(initial = SettingsRepository.DEFAULT_LANGUAGE)
    val defaultTeams by settingsRepository.defaultNumberOfTeams.collectAsState(initial = SettingsRepository.DEFAULT_NUMBER_OF_TEAMS)
    val defaultDuration by settingsRepository.defaultRoundDurationSeconds.collectAsState(initial = SettingsRepository.DEFAULT_ROUND_DURATION_SECONDS)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            Text(
                text = stringResource(Res.string.settings),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp),
            )

            // Language section
            Text(
                text = stringResource(Res.string.language),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(modifier = Modifier.selectableGroup()) {
                LanguageOption(
                    label = stringResource(Res.string.portuguese),
                    selected = language == "pt",
                    onClick = {
                        if (language != "pt") {
                            scope.launch {
                                settingsRepository.setLanguage("pt")
                                onLanguageChanged()
                            }
                        }
                    },
                )
                LanguageOption(
                    label = stringResource(Res.string.english),
                    description = stringResource(Res.string.cards_in_portuguese),
                    selected = language == "en",
                    onClick = {
                        if (language != "en") {
                            scope.launch {
                                settingsRepository.setLanguage("en")
                                onLanguageChanged()
                            }
                        }
                    },
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // Default teams
            Text(
                text = stringResource(Res.string.default_teams),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = {
                        if (defaultTeams > 2) {
                            scope.launch { settingsRepository.setDefaultNumberOfTeams(defaultTeams - 1) }
                        }
                    },
                    enabled = defaultTeams > 2,
                ) {
                    Text(text = "-", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(24.dp))
                Text(
                    text = stringResource(Res.string.teams_format, defaultTeams.toString()),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.width(24.dp))
                TextButton(
                    onClick = {
                        if (defaultTeams < 8) {
                            scope.launch { settingsRepository.setDefaultNumberOfTeams(defaultTeams + 1) }
                        }
                    },
                    enabled = defaultTeams < 8,
                ) {
                    Text(text = "+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // Default round duration
            Text(
                text = stringResource(Res.string.default_round_duration),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = {
                        if (defaultDuration > 15) {
                            scope.launch { settingsRepository.setDefaultRoundDurationSeconds(defaultDuration - 15) }
                        }
                    },
                    enabled = defaultDuration > 15,
                ) {
                    Text(text = "-", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(24.dp))
                Text(
                    text = stringResource(Res.string.seconds_format, defaultDuration.toString()),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.width(24.dp))
                TextButton(
                    onClick = {
                        if (defaultDuration < 120) {
                            scope.launch { settingsRepository.setDefaultRoundDurationSeconds(defaultDuration + 15) }
                        }
                    },
                    enabled = defaultDuration < 120,
                ) {
                    Text(text = "+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LanguageOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    description: String? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
            )
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
