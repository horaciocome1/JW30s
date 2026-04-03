package me.horacioco.jw30s.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jw30s.composeapp.generated.resources.Res
import jw30s.composeapp.generated.resources.game_setup
import jw30s.composeapp.generated.resources.number_of_teams
import jw30s.composeapp.generated.resources.round_duration
import jw30s.composeapp.generated.resources.seconds_format
import jw30s.composeapp.generated.resources.start_game
import jw30s.composeapp.generated.resources.teams_format
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSetupBottomSheet(
    sheetState: SheetState,
    defaultNumberOfTeams: Int,
    defaultRoundDurationSeconds: Int,
    onStartGame: (numberOfTeams: Int, roundDurationSeconds: Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    key(defaultNumberOfTeams, defaultRoundDurationSeconds) {
        var numberOfTeams by remember { mutableIntStateOf(defaultNumberOfTeams) }
        var roundDuration by remember { mutableIntStateOf(defaultRoundDurationSeconds) }

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            modifier = modifier,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.game_setup),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Number of teams selector
                Text(
                    text = stringResource(Res.string.number_of_teams),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                NumberSelector(
                    value = numberOfTeams,
                    onValueChange = { numberOfTeams = it },
                    range = 2..8,
                    label = { stringResource(Res.string.teams_format, it.toString()) },
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Round duration selector
                Text(
                    text = stringResource(Res.string.round_duration),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                NumberSelector(
                    value = roundDuration,
                    onValueChange = { roundDuration = it },
                    range = 15..120,
                    step = 15,
                    label = { stringResource(Res.string.seconds_format, it.toString()) },
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Start game button
                Button(
                    onClick = { onStartGame(numberOfTeams, roundDuration) },
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

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun NumberSelector(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    step: Int = 1,
    label: @Composable (Int) -> String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(
            onClick = {
                val newValue = value - step
                if (newValue >= range.first) onValueChange(newValue)
            },
            enabled = value - step >= range.first,
        ) {
            Text(
                text = "-",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.width(24.dp))

        Text(
            text = label(value),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.width(24.dp))

        TextButton(
            onClick = {
                val newValue = value + step
                if (newValue <= range.last) onValueChange(newValue)
            },
            enabled = value + step <= range.last,
        ) {
            Text(
                text = "+",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
