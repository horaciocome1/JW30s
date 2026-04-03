package me.horacioco.jw30s.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class Card(
    @PrimaryKey val id: String,
    val category: String,
    val hasBeenShown: Boolean = false,
)