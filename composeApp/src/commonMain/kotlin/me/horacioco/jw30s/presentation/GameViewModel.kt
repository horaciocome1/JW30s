package me.horacioco.jw30s.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.horacioco.jw30s.data.CardRepository
import me.horacioco.jw30s.domain.CardResourceMapper
import org.jetbrains.compose.resources.DrawableResource

data class GameUiState(
    val shownCards: List<DrawableResource> = emptyList(),
    val currentPageIndex: Int = 0,
    val timerSeconds: Int = 30,
    val isRoundActive: Boolean = false,
    val isRoundOver: Boolean = false,
    val isWaitingToStart: Boolean = true,
    val isLoading: Boolean = true,
    val currentTeam: Int = 1,
    val numberOfTeams: Int = 2,
    val roundDurationSeconds: Int = 30,
    val roundNumber: Int = 0,
)

class GameViewModel(
    private val repository: CardRepository,
    private val numberOfTeams: Int,
    private val roundDurationSeconds: Int,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        GameUiState(
            numberOfTeams = numberOfTeams,
            roundDurationSeconds = roundDurationSeconds,
            timerSeconds = roundDurationSeconds,
        ),
    )
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            repository.initializeCards()
            loadNextCard()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isWaitingToStart = true,
                    currentTeam = 1,
                    roundNumber = 1,
                )
            }
        }
    }

    fun startRound() {
        if (_uiState.value.isRoundActive) return
        _uiState.update {
            it.copy(
                isWaitingToStart = false,
                isRoundActive = true,
                isRoundOver = false,
                timerSeconds = roundDurationSeconds,
            )
        }
        startTimer()
    }

    fun shuffleNextCard() {
        val state = _uiState.value
        if (state.isRoundActive || state.isWaitingToStart) return
        if (!state.isRoundOver) return

        viewModelScope.launch {
            loadNextCard()
            val nextTeam = (state.currentTeam % state.numberOfTeams) + 1
            _uiState.update {
                it.copy(
                    isRoundOver = false,
                    isWaitingToStart = true,
                    currentTeam = nextTeam,
                    roundNumber = it.roundNumber + 1,
                )
            }
        }
    }

    private suspend fun loadNextCard() {
        val card = repository.getNextCard() ?: return
        repository.markCardShown(card.id)
        val drawable = CardResourceMapper.getDrawableResource(card.id) ?: return

        _uiState.update { state ->
            val newCards = state.shownCards + drawable
            state.copy(
                shownCards = newCards,
                currentPageIndex = newCards.size - 1,
            )
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timerSeconds > 0) {
                delay(1000)
                _uiState.update { state ->
                    val newSeconds = state.timerSeconds - 1
                    if (newSeconds <= 0) {
                        state.copy(
                            timerSeconds = 0,
                            isRoundActive = false,
                            isRoundOver = true,
                        )
                    } else {
                        state.copy(timerSeconds = newSeconds)
                    }
                }
            }
        }
    }

    fun skipRound() {
        if (!_uiState.value.isRoundActive) return
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                timerSeconds = 0,
                isRoundActive = false,
                isRoundOver = true,
            )
        }
    }

    fun nextTeamLabel(): Int {
        val state = _uiState.value
        return (state.currentTeam % state.numberOfTeams) + 1
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
