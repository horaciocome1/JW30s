package me.horacioco.jw30s.navigation

import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
data class Game(
    val numberOfTeams: Int,
    val roundDurationSeconds: Int,
)
