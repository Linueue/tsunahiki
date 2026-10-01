package com.kldevs.tsunahiki.audio

import android.content.Context
import com.kldevs.tsunahiki.game.GameSettings
import com.kldevs.tsunahiki.game.PlayerRepository
import com.kldevs.tsunahiki.purchases.CoinStoreViewModel
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

actual val targetModule = module {
    single<AudioEngine> { AudioEngine(context = androidContext())  }

    single<Settings> {
        SharedPreferencesSettings(
            androidContext().getSharedPreferences("game_settings", Context.MODE_PRIVATE)
        )
    }

    single { FileSystem.SYSTEM }
    single { androidContext().filesDir.toOkioPath() }
    single { GameSettings(get()) }
    single { PlayerRepository(get(), get()) }
    viewModelOf(::CoinStoreViewModel)
}