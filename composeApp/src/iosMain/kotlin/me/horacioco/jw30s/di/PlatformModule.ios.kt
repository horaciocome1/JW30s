package me.horacioco.jw30s.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import me.horacioco.jw30s.GameFeedback
import me.horacioco.jw30s.IOSGameFeedback
import me.horacioco.jw30s.data.AppDatabase
import me.horacioco.jw30s.data.DATABASE_NAME
import me.horacioco.jw30s.data.LanguagePersistence
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual val platformModule: Module = module {

    single<AppDatabase> {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )!!
        val dbFilePath = "${documentDirectory.path}/$DATABASE_NAME"
        Room.databaseBuilder<AppDatabase>(
            name = dbFilePath,
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    single<GameFeedback> { IOSGameFeedback() }

    single<DataStore<Preferences>> {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )!!
        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                "${documentDirectory.path}/jw30s_settings.preferences_pb".toPath()
            },
        )
    }

    single<LanguagePersistence> {
        object : LanguagePersistence {
            override fun saveLanguage(languageCode: String) {
                // No-op on iOS: the system locale is used
            }
        }
    }
}
