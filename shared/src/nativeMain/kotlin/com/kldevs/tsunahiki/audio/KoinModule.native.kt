package com.kldevs.tsunahiki.audio

import org.koin.core.module.Module
import org.koin.dsl.module

actual val targetModule = module {
    single<AudioEngine> { AudioEngine() }
}