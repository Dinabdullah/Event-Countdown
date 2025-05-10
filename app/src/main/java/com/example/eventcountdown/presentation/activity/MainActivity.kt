package com.example.eventcountdown.presentation.activity

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import androidx.work.WorkManager
import com.example.eventcountdown.data.local.AppDatabase
import com.example.eventcountdown.data.remote.api.RetrofitClient
import com.example.eventcountdown.data.repository.HolidayRepository
import com.example.eventcountdown.presentation.auth.AuthViewModel
import com.example.eventcountdown.presentation.navigation.EventNavigation
import com.example.eventcountdown.presentation.theme.AppSettingsViewModel
import com.example.eventcountdown.presentation.theme.EventCountdownTheme
import com.example.eventcountdown.presentation.theme.SettingsRepository
import com.example.eventcountdown.presentation.theme.ThemePreference
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // تهيئة Firebase
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setProjectId("event-countdown-191f7")
                    .setApplicationId("1:613602753809:android:abf1fa15f0f375f4d620b0")
                    .setApiKey("AIzaSyAlLVMzGH3SX3qBOBAANoJFheGCs3PwVWw")
                    .build()
                FirebaseApp.initializeApp(this, options)
            }
        } catch (e: Exception) {
            Log.e("Firebase", "Error initializing Firebase: ${e.message}")
        }

        // WorkManager
        val workManager = WorkManager.getInstance(this)
        Log.d("NotificationDebug", "WorkManager initialized: $workManager")

        // قاعدة البيانات
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "event_database.db"
        ).fallbackToDestructiveMigration().build()

        // واجهة المستخدم
        setContent {
            val repository = remember { SettingsRepository(applicationContext) }
            val appSettingsViewModel = remember { AppSettingsViewModel(repository) }
            val themePref by appSettingsViewModel.themePreference.collectAsState()
            val languagePref by appSettingsViewModel.languagePreference.collectAsState()

            // Handle language change
            ApplyLanguage(languagePref)

            EventCountdownTheme(themePreference = themePref) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // إنشاء الـ ViewModels
                    val holidayRepository = HolidayRepository(RetrofitClient.api)
                    val eventViewModel = EventViewModel(
                        application = application,
                        eventDao = db.eventDao(),
                        holidayRepository = holidayRepository
                    )
                    val authViewModel = AuthViewModel(application)

                    // التنقل بين الشاشات
                    EventNavigation(
                        authViewModel = authViewModel,
                        eventViewModel = eventViewModel,
                        appSettingsViewModel = appSettingsViewModel
                    )
                }
            }
        }

        // الصلاحيات والقناة الخاصة بالإشعارات
        requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "event_countdown_channel",
                "Event Countdown Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for when events are happening"
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getCurrentLanguage(): String {
        val locale = resources.configuration.locales.get(0)
        return if (locale.language == "ar") "العربيه" else "English"
    }

    private fun setLocale(language: String) {
        val locale = when (language) {
            "العربيه" -> Locale("ar")
            else -> Locale("en")
        }
        Locale.setDefault(locale)
        val config = resources.configuration.apply {
            setLocale(locale)
        }
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    @SuppressLint("ContextCastToActivity")
    @Composable
    private fun ApplyLanguage(language: String) {
        val activity = LocalContext.current as? ComponentActivity
        LaunchedEffect(language) {
            if (language != getCurrentLanguage()) {
                setLocale(language)
                activity?.recreate()
            }
        }
    }
}