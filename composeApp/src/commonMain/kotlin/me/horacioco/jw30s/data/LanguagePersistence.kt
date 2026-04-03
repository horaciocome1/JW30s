package me.horacioco.jw30s.data

/**
 * Platform-specific persistence for language preference.
 * On Android, this writes to SharedPreferences so the locale can be
 * read synchronously in Activity.attachBaseContext().
 * On iOS, this is a no-op (system locale is used).
 */
interface LanguagePersistence {
    fun saveLanguage(languageCode: String)
}
