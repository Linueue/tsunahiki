package com.kldevs.tsunahiki.audio

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val targetModule = module {
    single<AudioEngine> { AudioEngine(context = androidContext())  }
}