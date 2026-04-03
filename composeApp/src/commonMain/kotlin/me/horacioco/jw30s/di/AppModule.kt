package me.horacioco.jw30s.di

import me.horacioco.jw30s.data.AppDatabase
import me.horacioco.jw30s.data.CardRepository
import me.horacioco.jw30s.data.SettingsRepository
import me.horacioco.jw30s.presentation.GameViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { get<AppDatabase>().cardDao() }

    single { CardRepository(get()) }

    single { SettingsRepository(get(), get()) }

    viewModel { params ->
        GameViewModel(
            repository = get(),
            numberOfTeams = params.get(),
            roundDurationSeconds = params.get(),
        )
    }
}