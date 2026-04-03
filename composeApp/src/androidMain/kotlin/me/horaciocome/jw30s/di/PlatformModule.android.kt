package me.horaciocome.jw30s.di

import android.content.Context
import me.horaciocome.jw30s.AndroidGameFeedback
import me.horaciocome.jw30s.GameFeedback
import me.horaciocome.jw30s.MainActivity
import me.horaciocome.jw30s.data.AppDatabase
import me.horaciocome.jw30s.data.DATABASE_NAME
import me.horaciocome.jw30s.data.LanguagePersistence
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import okio.Path.Companion.toPath
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {

    single<AppDatabase> {
        val context = androidContext()
        val dbFile = context.getDatabasePath(DATABASE_NAME)
        Room.databaseBuilder<AppDatabase>(
            context = context,
            name = dbFile.absolutePath,
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    single<GameFeedback> { AndroidGameFeedback(androidContext()) }

    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                androidContext().filesDir.resolve("jw30s_settings.preferences_pb").absolutePath.toPath()
            },
        )
    }

    single<LanguagePersistence> {
        object : LanguagePersistence {
            override fun saveLanguage(languageCode: String) {
                androidContext()
                    .getSharedPreferences(
                        MainActivity.LANGUAGE_PREFS_NAME,
                        Context.MODE_PRIVATE,
                    )
                    .edit()
                    .putString(MainActivity.LANGUAGE_KEY, languageCode)
                    .apply()
            }
        }
    }
}
