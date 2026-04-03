package me.horaciocome.jw30s

import androidx.compose.ui.window.ComposeUIViewController
import me.horaciocome.jw30s.di.appModule
import me.horaciocome.jw30s.di.platformModule
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        startKoin {
            modules(platformModule, appModule)
        }
    },
) {
    App()
}
