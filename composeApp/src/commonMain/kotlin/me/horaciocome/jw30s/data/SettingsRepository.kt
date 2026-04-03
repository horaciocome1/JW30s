package me.horaciocome.jw30s.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(
    private val dataStore: DataStore<Preferences>,
    private val languagePersistence: LanguagePersistence,
) {

    val language: Flow<String> = dataStore.data.map { preferences ->
        preferences[KEY_LANGUAGE] ?: DEFAULT_LANGUAGE
    }

    val defaultNumberOfTeams: Flow<Int> = dataStore.data.map { preferences ->
        preferences[KEY_DEFAULT_TEAMS] ?: DEFAULT_NUMBER_OF_TEAMS
    }

    val defaultRoundDurationSeconds: Flow<Int> = dataStore.data.map { preferences ->
        preferences[KEY_DEFAULT_ROUND_DURATION] ?: DEFAULT_ROUND_DURATION_SECONDS
    }

    suspend fun setLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[KEY_LANGUAGE] = language
        }
        languagePersistence.saveLanguage(language)
    }

    suspend fun setDefaultNumberOfTeams(numberOfTeams: Int) {
        dataStore.edit { preferences ->
            preferences[KEY_DEFAULT_TEAMS] = numberOfTeams
        }
    }

    suspend fun setDefaultRoundDurationSeconds(durationSeconds: Int) {
        dataStore.edit { preferences ->
            preferences[KEY_DEFAULT_ROUND_DURATION] = durationSeconds
        }
    }

    companion object {
        private val KEY_LANGUAGE = stringPreferencesKey("language")
        private val KEY_DEFAULT_TEAMS = intPreferencesKey("default_teams")
        private val KEY_DEFAULT_ROUND_DURATION = intPreferencesKey("default_round_duration")

        const val DEFAULT_LANGUAGE = "pt"
        const val DEFAULT_NUMBER_OF_TEAMS = 2
        const val DEFAULT_ROUND_DURATION_SECONDS = 30
    }
}
