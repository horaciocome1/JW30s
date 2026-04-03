package me.horaciocome.jw30s.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jw30s.composeapp.generated.resources.Res
import jw30s.composeapp.generated.resources.card_content_description
import jw30s.composeapp.generated.resources.end_round_early
import jw30s.composeapp.generated.resources.leave
import jw30s.composeapp.generated.resources.leave_game
import jw30s.composeapp.generated.resources.leave_game_content_description
import jw30s.composeapp.generated.resources.leave_game_message
import jw30s.composeapp.generated.resources.next_team
import jw30s.composeapp.generated.resources.no
import jw30s.composeapp.generated.resources.skip
import jw30s.composeapp.generated.resources.start_round
import jw30s.composeapp.generated.resources.stay
import jw30s.composeapp.generated.resources.swipe_to_review
import jw30s.composeapp.generated.resources.team_get_ready
import jw30s.composeapp.generated.resources.team_label
import jw30s.composeapp.generated.resources.team_playing
import jw30s.composeapp.generated.resources.times_up
import me.horaciocome.jw30s.GameFeedback
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    feedback: GameFeedback,
    onNavigateHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }
    var showSkipPopup by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(
        initialPage = uiState.currentPageIndex,
        pageCount = { uiState.shownCards.size },
    )

    LaunchedEffect(uiState.currentPageIndex) {
        pagerState.animateScrollToPage(uiState.currentPageIndex)
    }

    LaunchedEffect(pagerState, uiState.isRoundActive, uiState.currentPageIndex) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            if (uiState.isRoundActive) {
                if (page != uiState.currentPageIndex) {
                    pagerState.animateScrollToPage(uiState.currentPageIndex)
                }
            } else {
                if (page > uiState.currentPageIndex) {
                    pagerState.animateScrollToPage(uiState.currentPageIndex)
                }
            }
        }
    }

    // Trigger vibration and sound when round ends
    LaunchedEffect(uiState.isRoundOver) {
        if (uiState.isRoundOver) {
            showSkipPopup = false
            feedback.vibrate()
            feedback.playTimerEndSound()
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // Exit confirmation dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(stringResource(Res.string.leave_game)) },
            text = { Text(stringResource(Res.string.leave_game_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        onNavigateHome()
                    },
                ) {
                    Text(stringResource(Res.string.leave), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(stringResource(Res.string.stay))
                }
            },
        )
    }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        if (uiState.shownCards.isNotEmpty()) {
            Image(
                painter = painterResource(uiState.shownCards[uiState.currentPageIndex]),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(25.dp),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
            )
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        TeamIndicator(
                            currentTeam = uiState.currentTeam,
                            numberOfTeams = uiState.numberOfTeams,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { showExitDialog = true }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(Res.string.leave_game_content_description),
                                tint = Color.White,
                            )
                        }
                    },
                    actions = {
                        Box(Modifier.padding(end = 8.dp)) {
                            TimerDisplay(
                                seconds = uiState.timerSeconds,
                                isRoundOver = uiState.isRoundOver,
                                isWaitingToStart = uiState.isWaitingToStart,
                                onClick = {
                                    if (uiState.isRoundActive) {
                                        showSkipPopup = !showSkipPopup
                                    }
                                },
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                    ),
                )
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    when {
                        uiState.isWaitingToStart -> {
                            Text(
                                text = stringResource(
                                    Res.string.team_get_ready,
                                    uiState.currentTeam.toString()
                                ),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.8f),
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.startRound() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                ),
                                shape = MaterialTheme.shapes.large,
                            ) {
                                Text(
                                    text = stringResource(Res.string.start_round),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }

                        uiState.isRoundActive -> {
                            Text(
                                text = stringResource(
                                    Res.string.team_playing,
                                    uiState.currentTeam.toString()
                                ),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.7f),
                            )
                        }

                        uiState.isRoundOver -> {
                            Text(
                                text = stringResource(Res.string.times_up),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(Res.string.swipe_to_review),
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.5f),
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            FloatingActionButton(
                                onClick = { viewModel.shuffleNextCard() },
                                containerColor = MaterialTheme.colorScheme.primary,
                                shape = CircleShape,
                                modifier = Modifier.size(64.dp),
                            ) {
                                Text(
                                    text = "\uD83D\uDD00",
                                    fontSize = 24.sp,
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(
                                    Res.string.next_team,
                                    viewModel.nextTeamLabel().toString()
                                ),
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.6f),
                            )
                        }
                    }
                }
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                // Card pager
                if (uiState.shownCards.isNotEmpty()) {
                    VerticalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        userScrollEnabled = !uiState.isRoundActive && uiState.isRoundOver,
                        beyondViewportPageCount = 1,
                    ) { page ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                painter = painterResource(uiState.shownCards[page]),
                                contentDescription = stringResource(
                                    Res.string.card_content_description,
                                    (page + 1).toString()
                                ),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit,
                            )
                        }
                    }
                }


                if (showSkipPopup && uiState.isRoundActive) {
                    SkipRoundPopup(
                        onSkip = {
                            showSkipPopup = false
                            viewModel.skipRound()
                        },
                        onDismiss = { showSkipPopup = false },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SkipRoundPopup(
    onSkip: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(top = 48.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(12.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(Res.string.end_round_early),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(Res.string.no), fontSize = 13.sp)
                }
                Button(
                    onClick = onSkip,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                    ),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(stringResource(Res.string.skip), fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun TeamIndicator(
    currentTeam: Int,
    numberOfTeams: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(
                color = Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp),
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (team in 1..numberOfTeams) {
            val isActive = team == currentTeam
            Box(
                modifier = Modifier
                    .size(if (isActive) 10.dp else 8.dp)
                    .background(
                        color = if (isActive) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.White.copy(alpha = 0.3f)
                        },
                        shape = CircleShape,
                    ),
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(Res.string.team_label, currentTeam.toString()),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
        )
    }
}

@Composable
private fun TimerDisplay(
    seconds: Int,
    isRoundOver: Boolean,
    isWaitingToStart: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val timerColor by animateColorAsState(
        targetValue = when {
            isRoundOver -> MaterialTheme.colorScheme.error
            isWaitingToStart -> Color.White.copy(alpha = 0.5f)
            seconds <= 10 -> Color(0xFFFF6B00)
            else -> MaterialTheme.colorScheme.primary
        },
    )

    val minutes = seconds / 60
    val secs = seconds % 60
    val timeText = "$minutes:${secs.toString().padStart(2, '0')}"

    Box(
        modifier = modifier
            .background(
                color = timerColor.copy(alpha = 0.15f),
                shape = MaterialTheme.shapes.medium,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (isRoundOver) "0:00" else timeText,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = timerColor,
        )
    }
}
