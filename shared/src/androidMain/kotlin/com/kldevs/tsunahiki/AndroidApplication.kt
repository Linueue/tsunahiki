package com.kldevs.tsunahiki

import android.app.Application
import com.kldevs.tsunahiki.audio.AudioEngine
import com.kldevs.tsunahiki.audio.initKoin
import com.kldevs.tsunahiki.game.GameSettings
import com.revenuecat.purchases.kmp.LogLevel
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesConfiguration
import com.russhwolf.settings.BuildConfig
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext

class AndroidApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@AndroidApplication)
        }

        val settings = getKoin().get<GameSettings>()
        val audio = getKoin().get<AudioEngine>()
        audio.setMusicVolume(settings.musicVolume)
        audio.setSfxVolume(settings.sfxVolume)

        // Enable debug logs in debug builds
        Purchases.logLevel = if (BuildConfig.DEBUG) LogLevel.DEBUG else LogLevel.INFO

        // Initialize RevenueCat with your Android API key from the dashboard
        Purchases.configure(
            PurchasesConfiguration.Builder(
                apiKey = BuildKonfig.REVENUECAT_API_KEY,
            ).build()
        )
    }
}