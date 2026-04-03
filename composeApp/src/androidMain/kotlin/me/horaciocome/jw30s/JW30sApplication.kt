package me.horaciocome.jw30s

import android.app.Application
import me.horaciocome.jw30s.di.appModule
import me.horaciocome.jw30s.di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class JW30sApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@JW30sApplication)
            modules(platformModule, appModule)
        }
    }
}
