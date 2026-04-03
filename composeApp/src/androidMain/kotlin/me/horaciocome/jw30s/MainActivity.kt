package me.horaciocome.jw30s

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App(
                onLanguageChanged = { recreate() },
            )
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences(LANGUAGE_PREFS_NAME, Context.MODE_PRIVATE)
        val languageCode = prefs.getString(LANGUAGE_KEY, "pt") ?: "pt"
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    companion object {
        const val LANGUAGE_PREFS_NAME = "jw30s_language"
        const val LANGUAGE_KEY = "language"
    }
}
